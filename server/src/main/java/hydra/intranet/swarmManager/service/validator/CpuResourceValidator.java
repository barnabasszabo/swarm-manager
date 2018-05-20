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
		eco.getTasks().stream().filter(t -> GOOD_TASK_STATE.contains(t.getTask().getStatus().getState())).forEach(t -> {
			calculateResource(eco, t);
		});

	}

	private void calculateResource(final Ecosystem eco, final SwarmTask t) {
		try {
			eco.getUsedResource().addLimitCpu(t.getTask().getSpec().getResources().getLimits().getNanoCPUs());
			final Long maxLimitCpuNum = configService.getLong("MAXIMUM_CPU_LIMIT_VALUE");
			if ((maxLimitCpuNum > 0) && (eco.getUsedResource().getLimitCpu() > maxLimitCpuNum)) {
				eco.addRemoveMessage("Defined CPU limit value is higher than the allowed " + maxLimitCpuNum + " nanoCpu!");
			}
		} catch (final Exception e) {
			markIf(eco, "Missing, or invalid CPU Limit resource definition", "REMOVE_IF_CPU_LIMIT_IS_INVALID");
		}
		try {
			eco.getUsedResource().addReservedCpu(t.getTask().getSpec().getResources().getReservations().getNanoCPUs());
			final Long maxReservedCpuNum = configService.getLong("MAXIMUM_CPU_RESERVED_VALUE");
			if ((maxReservedCpuNum > 0) && (eco.getUsedResource().getReservedCpu() > maxReservedCpuNum)) {
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
				final Long gapPercent = configService.getLong("CPU_LIMIT_RESERVED_GAP_IN_PERCENT");
				if ((gapPercent > 0) && (cpuGapInPercent > gapPercent)) {
					eco.addRemoveMessage("Reserved, and Limit resource GAP is higher than " + gapPercent + "%");
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
