package hydra.intranet.swarmManager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.PortConfig;
import com.github.dockerjava.api.model.Service;
import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.Task;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.common.base.Optional;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.Pool;
import hydra.intranet.swarmManager.event.EcosystemRemoved;
import hydra.intranet.swarmManager.service.detector.IDetector;
import hydra.intranet.swarmManager.service.task.SwarmCollectTask;
import hydra.intranet.swarmManager.service.validator.IEcosystemValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SwarmService {

	private static final String DOCKER_STACK_LABEL = "com.docker.stack.namespace";

	private DockerClient client;

	@Autowired
	private Collection<IEcosystemValidator> ecosystemValidators;

	@Autowired
	private Collection<IDetector> detectors;

	private Collection<Ecosystem> ecosystems = new ArrayList<>();

	@Autowired
	private ConfigService configService;

	@Autowired
	private PoolService poolService;

	@Autowired
	private ExecService execService;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	private Map<String, Integer> lastChanceMap = new HashMap<>();

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		final DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost(configService.getString("DOCKER_HOST")).build();
		client = DockerClientBuilder.getInstance(config).build();

		final Trigger nextTrigger = triggerContext -> {
			final Calendar nextExecutionTime = new GregorianCalendar();
			final Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
			nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
			nextExecutionTime.add(Calendar.SECOND, Math.toIntExact(configService.getLong("CHECK_FIXED_DELAY_IN_SEC")));
			return nextExecutionTime.getTime();
		};
		taskScheduler.schedule(new SwarmCollectTask(this), nextTrigger);
	}

	public Collection<Ecosystem> getEcosystems() {
		return ecosystems;
	}

	public Collection<Ecosystem> getEcosystems(final String poolId) {
		final Collection<Ecosystem> ecosystems = new ArrayList<>();
		final Optional<Pool> maybePool = poolService.getPool(poolId);
		if (maybePool.isPresent()) {
			final Collection<Ecosystem> ecoList = new ArrayList<>(getEcosystems(maybePool.get()));
			ecosystems.addAll(ecoList);
		}
		return ecosystems;
	}

	public Collection<Ecosystem> getEcosystems(final Pool pool) {
		return getEcosystems().stream().filter(e -> e.getPools().contains(pool)).collect(Collectors.toList());
	}

	public Collection<Ecosystem> collectEcosystems() {
		final long start = System.currentTimeMillis();
		final Collection<Ecosystem> ecosystems = collectRawEcosystems();

		ecosystems.forEach(eco -> {
			ecosystemValidators.forEach(calc -> {
				calc.calculate(eco);
			});
		});

		detectors.forEach(d -> {
			d.detect(ecosystems);
		});

		log.debug("Ecosystem collected in {} millisec", (System.currentTimeMillis() - start));

		this.ecosystems = ecosystems;
		return ecosystems;
	}

	public Collection<SwarmNode> getNodes() {
		return client.listSwarmNodesCmd().exec();
	}

	public String getJoinToken() {
		return client.joinSwarmCmd().getJoinToken();
	}

	public void removeEcosystem(final Ecosystem ecosystem) {
		if (configService.isTrue("EXEC_REMOVE_COMMAND")) {
			final String rmCommand = ecosystem.isStack() ? "docker stack rm " + ecosystem.getName()
					: "docker service rm " + ecosystem.getName();
			try {
				log.info("Remove ecosystem: {}", rmCommand);
				execService.exec(rmCommand);
				applicationEventPublisher.publishEvent(new EcosystemRemoved(this, ecosystem));
			} catch (final Exception e) {
				log.error("Error in remove command", e);
			}
		}
	}

	public boolean isChangeExpire(Ecosystem eco) {
		if (eco.isMarkedAsRemove()) {
			if (lastChanceMap.containsKey(eco.getName())) {
				Integer chanceNum = lastChanceMap.get(eco.getName());
				if (chanceNum >= configService.getLong("REMOVE_CHANCE_NUM")) {
					return true;
				} else {
					lastChanceMap.put(eco.getName(), chanceNum + 1);
				}
			} else {
				lastChanceMap.put(eco.getName(), 1);
			}
		}
		return false;
	}

	@EventListener
	public void ecosystemRemoved(final EcosystemRemoved event) {
		final Ecosystem eco = event.getEcosystem();
		if (lastChanceMap.containsKey(eco.getName())) {
			lastChanceMap.remove(eco.getName());
		}
	}

	private Collection<Ecosystem> collectRawEcosystems() {
		final Collection<Ecosystem> ecosystems = new ArrayList<>();
		try {
			final List<Service> rawServices = client.listServicesCmd().exec();
			rawServices.forEach(service -> {
				final Optional<Map<String, String>> maybeLabels = Optional.fromNullable(service.getSpec().getLabels());
				final boolean isStack = maybeLabels.isPresent() && maybeLabels.get().containsKey(DOCKER_STACK_LABEL);
				final String name = isStack ? maybeLabels.get().get(DOCKER_STACK_LABEL) : service.getSpec().getName();
				final List<Task> tasks = client.listTasksCmd().withServiceFilter(service.getSpec().getName()).exec();
	
				List<PortConfig> ports = Collections.emptyList();
				try {
					ports = Arrays.asList(service.getEndpoint().getPorts());
				} catch (final Exception e) {
				}
	
				if (isStack) {
					addTaskToEcosystem(ecosystems, name, tasks, maybeLabels, ports, service);
				} else {
					final Ecosystem eco = Ecosystem.builder().created(service.getCreatedAt())
							.updated(service.getUpdatedAt()).isStack(isStack).name(name).build();
					eco.addLabel(maybeLabels);
					eco.addTasks(tasks, service);
					eco.addPorts(ports);
					ecosystems.add(eco);
				}
			});
		} catch (Exception e) {
			log.error("Error in ecosystem collection process...", e);
		}
		return ecosystems;
	}

	private void addTaskToEcosystem(final Collection<Ecosystem> ecosystems, final String stackName,
			final List<Task> tasks, final Optional<Map<String, String>> maybeLabels, final List<PortConfig> ports,
			final Service service) {
		final List<Ecosystem> ecosystemArray = ecosystems.stream().filter(s -> s.getName().equals(stackName))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(ecosystemArray)) { // Create new stack
			final Ecosystem eco = Ecosystem.builder().created(service.getCreatedAt()).updated(service.getUpdatedAt())
					.isStack(true).name(stackName).build();
			eco.addLabel(maybeLabels);
			eco.addTasks(tasks, service);
			eco.addPorts(ports);
			ecosystems.add(eco);
		} else { // Add to existing stack
			ecosystemArray.forEach(eco -> {
				eco.addLabel(maybeLabels);
				eco.addTasks(tasks, service);
				eco.addPorts(ports);
			});
		}
	}

}
