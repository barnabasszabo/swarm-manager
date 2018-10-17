package hydra.intranet.swarmManager.service.task;

import java.util.Collection;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.service.SwarmService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SwarmCollectTask implements Runnable {

	private SwarmService swarmService;

	public SwarmCollectTask(final SwarmService swarmService) {
		this.swarmService = swarmService;
	}

	@Override
	public void run() {
		final Collection<Ecosystem> collectEcosystems = swarmService.collectEcosystems();

		collectEcosystems.stream().filter(eco -> eco.isMarkedAsRemove() && swarmService.isChangeExpire(eco)).forEach(eco -> {
			swarmService.removeEcosystem(eco);
		});
	}

}
