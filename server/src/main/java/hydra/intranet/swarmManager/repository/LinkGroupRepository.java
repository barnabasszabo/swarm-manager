package hydra.intranet.swarmManager.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import hydra.intranet.swarmManager.domain.LinkGroup;

public interface LinkGroupRepository extends MongoRepository<LinkGroup, String> {

	Collection<LinkGroup> findByPoolId(String poolId);

	@Transactional
	void deleteByPoolId(String poolId);

}
