package hydra.intranet.swarmManager.service.validator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.model.SwarmNode;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.service.SwarmService;

@Component
public class NodeValidator extends AbstractValidator {

	@Autowired
	private SwarmService swarmService;

	@Override
	public void calculate(final Ecosystem eco) {
		final Map<String, SwarmNode> nodeMap = new HashMap<>();
		swarmService.getNodes().forEach(node -> {
			nodeMap.put(node.getId(), node);
		});
		eco.addNodes(nodeMap);
	}

}
