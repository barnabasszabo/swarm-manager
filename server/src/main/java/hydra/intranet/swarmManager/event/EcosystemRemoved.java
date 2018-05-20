package hydra.intranet.swarmManager.event;
import org.springframework.context.ApplicationEvent;

import hydra.intranet.swarmManager.domain.Ecosystem;

public class EcosystemRemoved extends ApplicationEvent {

	private static final long serialVersionUID = 878383844355172645L;

	private Ecosystem ecosystem;

	public EcosystemRemoved(final Object source, final Ecosystem ecosystem) {
		super(source);
		this.ecosystem = ecosystem;
	}

	public Ecosystem getEcosystem() {
		return ecosystem;
	}

}
