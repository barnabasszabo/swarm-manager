package hydra.intranet.swarmManager.service.validator;

import org.springframework.stereotype.Component;

import com.github.dockerjava.api.model.TaskState;

import hydra.intranet.swarmManager.domain.Ecosystem;

@Component
public class StatusValidator extends AbstractValidator {

	@Override
	public void calculate(final Ecosystem eco) {
		eco.getTasks().stream().filter(t -> !TaskState.SHUTDOWN.equals(t.getTask().getStatus().getState())).forEach(t -> {
			eco.getStatus().increaseTotalService();
		});
		eco.getTasks().stream().filter(t -> TaskState.RUNNING.equals(t.getTask().getStatus().getState())).forEach(t -> {
			eco.getStatus().increaseRunningService();
		});
	}

}
