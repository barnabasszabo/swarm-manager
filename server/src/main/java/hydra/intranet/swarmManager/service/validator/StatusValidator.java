package hydra.intranet.swarmManager.service.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.github.dockerjava.api.model.TaskState;

import hydra.intranet.swarmManager.domain.Ecosystem;

@Component
public class StatusValidator extends AbstractValidator {

	private Set<TaskState> FAILED_STATUS = new HashSet<>(Arrays.asList(TaskState.SHUTDOWN, TaskState.FAILED, TaskState.REJECTED));

	@Override
	public void calculate(final Ecosystem eco) {
		eco.getTasks().stream().filter(t -> !FAILED_STATUS.contains(t.getTask().getStatus().getState())).forEach(t -> {
			eco.getStatus().increaseTotalService();
		});
		eco.getTasks().stream().filter(t -> TaskState.RUNNING.equals(t.getTask().getStatus().getState())).forEach(t -> {
			eco.getStatus().increaseRunningService();
		});
	}

}
