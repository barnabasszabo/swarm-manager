package hydra.intranet.swarmManager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GerritService {

	private GerritApi gerritApi;

	@Autowired
	private ConfigService configService;

	public void init() {
		final GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
		final GerritAuthData.Basic authData = new GerritAuthData.Basic(configService.getString("GERRIT_HOST"), configService.getString("GERRIT_USER"),
				configService.getString("GERRIT_PWD"));
		gerritApi = gerritRestApiFactory.create(authData);
	}

	public ChangeInfo getChange(final String reviewId) {
		if (reviewId != null) {
			try {
				if (gerritApi == null) {
					init();
				}
				return gerritApi.changes().id(reviewId).get();
			} catch (final RestApiException e) {
				log.warn("Error in Gerrit API", e);
			}
		}
		return null;
	}

}
