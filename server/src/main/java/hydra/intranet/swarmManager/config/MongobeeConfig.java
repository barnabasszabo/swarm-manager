package hydra.intranet.swarmManager.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MongobeeConfig extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String uri;
	@Value("${spring.data.mongodb.database}")
	private String dbName;

	@Override
	public MongoClient mongoClient() {
		return new MongoClient(new MongoClientURI(uri));
	}

	@Override
	protected String getDatabaseName() {
		return dbName;
	}

	@Bean
	public Mongobee mongobee(final Environment environment, final BeanFactory beanFactory) throws Exception {
		log.info("Configuring Mongo patch module");
		final Mongobee runner = new Mongobee(uri);
		runner.setChangeLogsScanPackage("hydra.intranet.swarmManager.db.changelog");
		runner.setSpringEnvironment(environment);
		runner.setMongoTemplate(mongoTemplate());
		return runner;
	}

}
