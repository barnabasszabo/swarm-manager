package hydra.intranet.swarmManager.controller;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
		PoolResource result = PoolResource.builder().build();
		if (!StringUtils.isEmpty(poolId)) {
			if ("all".equals(poolId)) {
				final PoolResource resultTmp = PoolResource.builder().build();
				resourceService.getPoolResources(swarmService.getEcosystems()).forEach(pr -> {
					// Dedicated
					resultTmp.getDedicated().addLimitCpu(pr.getDedicated().getLimitCpu());
					resultTmp.getDedicated().addReservedCpu(pr.getDedicated().getReservedCpu());
					resultTmp.getDedicated().addLimitMemory(pr.getDedicated().getLimitMemory());
					resultTmp.getDedicated().addReservedMemory(pr.getDedicated().getReservedMemory());

					// Used
					resultTmp.getUsed().addLimitCpu(pr.getUsed().getLimitCpu());
					resultTmp.getUsed().addReservedCpu(pr.getUsed().getReservedCpu());
					resultTmp.getUsed().addLimitMemory(pr.getUsed().getLimitMemory());
					resultTmp.getUsed().addReservedMemory(pr.getUsed().getReservedMemory());
				});
				result = resultTmp;
			} else {
				result = resourceService.getPoolResource(poolId);
			}
		}
		return result;
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
		Collection<Ecosystem> result = Collections.emptyList();
		if (!StringUtils.isEmpty(poolId)) {
			if ("all".equals(poolId)) {
				result = swarmService.getEcosystems();
			} else {
				result = swarmService.getEcosystems(poolId);
			}
		}
		return result;
	}

}
