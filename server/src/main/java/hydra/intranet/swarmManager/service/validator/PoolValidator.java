package hydra.intranet.swarmManager.service.validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.domain.Pool;
import hydra.intranet.swarmManager.service.PoolService;

@Component
public class PoolValidator extends AbstractValidator {

	@Autowired
	private PoolService poolService;

	@Override
	public void calculate(final Ecosystem eco) {
		final Collection<Pool> activePools = poolService.getActivePools();
		activePools.forEach(p -> {
			final Map<String, List<String>> labels = eco.getLabels();
			final String poolLabelKey = configService.getString("POOL_LABEL_KEY");
			if (labels.containsKey(poolLabelKey)) {
				labels.get(poolLabelKey).forEach(l -> {
					if (p.getCode().equalsIgnoreCase(l)) {
						eco.getPools().add(p);
					}
				});
			} else {
				markIf(eco, "There is no pool definition!", "POOL_REMOVE_ECOSYSTEM_IF_NO_POOL_LABEL");
			}
		});

		if (CollectionUtils.isEmpty(eco.getPools())) {
			markIf(eco, "Ecosystem is not in any pool", "POOL_REMOVE_ECOSYSTEM_IF_NO_MATCHING_POOL");
		} else if (eco.getPools().size() > 1) {
			markIf(eco, "Ecosystem is in more than 1 pool", "POOL_REMOVE_ECOSYSTEM_IF_MULTIPLE_MATCHING_POOL");
		}
	}

}
