package hydra.intranet.swarmManager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Config;
import hydra.intranet.swarmManager.repository.ConfigRepository;

@Component
public class ConfigService {

	@Autowired
	private ConfigRepository configRepository;

	public Config getConfig(final String key) {
		Config cfg = configRepository.findByKey(key);
		if (cfg == null) {
			cfg = Config.builder().key(key).value("").build();
			cfg = configRepository.save(cfg);
		}
		return cfg;
	}

	public boolean isTrue(final String key) {
		final Config config = getConfig(key);
		try {
			return new Boolean(config.getValue());
		} catch (final Exception e) {
			return false;
		}
	}

	public Long getLong(final String key) {
		final Config config = getConfig(key);
		try {
			return new Long(config.getValue());
		} catch (final Exception e) {
			return 0l;
		}
	}

	public String getString(final String key) {
		return getConfig(key).getValue();
	}
}
