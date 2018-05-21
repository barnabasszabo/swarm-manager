package hydra.intranet.swarmManager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.github.dockerjava.api.model.PortConfig;
import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.Task;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SwarmTask implements Serializable {

	private static final long serialVersionUID = -3730834092545537070L;

	private SwarmNode node;

	private Task task;

	private String serviceName;

	@Builder.Default
	private Collection<PortConfig> ports = new ArrayList<>();

}
