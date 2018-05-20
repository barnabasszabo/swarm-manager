package hydra.intranet.swarmManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import hydra.intranet.swarmManager.service.task.SwarmCollectTask;

@Configuration
@ComponentScan(basePackages = "hydra.intranet.swarmManager.service.task", basePackageClasses = { SwarmCollectTask.class })
public class ThreadPoolTaskSchedulerConfig {

	private static final int INIT_DELAY = 10000;
	private static final int POOL_SIZE = 2;

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		return threadPoolTaskScheduler;
	}

}
