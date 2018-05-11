package hydra.intranet.swarmManager.service.validator;

import org.springframework.beans.factory.annotation.Autowired;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.service.ConfigService;

public abstract class AbstractValidator implements IEcosystemValidator {

	@Autowired
	protected ConfigService configService;

	protected final void markIf(final Ecosystem ecosystem, final String msg, final String configKey) {
		if (configService.isTrue(configKey)) {
			ecosystem.addRemoveMessage(msg);
		}
	}
}
