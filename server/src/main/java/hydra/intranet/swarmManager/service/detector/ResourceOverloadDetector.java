package hydra.intranet.swarmManager.service.detector;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.PoolResource;
import hydra.intranet.swarmManager.domain.SwarmResource;
import hydra.intranet.swarmManager.service.ResourceService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ResourceOverloadDetector extends AbstractDetector {

	@Autowired
	private ResourceService resourceService;

	@Override
	public void detect(final Collection<Ecosystem> ecosystems) {
		final List<PoolResource> poolResources = resourceService.getPoolResources(ecosystems);
		calculatedOverloadedEcosystems(ecosystems, poolResources);
		final ResourceTmp usedResources = calculateDedicatedResources(ecosystems);
		final SwarmResource swarmResource = resourceService.getWorkspaceSwarmResources();
		final ResourceTmp diff = ResourceTmp.builder().cpu(usedResources.cpu - swarmResource.getReservedCpu()).memory(usedResources.memory - swarmResource.getReservedMemory())
				.build();
		poolResources.sort((p1, p2) -> p2.getOverloadedEcosystems().size() - p1.getOverloadedEcosystems().size());
		poolResources.forEach(p -> {
			p.getOverloadedEcosystems().forEach(eco -> {
				if (isOver("CHECK_CPU_IN_OVERLOAD_DETECTION", diff.cpu, eco.getUsedResource().getReservedCpu())
						|| isOver("CHECK_MEMORY_IN_OVERLOAD_DETECTION", diff.memory, eco.getUsedResource().getReservedMemory())) {
					eco.addRemoveMessage("Ecosystem is over the dedicated workspace");
					diff.cpu -= eco.getUsedResource().getReservedCpu();
					diff.memory -= eco.getUsedResource().getReservedMemory();
				}
			});
		});
	}

	private void calculatedOverloadedEcosystems(final Collection<Ecosystem> ecosystems, final Collection<PoolResource> poolResources) {
		poolResources.forEach(poolResource -> {
			final ResourceTmp diff = ResourceTmp.builder().cpu(poolResource.getUsed().getReservedCpu() - poolResource.getDedicated().getReservedCpu())
					.memory(poolResource.getUsed().getReservedMemory() - poolResource.getDedicated().getReservedMemory()).build();
			ecosystems.stream().filter(eco -> eco.getPools().contains(poolResource.getPool())).sorted((e1, e2) -> {
				final String e1LastLabel = e1.getLastLabel(configService.getString("PRIORITY_LABEL"));
				final String e2LastLabel = e2.getLastLabel(configService.getString("PRIORITY_LABEL"));
				final Integer prio1 = e1LastLabel != null ? new Integer(e1LastLabel) : 0;
				final Integer prio2 = e2LastLabel != null ? new Integer(e2LastLabel) : 0;
				return prio2 - prio1;
			}).forEach(eco -> {
				if (isOver("CHECK_CPU_IN_OVERLOAD_DETECTION", diff.cpu, eco.getUsedResource().getReservedCpu())
						|| isOver("CHECK_MEMORY_IN_OVERLOAD_DETECTION", diff.memory, eco.getUsedResource().getReservedMemory())) {
					diff.cpu -= eco.getUsedResource().getReservedCpu();
					diff.memory -= eco.getUsedResource().getReservedMemory();
					poolResource.getOverloadedEcosystems().add(eco);
					eco.setOverloaded(true);
					log.debug("Ecosystem is overloaded: {}", eco.getName());
				}
			});
		});
	}

	private ResourceTmp calculateDedicatedResources(final Collection<Ecosystem> ecosystems) {
		final ResourceTmp res = ResourceTmp.builder().build();
		ecosystems.forEach(eco -> {
			res.cpu += eco.getUsedResource().getReservedCpu();
			res.memory += eco.getUsedResource().getReservedMemory();
		});
		return res;
	}

	private boolean isOver(final String configKey, final long diff, final long used) {
		return configService.isTrue(configKey) ? (diff > 0) && ((diff - used) > 0) : false;
	}

}
