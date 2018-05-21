package hydra.intranet.swarmManager.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.LinkGroup;
import hydra.intranet.swarmManager.domain.Pool;
import hydra.intranet.swarmManager.domain.PoolResource;
import hydra.intranet.swarmManager.service.PoolService;
import hydra.intranet.swarmManager.service.ResourceService;
import hydra.intranet.swarmManager.service.SwarmService;

@RestController
public class PoolController extends BaseController {

	@Autowired
	private PoolService poolService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private SwarmService swarmService;

	@GetMapping("/pool")
	public Collection<Pool> getPoolList() {
		return poolService.getActivePools();
	}

	@GetMapping("/pool/{id}")
	public Pool getPool(@PathVariable("id") final String poolId) {
		final Optional<Pool> maybePool = poolService.getPool(poolId);
		return maybePool.or(Pool.builder().build());
	}

	@GetMapping("/pool/{id}/resource")
	public PoolResource getPoolResource(@PathVariable("id") final String poolId) {
		return resourceService.getPoolResource(poolId);
	}

	@GetMapping("/pool/{id}/link")
	public Collection<LinkGroup> getPoolLink(@PathVariable("id") final String poolId) {
		return poolService.getLinkGroups(poolId);
	}

	@PostMapping("/pool/{id}/link")
	public Collection<LinkGroup> setPoolLink(@PathVariable("id") final String poolId, @RequestBody final Collection<LinkGroup> links) {
		return poolService.saveLinkGroups(poolId, links);
	}

	@GetMapping("/pool/{id}/ecosystem")
	public Collection<Ecosystem> getEcosystemList(@PathVariable("id") final String poolId) {
		return swarmService.getEcosystems(poolId);
	}

}
