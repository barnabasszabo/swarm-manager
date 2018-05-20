package hydra.intranet.swarmManager.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.Pool;
import hydra.intranet.swarmManager.service.PoolService;
import hydra.intranet.swarmManager.service.SwarmService;

@RestController
public class PoolController {

	@Autowired
	private PoolService poolService;

	@Autowired
	private SwarmService swarmService;

	@GetMapping("/pool")
	public Collection<Pool> getPoolList() {
		return poolService.getActivePools();
	}

	@GetMapping("/pool/{id}/ecosystem")
	public Collection<Ecosystem> getEcosystemList(@PathVariable("id") final String poolId) {
		return swarmService.getEcosystems(poolId);
	}

}
