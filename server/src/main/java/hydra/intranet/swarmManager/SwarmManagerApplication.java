package hydra.intranet.swarmManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories
@EnableReactiveMongoRepositories
@EnableScheduling
public class SwarmManagerApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SwarmManagerApplication.class, args);
	}

}
