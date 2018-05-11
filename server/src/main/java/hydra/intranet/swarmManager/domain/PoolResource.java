package hydra.intranet.swarmManager.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PoolResource {

	private Pool pool;

	@Builder.Default
	private SwarmResource dedicated = SwarmResource.builder().build();

	@Builder.Default
	private SwarmResource used = SwarmResource.builder().build();

	@Builder.Default
	private List<Ecosystem> overloadedEcosystems = new ArrayList<>();

}
