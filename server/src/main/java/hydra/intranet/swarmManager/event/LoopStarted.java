package hydra.intranet.swarmManager.event;

import org.springframework.context.ApplicationEvent;

public class LoopStarted extends ApplicationEvent {

	private static final long serialVersionUID = 878383844355172645L;

	public LoopStarted(final Object source) {
		super(source);
	}

}
