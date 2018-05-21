package hydra.intranet.swarmManager.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.SwarmNodeAvailability;
import com.github.dockerjava.api.model.SwarmNodeState;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.Pool;
import hydra.intranet.swarmManager.domain.PoolResource;
import hydra.intranet.swarmManager.domain.SwarmResource;

@Component
public class ResourceService {

	@Autowired
	private SwarmService swarmService;

	@Autowired
	private PoolService poolService;

	@Autowired
	private ConfigService configService;

	public PoolResource getPoolResource(final String poolId) {
		PoolResource result = PoolResource.builder().build();
		final List<PoolResource> poolResources = getPoolResources(swarmService.getEcosystems()).stream().filter(pr -> pr.getPool().getId().equals(poolId))
				.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(poolResources)) {
			result = poolResources.get(0);
		}
		return result;
	}

	// FIXME Ha nem kötelezőek a pool-ok!
	public List<PoolResource> getPoolResources(final Collection<Ecosystem> ecosystems) {
		final List<PoolResource> result = new ArrayList<>();

		long totalWeight = 0;
		final Collection<Pool> activePools = poolService.getActivePools();
		for (final Pool p : activePools) {
			totalWeight += p.getWeight();
		}
		final SwarmResource wsResources = getWorkspaceSwarmResources();
		final Double weightCpuUnit = (wsResources.getReservedCpu() * 1.0) / totalWeight;
		final Double weightMemoryUnit = (wsResources.getReservedMemory() * 1.0) / totalWeight;

		activePools.forEach(p -> {
			final PoolResource poolResource = PoolResource.builder().pool(p).build();

			// calculate dedicated resources
			poolResource.getDedicated().addReservedCpu(Math.round(p.getWeight() * weightCpuUnit));
			poolResource.getDedicated().addLimitCpu(poolResource.getDedicated().getReservedCpu());
			poolResource.getDedicated().addReservedMemory(Math.round(p.getWeight() * weightMemoryUnit));
			poolResource.getDedicated().addLimitMemory(poolResource.getDedicated().getReservedMemory());

			// calculate used resources
			ecosystems.stream().filter(eco -> eco.getPools().contains(p)).forEach(eco -> {
				poolResource.getUsed().addLimitCpu(eco.getUsedResource().getLimitCpu());
				poolResource.getUsed().addReservedCpu(eco.getUsedResource().getReservedCpu());
				poolResource.getUsed().addLimitMemory(eco.getUsedResource().getLimitMemory());
				poolResource.getUsed().addReservedMemory(eco.getUsedResource().getReservedMemory());
			});

			result.add(poolResource);
		});
		return result;
	}

	public SwarmResource getWorkspaceSwarmResources() {
		final SwarmResource ws = SwarmResource.builder().build();
		final SwarmResource total = getTotalSwarmResources();

		final Long workspaceCpuPercent = 100 - (configService.getLong("SYSMTEM_STANDBY_PERCENT") + configService.getLong("CPU_LIMIT_RESERVED_GAP_IN_PERCENT"));
		final Long workspaceMemoryPercent = 100 - (configService.getLong("SYSMTEM_STANDBY_PERCENT") + configService.getLong("MEMORY_LIMIT_RESERVED_GAP_IN_PERCENT"));

		ws.addLimitCpu((total.getLimitCpu() / 100) * workspaceCpuPercent);
		ws.addReservedCpu(ws.getLimitCpu());

		ws.addLimitMemory((total.getLimitMemory() / 100) * workspaceMemoryPercent);
		ws.addReservedMemory(ws.getLimitMemory());

		return ws;
	}

	private SwarmResource getTotalSwarmResources() {
		final SwarmResource total = SwarmResource.builder().build();
		final Collection<SwarmNode> nodes = swarmService.getNodes();
		nodes.stream().filter(n -> SwarmNodeAvailability.ACTIVE.equals(n.getSpec().getAvailability()) && SwarmNodeState.READY.equals(n.getStatus().getState())).forEach(n -> {
			total.addLimitCpu(n.getDescription().getResources().getNanoCPUs());
			total.addReservedCpu(n.getDescription().getResources().getNanoCPUs());
			total.addLimitMemory(n.getDescription().getResources().getMemoryBytes());
			total.addReservedMemory(n.getDescription().getResources().getMemoryBytes());
		});
		return total;
	}

}
