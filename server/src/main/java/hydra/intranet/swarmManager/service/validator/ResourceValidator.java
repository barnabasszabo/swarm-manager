package hydra.intranet.swarmManager.service.validator;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.dockerjava.api.model.TaskState;

import hydra.intranet.swarmManager.domain.Ecosystem;

@Component
public class ResourceValidator extends AbstractValidator {

	private static final List<TaskState> GOOD_TASK_STATE = Arrays.asList(TaskState.RUNNING, TaskState.STARTING, TaskState.ALLOCATED, TaskState.ACCEPTED, TaskState.ASSIGNED,
			TaskState.NEW, TaskState.PREPARING, TaskState.READY, TaskState.PENDING);

	@Override
	public void calculate(final Ecosystem eco) {
		eco.getTasks().stream().filter(t -> GOOD_TASK_STATE.contains(t.getTask().getStatus().getState())).forEach(t -> {
			try {
				final Long limitCpu = t.getTask().getSpec().getResources().getLimits().getNanoCPUs();
				final Long limitMemory = t.getTask().getSpec().getResources().getLimits().getMemoryBytes();

				final Long reservedCpu = t.getTask().getSpec().getResources().getReservations().getNanoCPUs();
				final Long reservedMemory = t.getTask().getSpec().getResources().getReservations().getMemoryBytes();

				eco.getUsedResource().addLimitCpu(limitCpu);
				eco.getUsedResource().addReservedCpu(reservedCpu);
				eco.getUsedResource().addLimitMemory(limitMemory);
				eco.getUsedResource().addReservedMemory(reservedMemory);

				final double cpuGapInPercent = (((limitCpu - reservedCpu) * 1.0) / reservedCpu) * 100;
				final double memoryGapInPercent = (((limitMemory - reservedMemory) * 1.0) / reservedMemory) * 100;
				final Long gapPercent = configService.getLong("RESOURCE_GAP_PERCENT");
				if ((cpuGapInPercent > gapPercent) || (memoryGapInPercent > gapPercent)) {
					markIf(eco, "Reserved, and Limit resource GAP is higher than " + gapPercent + "%", "RESOURCE_REMOVE_ECOSYSTEM_IF_GAP_IS_MORE");
				}
				if ((limitCpu < reservedCpu) || (limitMemory < reservedMemory)) {
					markIf(eco, "Reserved is higher than Limit definition", "RESOURCE_REMOVE_ECOSYSTEM_IF_RESERVED_HIGHER");
				}

			} catch (final Exception e) {
				markIf(eco, "Missing, or invalid resource definition", "RESOURCE_REMOVE_ECOSYSTEM_IF_INVALID");
			}

		});

	}

}
