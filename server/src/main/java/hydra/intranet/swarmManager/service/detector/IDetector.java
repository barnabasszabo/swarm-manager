package hydra.intranet.swarmManager.service.detector;

import java.util.Collection;

import hydra.intranet.swarmManager.domain.Ecosystem;

public interface IDetector {

	void detect(Collection<Ecosystem> ecosystems);
}
