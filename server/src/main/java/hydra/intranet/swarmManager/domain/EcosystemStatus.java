package hydra.intranet.swarmManager.domain;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EcosystemStatus implements Serializable {

	private static final long serialVersionUID = 625779960734093275L;

	private int runningServices;

	private int totalServices;

	public EcosystemStatus increaseRunningService() {
		runningServices++;
		return this;
	}

	public EcosystemStatus increaseTotalService() {
		totalServices++;
		return this;
	}

}
