package hydra.intranet.swarmManager.service.validator;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.model.TaskState;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.SwarmTask;
import hydra.intranet.swarmManager.service.MailService;

@Component
public class MemoryResourceValidator extends AbstractValidator {

	@Autowired
	private MailService mailService;

	private static final List<TaskState> GOOD_TASK_STATE = Arrays.asList(TaskState.RUNNING, TaskState.STARTING, TaskState.ALLOCATED, TaskState.ACCEPTED, TaskState.ASSIGNED,
			TaskState.NEW, TaskState.PREPARING, TaskState.READY, TaskState.PENDING);

	@Override
	public void calculate(final Ecosystem eco) {
		eco.getTasks().stream().filter(t -> GOOD_TASK_STATE.contains(t.getTask().getStatus().getState())).forEach(t -> {
			calculateResource(eco, t);
		});

	}

	private void calculateResource(final Ecosystem eco, final SwarmTask t) {
		try {
			eco.getUsedResource().addLimitMemory(t.getTask().getSpec().getResources().getLimits().getMemoryBytes());
			final Long maxLimitMemoryNum = configService.getLong("MAXIMUM_MEMORY_LIMIT_VALUE");
			if ((maxLimitMemoryNum > 0) && (eco.getUsedResource().getLimitMemory() > maxLimitMemoryNum)) {
				eco.addRemoveMessage("Defined memory limit value is higher than the allowed " + maxLimitMemoryNum + " byte!");
			}
		} catch (final Exception e) {
			markIf(eco, "Missing, or invalid Memory Limit resource definition", "REMOVE_IF_MEMORY_LIMIT_IS_INVALID");
		}
		try {
			eco.getUsedResource().addReservedMemory(t.getTask().getSpec().getResources().getReservations().getMemoryBytes());
			final Long maxReservedMemoryNum = configService.getLong("MAXIMUM_MEMORY_RESERVED_VALUE");
			if ((maxReservedMemoryNum > 0) && (eco.getUsedResource().getReservedMemory() > maxReservedMemoryNum)) {
				eco.addRemoveMessage("Defined memory reserved value is higher than the allowed " + maxReservedMemoryNum + " byte!");
			}
		} catch (final Exception e) {
			markIf(eco, "Missing, or invalid Memory reserved resource definition", "REMOVE_IF_MEMORY_RESERVED_IS_INVALID");
		}
		try {
			final long limitMemory = eco.getUsedResource().getLimitMemory();
			final long reservedMemory = eco.getUsedResource().getReservedMemory();
			if ((limitMemory > 0) && (reservedMemory > 0)) {
				final double memoryGapInPercent = (((limitMemory - reservedMemory) * 1.0) / reservedMemory) * 100;
				final Long gapPercent = configService.getLong("MEMORY_LIMIT_RESERVED_GAP_IN_PERCENT");
				if ((gapPercent > 0) && (memoryGapInPercent > gapPercent)) {
					eco.addRemoveMessage("Reserved, and Limit resource GAP is higher than " + gapPercent + "%");
				}
				if (limitMemory < reservedMemory) {
					markIf(eco, "Reserved memory is higher than Limit memory definition", "REMOVE_IF_RESERVED_MEMORY_LOWER_THAN_LIMIT");
				}
			}
		} catch (final Exception e) {
			mailService.sendToSupport("Memory calculation error",
					"Error occured in memory limit and reserved GAP calculation! " + e.getLocalizedMessage() + " - ecosystem: " + eco);
		}
	}

}
