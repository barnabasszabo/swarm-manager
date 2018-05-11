package hydra.intranet.swarmManager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import hydra.intranet.swarmManager.domain.Config;

@Repository
public interface ConfigRepository extends MongoRepository<Config, String> {

	Config findByKey(String key);

}
