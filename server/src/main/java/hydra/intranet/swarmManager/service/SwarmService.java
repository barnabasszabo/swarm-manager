package hydra.intranet.swarmManager.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Service;
import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.Task;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.common.base.Optional;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.service.detector.IDetector;
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

	@PostConstruct
	public void init() {
		final DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("unix:///var/run/docker.sock")
				.withDockerConfig("/home/barnabas/.docker").build();
		client = DockerClientBuilder.getInstance(config).build();
	}

	@Scheduled(fixedDelay = 10000)
	public void scheduler() {
		ecosystems = collectEcosystems();
	}

	public Collection<Ecosystem> getEcosystems() {
		return ecosystems;
	}

	private Collection<Ecosystem> collectEcosystems() {
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

		log.info("Ecosystem collected in {} millisec", (System.currentTimeMillis() - start));
		return ecosystems;
	}

	public Collection<SwarmNode> getNodes() {
		return client.listSwarmNodesCmd().exec();
	}

	private Collection<Ecosystem> collectRawEcosystems() {
		final Collection<Ecosystem> ecosystems = new ArrayList<>();
		final List<Service> rawServices = client.listServicesCmd().exec();
		rawServices.forEach(service -> {
			final Optional<Map<String, String>> maybeLabels = Optional.fromNullable(service.getSpec().getLabels());
			final boolean isStack = maybeLabels.isPresent() && maybeLabels.get().containsKey(DOCKER_STACK_LABEL);
			final String name = isStack ? maybeLabels.get().get(DOCKER_STACK_LABEL) : service.getSpec().getName();
			final List<Task> tasks = client.listTasksCmd().withServiceFilter(service.getSpec().getName()).exec();
			if (isStack) {
				addTaskToEcosystem(ecosystems, name, tasks, maybeLabels);
			} else {
				final Ecosystem eco = Ecosystem.builder().isStack(isStack).name(name).build();
				eco.addLabel(maybeLabels);
				eco.addTasks(tasks);
				ecosystems.add(eco);
			}
		});
		return ecosystems;
	}

	private void addTaskToEcosystem(final Collection<Ecosystem> ecosystems, final String stackName, final List<Task> tasks, final Optional<Map<String, String>> maybeLabels) {
		final List<Ecosystem> ecosystemArray = ecosystems.stream().filter(s -> s.getName().equals(stackName)).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(ecosystemArray)) { // Create new stack
			final Ecosystem eco = Ecosystem.builder().isStack(true).name(stackName).build();
			eco.addLabel(maybeLabels);
			eco.addTasks(tasks);
			ecosystems.add(eco);
		} else { // Add to existing stack
			ecosystemArray.forEach(e -> {
				e.addLabel(maybeLabels);
				e.addTasks(tasks);
			});
		}
	}

}
