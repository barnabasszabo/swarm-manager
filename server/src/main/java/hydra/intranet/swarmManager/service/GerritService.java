package hydra.intranet.swarmManager.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;

@Component
public class GerritService {

	private static final String GERRIT_HOST = "";
	private static final String GERRIT_USER = "";
	private static final String GERRIT_PWD = "";

	private GerritApi gerritApi;

	@PostConstruct
	public void init() {
		final GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
		final GerritAuthData.Basic authData = new GerritAuthData.Basic(GERRIT_HOST, GERRIT_USER, GERRIT_PWD);
		gerritApi = gerritRestApiFactory.create(authData);
	}

	public ChangeInfo getChange(final String reviewId) {
		return null;
	}

}
