package hydra.intranet.swarmManager.domain;

import java.io.Serializable;

import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.Task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class SwarmTask implements Serializable {

	private static final long serialVersionUID = -3730834092545537070L;

	private SwarmNode node;

	private Task task;

}
