package hydra.intranet.swarmManager.db.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import hydra.intranet.swarmManager.domain.Config;
import hydra.intranet.swarmManager.domain.ConfigType;
import hydra.intranet.swarmManager.domain.Pool;

@ChangeLog(order = "001")
public class Changelog001Init {

	@ChangeSet(order = "001", id = "Init_Config_Document", author = "Barnabas Szabo")
	public void configInit(final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
		// Docker
		mongoTemplate.save(Config.builder().key("DOCKER_HOST").value("unix:///var/run/docker.sock")
				.description("Docker host definition. Use unix:// in case of the localhost, or tcp:// in case of remote connection").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("DOCKER_HOST_DNS_NAME").value("localhost").description("Swarm DNS name").type(ConfigType.STRING).build());

		// Gerrit
		mongoTemplate.save(Config.builder().key("GERRIT_CHECK").value("False").description("Check the gerrit").type(ConfigType.BOOLEAN).build());
		mongoTemplate
				.save(Config.builder().key("GERRIT_REVIEW_LABEL_NAME").value("custom.review").description("Label key of the review definition").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("GERRIT_HOST").value("").description("Gerrit host with http://").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("GERRIT_USER").value("").description("Gerrit connection user name").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("GERRIT_PWD").value("").description("Gerrit connection password").type(ConfigType.STRING).build());
		mongoTemplate.save(
				Config.builder().key("GERRIT_REMOVE_ECOSYSTEM_IF_CLOSED").value("False").description("Remove the ecosystem is review is closed").type(ConfigType.BOOLEAN).build());

		// Info
		mongoTemplate.save(Config.builder().key("INFO_LABEL").value("custom.info").description("Label key of the ecosystem information section").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("REMOVE_BY_MISSING_INFO_LABEL").value("False").description("Remove the ecosystem, if there is no information label")
				.type(ConfigType.BOOLEAN).build());

		// Email
		mongoTemplate
				.save(Config.builder().key("EMAIL_LABEL").value("custom.cleanEmail").description("Email address for the remove feedback action").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("SUPPORT_EMAIL").value("support@no-email.com").description("Email address of the support team").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("EMAIL_SUBJECT_PREFIX").value("[SWARM][WARN] ").description("Prefix of the email subject").type(ConfigType.STRING).build());

		// Priority
		mongoTemplate.save(Config.builder().key("PRIORITY_LABEL").value("custom.priority").description("Label of the priority").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("PRIORITY_FROM").value("0").description("Minimum value of the priority").type(ConfigType.LONG).build());
		mongoTemplate.save(Config.builder().key("PRIORITY_TO").value("100").description("Maximum value of the priority").type(ConfigType.LONG).build());
		mongoTemplate.save(Config.builder().key("PRIORITY_REMOVE_ECOSYSTEM_IF_INVALID").value("False").description("Remove ecosystem if priority definition is invalid")
				.type(ConfigType.BOOLEAN).build());

		// Pool
		mongoTemplate.save(Config.builder().key("POOL_LABEL_KEY").value("custom.pool").description("Label key of the pool").type(ConfigType.STRING).build());
		mongoTemplate.save(Config.builder().key("POOL_REMOVE_ECOSYSTEM_IF_NO_POOL_LABEL").value("False").description("Remove ecosystem is there is no pool label")
				.type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("POOL_REMOVE_ECOSYSTEM_IF_NO_MATCHING_POOL").value("False").description("Remove ecosystem if ecosystem is not in any poll")
				.type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("POOL_REMOVE_ECOSYSTEM_IF_MULTIPLE_MATCHING_POOL").value("False")
				.description("Remove ecosystem if ecosystem is in more than one pool").type(ConfigType.BOOLEAN).build());

		// Resource
		mongoTemplate.save(Config.builder().key("MEMORY_LIMIT_RESERVED_GAP_IN_PERCENT").value("0")
				.description("If limit, and reserved memory value defined, what is the allowed GAP between them").type(ConfigType.LONG).build());
		mongoTemplate.save(Config.builder().key("REMOVE_IF_RESERVED_MEMORY_LOWER_THAN_LIMIT").value("False")
				.description("Remove the ecosystem, if limit is lower than reserved memory value").type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("REMOVE_IF_MEMORY_RESERVED_IS_INVALID").value("False")
				.description("Remove the ecosystem if one of the container's memory reserved definition is missing!").type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("REMOVE_IF_MEMORY_LIMIT_IS_INVALID").value("False")
				.description("Remove the ecosystem if one of the container's memory limit definition is missing!").type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("MAXIMUM_MEMORY_LIMIT_VALUE").value("0").description("Maximum memory limit value in byte").type(ConfigType.LONG).build());
		mongoTemplate.save(Config.builder().key("MAXIMUM_MEMORY_RESERVED_VALUE").value("0").description("Maximum memory reserved value in byte").type(ConfigType.LONG).build());

		mongoTemplate.save(Config.builder().key("CPU_LIMIT_RESERVED_GAP_IN_PERCENT").value("0")
				.description("If limit, and reserved CPU value defined, what is the allowed GAP between them").type(ConfigType.LONG).build());
		mongoTemplate.save(Config.builder().key("REMOVE_IF_RESERVED_CPU_LOWER_THAN_LIMIT").value("False")
				.description("Remove the ecosystem, if limit is lower than reserved CPU value").type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("REMOVE_IF_CPU_LIMIT_IS_INVALID").value("False")
				.description("Remove the ecosystem if one of the container's CPU limit definition is missing!").type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("REMOVE_IF_CPU_RESERVED_IS_INVALID").value("False")
				.description("Remove the ecosystem if one of the container's CPU reserved definition is missing!").type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("MAXIMUM_CPU_LIMIT_VALUE").value("0").description("Maximum CPU limit value in nanoCore").type(ConfigType.LONG).build());
		mongoTemplate.save(Config.builder().key("MAXIMUM_CPU_RESERVED_VALUE").value("0").description("Maximum CPU reserved value in nanoCore").type(ConfigType.LONG).build());
		mongoTemplate.save(Config.builder().key("CHECK_CPU_IN_OVERLOAD_DETECTION").value("False").description("Check the CPU reserved values in overload detection")
				.type(ConfigType.BOOLEAN).build());
		mongoTemplate.save(Config.builder().key("CHECK_MEMORY_IN_OVERLOAD_DETECTION").value("False").description("Check the memory reserved values in overload detection")
				.type(ConfigType.BOOLEAN).build());

		mongoTemplate.save(Config.builder().key("SYSMTEM_STANDBY_PERCENT").value("5").description("Percent of the system standby").type(ConfigType.LONG).build());

		// Global remove
		mongoTemplate.save(Config.builder().key("EXEC_REMOVE_COMMAND").value("False").description("Execute the remove command").type(ConfigType.BOOLEAN).build());

		// Scheduler
		mongoTemplate.save(Config.builder().key("CHECK_FIXED_DELAY_IN_SEC").value("10").description("Frequency of the check circle in sec.").type(ConfigType.LONG).build());

	}

	@ChangeSet(order = "002", id = "Init_Pool_Document", author = "Barnabas Szabo")
	public void poolInit(final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
		mongoTemplate.save(Pool.builder().displayName("Sample 1").code("mycat1").description("Sample pool").weight(100l).build());
		mongoTemplate.save(Pool.builder().displayName("Sample 2").code("mycat2").description("Sample pool").weight(150l).build());
		mongoTemplate.save(Pool.builder().displayName("Sample 3").code("mycat3").description("Sample pool").weight(40l).build());

	}

}
