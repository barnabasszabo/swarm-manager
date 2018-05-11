package hydra.intranet.swarmManager.service.validator;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gerrit.extensions.client.ChangeStatus;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.service.GerritService;

@Component
public class GerritReviewValidator extends AbstractValidator {

	private static final List<ChangeStatus> CLOSED_STATE = Arrays.asList(ChangeStatus.ABANDONED, ChangeStatus.MERGED);

	@Autowired
	private GerritService gerritRepo;

	@Override
	public void calculate(final Ecosystem eco) {
		if (configService.isTrue("GERRIT_CHECK")) {
			final String labelKey = configService.getString("GERRIT_REVIEW_LABEL_NAME");
			if (eco.getLabels().containsKey(labelKey)) {
				eco.setReview(gerritRepo.getChange(eco.getLabels().get(labelKey).get(0)));

				if (eco.getReview() != null) {
					if (CLOSED_STATE.contains(eco.getReview().status)) {
						markIf(eco, "Review is closed", "GERRIT_REMOVE_ECOSYSTEM_IF_CLOSED");
					}
				}
			}
		}
	}

}
