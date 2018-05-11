package hydra.intranet.swarmManager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private List<String> markedMessage = new ArrayList();
	
	private boolean isOverloaded;

	@Builder.Default
	private List<SwarmTask> tasks = new ArrayList<>();

	private ChangeInfo review;

	@Builder.Default
	private Set<Pool> pools = new HashSet<>();

	@Builder.Default
	private Map<String, List<String>> labels = new HashMap<>();

	public Ecosystem addRemoveMessage(String msg) {
		markedAsRemove = true;
		markedMessage.add(msg);
		return this;
	}

	public Ecosystem addTasks(List<Task> tasks) {
		tasks.forEach(t -> {
			this.tasks.add(SwarmTask.builder().task(t).build());
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
			labels.put(key, new ArrayList<>());
		}
		if (!labels.get(key).contains(value)) {
			labels.get(key).add(value);
		}
		return this;
	}
}
