package hydra.intranet.swarmManager.service.detector;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.PoolResource;
import hydra.intranet.swarmManager.domain.SwarmResource;
import hydra.intranet.swarmManager.service.ResourceService;

@Component
public class OverloadDetector extends AbstractDetector {

	@Autowired
	private ResourceService resourceService;

	@Override
	public void detect(final Collection<Ecosystem> ecosystems) {
		final List<PoolResource> poolResources = resourceService.getPoolResources(ecosystems);
		calculatedOverloadedEcosystems(ecosystems, poolResources);
		final ResourceTmp usedResources = calculateDedicatedResources(ecosystems);
		final SwarmResource swarmResource = resourceService.getWorkspaceSwarmResources();
		final ResourceTmp diff = new ResourceTmp();
		diff.cpu = usedResources.cpu - swarmResource.getReservedCpu();
		diff.memory = usedResources.memory - swarmResource.getReservedMemory();
		poolResources.sort((p1, p2) -> p2.getOverloadedEcosystems().size() - p1.getOverloadedEcosystems().size());
		poolResources.forEach(p -> {
			p.getOverloadedEcosystems().forEach(eco -> {
				if (isOver(diff.cpu, eco.getUsedResource().getReservedCpu()) || isOver(diff.memory, eco.getUsedResource().getReservedMemory())) {
					eco.addRemoveMessage("Ecosystem is over the dedicated workspace");
					diff.cpu -= eco.getUsedResource().getReservedCpu();
					diff.memory -= eco.getUsedResource().getReservedMemory();
				}
			});
		});
	}

	private void calculatedOverloadedEcosystems(final Collection<Ecosystem> ecosystems, final Collection<PoolResource> poolResources) {
		poolResources.forEach(poolResource -> {
			final ResourceTmp diff = new ResourceTmp();
			diff.cpu = poolResource.getDedicated().getReservedCpu() - poolResource.getUsed().getReservedCpu();
			diff.memory = poolResource.getDedicated().getReservedMemory() - poolResource.getUsed().getReservedMemory();
			ecosystems.stream().filter(eco -> eco.getPools().contains(poolResource.getPool())).sorted((e1, e2) -> {
				Integer prio1 = 0;
				Integer prio2 = 0;
				if (e1.getLabels().containsKey(configService.getString("PRIORITY_LABEL"))) {
					prio1 = new Integer(e1.getLabels().get(configService.getString("PRIORITY_LABEL")).get(0));
				}
				if (e2.getLabels().containsKey(configService.getString("PRIORITY_LABEL"))) {
					prio2 = new Integer(e2.getLabels().get(configService.getString("PRIORITY_LABEL")).get(0));
				}
				return prio2 - prio1;
			}).forEach(eco -> {
				if (isOver(diff.cpu, eco.getUsedResource().getReservedCpu()) || isOver(diff.memory, eco.getUsedResource().getReservedMemory())) {
					diff.cpu -= eco.getUsedResource().getReservedCpu();
					diff.memory -= eco.getUsedResource().getReservedMemory();
					poolResource.getOverloadedEcosystems().add(eco);
					eco.setOverloaded(true);
				}
			});
		});
	}

	private ResourceTmp calculateDedicatedResources(final Collection<Ecosystem> ecosystems) {
		final ResourceTmp res = new ResourceTmp();
		ecosystems.forEach(eco -> {
			res.cpu += eco.getUsedResource().getReservedCpu();
			res.memory += eco.getUsedResource().getReservedMemory();
		});
		return res;
	}

	private boolean isOver(final long diff, final long used) {
		return (diff > 0) && ((diff - used) > 0);
	}

}

class ResourceTmp {
	Long cpu;
	Long memory;
}