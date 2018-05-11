package hydra.intranet.swarmManager.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Pool;
import hydra.intranet.swarmManager.repository.PoolRepository;

@Component
public class PoolService {

	@Autowired
	private PoolRepository poolRepository;

	public Collection<Pool> getActivePools() {
		return poolRepository.findAll();
	}
}
