package hydra.intranet.swarmManager.service.validator;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Ecosystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TTLValidator extends AbstractValidator {

	@Override
	public void calculate(final Ecosystem eco) {
		final MinTmp minTTL = new MinTmp();
		final String ttlLabel = configService.getString("TTL_LABEL");
		if (eco.getLabels().containsKey(ttlLabel)) {
			eco.getLabels().get(ttlLabel).forEach(v -> {
				try {
					Integer definedTTL = new Integer(v);
					minTTL.min = Math.min(minTTL.min, definedTTL);
				} catch (final Exception e) {
					log.debug("Invalid TTL value! Please define as a number in {}", eco.getName());
				}
			});

			if (minTTL.min < Long.MAX_VALUE) {
				Date now = new Date();
				eco.setTtlEndDate(DateUtils.addMinutes(eco.getUpdated(), minTTL.min));
				if (eco.getTtlEndDate().before(now)) {
					eco.addRemoveMessage(
							"TTL expired: " + eco.getUpdated() + " + " + minTTL.min + " minutes is more than " + now);
				}
			}
		}
	}

}

class MinTmp {
	Integer min = Integer.MAX_VALUE;
}
