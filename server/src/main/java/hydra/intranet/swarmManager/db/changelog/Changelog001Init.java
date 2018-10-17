package hydra.intranet.swarmManager.db.changelog;

import java.util.Arrays;
import java.util.Collection;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import hydra.intranet.swarmManager.domain.Config;
import hydra.intranet.swarmManager.domain.ConfigType;
import hydra.intranet.swarmManager.domain.Faq;
import hydra.intranet.swarmManager.domain.Link;
import hydra.intranet.swarmManager.domain.LinkGroup;
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
		mongoTemplate.save(Pool.builder().id("sample1").displayName("Sample 1").code("mycat1").description("Sample pool").weight(100l).build());
		mongoTemplate.save(Pool.builder().id("sample2").displayName("Sample 2").code("mycat2").description("Sample pool").weight(150l).build());
		mongoTemplate.save(Pool.builder().id("sample3").displayName("Sample 3").code("mycat3").description("Sample pool").weight(40l).build());
	}

	@ChangeSet(order = "003", id = "Init_LinkGroup_Document", author = "Barnabas Szabo")
	public void linkInit(final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
		final Collection<Link> links1 = Arrays.asList(Link.builder().href("https://google.com").value("Google").build(),
				Link.builder().href("https://github.com").value("GitHub").build());
		mongoTemplate.save(LinkGroup.builder().poolId("all").displayName("Useful links").links(links1).build());
		mongoTemplate.save(LinkGroup.builder().poolId("sample1").displayName("Useful links").links(links1).build());
		mongoTemplate.save(LinkGroup.builder().poolId("sample2").displayName("Useful links").links(links1).build());
		mongoTemplate.save(LinkGroup.builder().poolId("sample3").displayName("Useful links").links(links1).build());

		final Collection<Link> links2 = Arrays.asList(Link.builder().href("https://github.com/szabobar/swarm-manager").value("Swarm Manager on Github").build());
		mongoTemplate.save(LinkGroup.builder().poolId("all").displayName("Dev links").links(links2).build());
		mongoTemplate.save(LinkGroup.builder().poolId("sample1").displayName("Dev links").links(links2).build());
		mongoTemplate.save(LinkGroup.builder().poolId("sample2").displayName("Dev links").links(links2).build());
		mongoTemplate.save(LinkGroup.builder().poolId("sample3").displayName("Dev links").links(links2).build());
	}

	@ChangeSet(order = "004", id = "Init_FAQ_Document", author = "Barnabas Szabo")
	public void faqInit(final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
		mongoTemplate.save(Faq.builder().value("# This is the init FAQ entry").build());
	}

	@ChangeSet(order = "005", id = "Init_DnsProxy_Config", author = "Barnabas Szabo")
	public void dnsProxyInit(final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {

		mongoTemplate.save(Config.builder().key("ENABLE_DNS_PROXY_FEATURE").value("False").description("Enable the DNS Proxy feature?").type(ConfigType.BOOLEAN).build());

		mongoTemplate
				.save(Config.builder().key("DNS_PROXY_LABEL").value("custom.dnsProxy").description("Label of the friendly Dns proxy feature.").type(ConfigType.STRING).build());

		mongoTemplate.save(Config.builder().key("RELOAD_PROXY_INTERVAL_IN_LOOP").value("3").description("Define the loop number of the Proxy server config reload")
				.type(ConfigType.LONG).build());

		mongoTemplate.save(Config.builder().key("DNSPROXY_DNS_REGISTER_CMD").value("echo 'FIXME'").description("DNS registration command").type(ConfigType.STRING).build());

		mongoTemplate.save(Config.builder().key("DNSPROXY_DNS_REMOVE_CMD").value("echo 'FIXME'").description("DNS unregistration command").type(ConfigType.STRING).build());

		mongoTemplate.save(Config.builder().key("DNSPROXY_SERVER_RELOAD").value("/opt/reload_proxy.sh").description("Proxy server reload command").type(ConfigType.STRING).build());

		mongoTemplate.save(Config.builder().key("DNSPROXY_MULTI_SERVER_CONFIG_ACTIVATE").value("scp /tmp/gen.DNS_NAME.conf root@proxy:/etc/nginx/conf.d/gen.DNS_NAME.conf")
				.description("Command for activate the dns proxy setup").type(ConfigType.STRING).build());

		mongoTemplate.save(Config.builder().key("DNSPROXY_MULTI_SERVER_CONFIG_TEMPLATE").value( //
				"server {\n" + // Server start
						"        listen 80;\n" + // Listen on default port
						"        server_name DNS_NAME;\n" + // new DNS name
						"        root /usr/share/nginx/html;\n" + // default HTML location
						"        location / {\n" + // location definition
						"                access_log off;\n" + // No access log
						"                proxy_set_header X-Real-IP $remote_addr;\n" + // Set header
						"                proxy_set_header Host $host;\n" + // Set header
						"                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n" + // Set header
						"                proxy_set_header Upgrade $http_upgrade;\n" + // Set header
						"                proxy_set_header Connection \"upgrade\";\n" + // Set header
						"                proxy_http_version 1.1;\n" + // Set header for websocket
						"                proxy_pass FULL_TARGET_ADDRESS;\n" + // target address
						"        }\n" + // End of location
						"}")
				.description("Tempalte of the proxy server setting").type(ConfigType.STRING).build());

	}

	@ChangeSet(order = "006", id = "Add_Change_before_Remove_command", author = "Barnabas Szabo")
	public void chanceInit(final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
		mongoTemplate.save(Config.builder().key("REMOVE_CHANCE_NUM").value("3").description("How many check circle allowed before remove the ecosystem.").type(ConfigType.LONG).build());
	}
	
	@ChangeSet(order = "007", id = "Add_TTL", author = "Barnabas Szabo")
	public void ttlInit(final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
		mongoTemplate.save(Config.builder().key("TTL_LABEL").value("loxon.ttl").description("Environment live in minutes").type(ConfigType.LONG).build());
	}
	
}
