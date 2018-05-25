package hydra.intranet.swarmManager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import hydra.intranet.swarmManager.domain.Faq;

@Repository
public interface FaqRepository extends MongoRepository<Faq, String> {

}
