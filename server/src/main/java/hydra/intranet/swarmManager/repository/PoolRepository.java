package hydra.intranet.swarmManager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import hydra.intranet.swarmManager.domain.Pool;

@Repository
public interface PoolRepository extends MongoRepository<Pool, String> {

}
