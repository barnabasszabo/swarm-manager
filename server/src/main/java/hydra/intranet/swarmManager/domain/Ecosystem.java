package hydra.intranet.swarmManager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.github.dockerjava.api.model.PortConfig;
import com.github.dockerjava.api.model.Service;
import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.Task;
import com.google.common.base.Optional;
import com.google.gerrit.extensions.common.ChangeInfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ecosystem implements Serializable {

	private static final long serialVersionUID = -8335828435691988610L;

	private String name;

	private boolean isStack;

	@Builder.Default
	private EcosystemStatus status = EcosystemStatus.builder().build();

	@Builder.Default
	private SwarmResource usedResource = SwarmResource.builder().build();

	private boolean markedAsRemove;

	@Builder.Default
	private Set<String> markedMessage = new HashSet();

	private boolean isOverloaded;

	@Builder.Default
	private List<SwarmTask> tasks = new ArrayList<>();

	private ChangeInfo review;

	@Builder.Default
	private Set<Pool> pools = new HashSet<>();

	@Builder.Default
	private Map<String, Set<String>> labels = new HashMap<>();

	@Builder.Default
	private Collection<PortConfig> portConfig = new ArrayList<>();

	private Date created;
	private Date updated;

	public Ecosystem addRemoveMessage(String msg) {
		markedAsRemove = true;
		markedMessage.add(msg);
		return this;
	}

	public Ecosystem addTasks(List<Task> tasks, Service service) {
		tasks.forEach(t -> {
			List<PortConfig> ports = Collections.emptyList();
			try {
				ports = Arrays.asList(service.getEndpoint().getPorts());
			} catch (final Exception e) {
			}
			this.tasks.add(SwarmTask.builder().task(t).ports(ports).serviceName(service.getSpec().getName()).build());
		});
		return this;
	}

	public Ecosystem addPorts(Collection<PortConfig> ports) {
		ports.forEach(p -> {
			List<PortConfig> equalPorts = portConfig.stream().filter(port -> port.getPublishedPort() == p.getPublishedPort()).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(equalPorts)) {
				portConfig.add(p);
			}
		});
		return this;
	}

	public Ecosystem addNodes(Map<String, SwarmNode> nodeMap) {
		tasks.forEach(t -> {
			t.setNode(nodeMap.get(t.getTask().getNodeId()));
		});
		return this;
	}

	public Ecosystem addLabel(Optional<Map<String, String>> maybeMap) {
		if (maybeMap.isPresent()) {
			maybeMap.get().entrySet().forEach(e -> {
				this.addLabel(e.getKey(), e.getValue());
			});
		}
		return this;
	}

	public Ecosystem addLabel(String key, String value) {
		if (!labels.containsKey(key)) {
			labels.put(key, new HashSet<String>());
		}
		if (!labels.get(key).contains(value)) {
			labels.get(key).add(value);
		}
		return this;
	}

	public String getLastLabel(String labelKey) {
		String result = null;
		if (labels.containsKey(labelKey)) {
			for (String value : labels.get(labelKey)) {
				result = value;
			}
		}
		return result;
	}
}
