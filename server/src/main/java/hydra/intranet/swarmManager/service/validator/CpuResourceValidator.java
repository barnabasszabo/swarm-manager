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
public class CpuResourceValidator extends AbstractValidator {

	@Autowired
	private MailService mailService;

	private static final List<TaskState> GOOD_TASK_STATE = Arrays.asList(TaskState.RUNNING, TaskState.STARTING, TaskState.ALLOCATED, TaskState.ACCEPTED, TaskState.ASSIGNED,
			TaskState.NEW, TaskState.PREPARING, TaskState.READY, TaskState.PENDING);

	@Override
	public void calculate(final Ecosystem eco) {
		final Long maxLimitCpuNum = configService.getLong("MAXIMUM_CPU_LIMIT_VALUE");
		final Long maxReservedCpuNum = configService.getLong("MAXIMUM_CPU_RESERVED_VALUE");
		final Long gapPercent = configService.getLong("CPU_LIMIT_RESERVED_GAP_IN_PERCENT");
		eco.getTasks().stream().filter(t -> GOOD_TASK_STATE.contains(t.getTask().getStatus().getState())).forEach(t -> {
			calculateResource(eco, t, maxLimitCpuNum, maxReservedCpuNum, gapPercent);
		});

	}

	private void calculateResource(final Ecosystem eco, final SwarmTask t, final Long maxLimitCpuNum, final Long maxReservedCpuNum, final Long gapPercent) {
		try {
			final Long nanoCPUs = t.getTask().getSpec().getResources().getLimits().getNanoCPUs();
			eco.getUsedResource().addLimitCpu(nanoCPUs);
			if ((maxLimitCpuNum > 0) && (nanoCPUs > maxLimitCpuNum)) {
				eco.addRemoveMessage("Defined CPU limit value is higher than the allowed " + maxLimitCpuNum + " nanoCpu in " + eco);
			}
		} catch (final Exception e) {
			markIf(eco, "Missing, or invalid CPU Limit resource definition", "REMOVE_IF_CPU_LIMIT_IS_INVALID");
		}
		try {
			final Long nanoCPUs = t.getTask().getSpec().getResources().getReservations().getNanoCPUs();
			eco.getUsedResource().addReservedCpu(nanoCPUs);
			if ((maxReservedCpuNum > 0) && (nanoCPUs > maxReservedCpuNum)) {
				eco.addRemoveMessage("Defined CPU reserved value is higher than the allowed " + maxReservedCpuNum + " nanoCpu!");
			}
		} catch (final Exception e) {
			markIf(eco, "Missing, or invalid CPU reserved resource definition", "REMOVE_IF_CPU_RESERVED_IS_INVALID");
		}
		try {
			final long limitCpu = eco.getUsedResource().getLimitCpu();
			final long reservedCpu = eco.getUsedResource().getReservedCpu();
			if ((limitCpu > 0) && (reservedCpu > 0)) {
				final double cpuGapInPercent = (((limitCpu - reservedCpu) * 1.0) / reservedCpu) * 100;
				if ((gapPercent > 0) && (cpuGapInPercent > gapPercent)) {
					eco.addRemoveMessage("Reserved, and Limit CPU resource GAP is higher than " + gapPercent + "%");
				}
				if (limitCpu < reservedCpu) {
					markIf(eco, "Reserved CPU is higher than Limit CPU definition", "REMOVE_IF_RESERVED_CPU_LOWER_THAN_LIMIT");
				}
			}
		} catch (final Exception e) {
			mailService.sendToSupport("CPU calculation error", "Error occured in CPU limit and reserved GAP calculation! " + e.getLocalizedMessage() + " - ecosystem: " + eco);
		}
	}

}
