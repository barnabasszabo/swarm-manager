package hydra.intranet.swarmManager.service.validator;

import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Ecosystem;

@Component
public class LabelValidator extends AbstractValidator {

	@Override
	public void calculate(final Ecosystem eco) {
		if (!eco.getLabels().containsKey(configService.getString("INFO_LABEL"))) {
			markIf(eco, "Missing info label", "REMOVE_BY_MISSING_INFO_LABEL");
		}
		final String prioLabel = configService.getString("PRIORITY_LABEL");
		if (eco.getLabels().containsKey(prioLabel)) {
			eco.getLabels().get(prioLabel).forEach(v -> {
				final Long from = configService.getLong("PRIORITY_FROM");
				final Long to = configService.getLong("PRIORITY_TO");
				try {
					final Integer prio = new Integer(v);
					if ((prio < from) || (prio > to)) {
						throw new Exception();
					}
				} catch (final Exception e) {
					markIf(eco, "Invalid Priority value! Please define between " + from + " and " + to, "PRIORITY_REMOVE_ECOSYSTEM_IF_INVALID");
				}
			});
		}
	}

}
