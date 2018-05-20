package hydra.intranet.swarmManager.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

import hydra.intranet.swarmManager.domain.Pool;
import hydra.intranet.swarmManager.repository.PoolRepository;

@Component
public class PoolService {

	@Autowired
	private PoolRepository poolRepository;

	public Collection<Pool> getActivePools() {
		return poolRepository.findAll();
	}

	public Optional<Pool> getPool(final String id) {
		Optional<Pool> maybePool = Optional.absent();
		for (final Pool pool : getActivePools()) {
			if (pool.getId().equals(id)) {
				maybePool = Optional.fromNullable(pool);
			}
		}
		return maybePool;
	}
}
