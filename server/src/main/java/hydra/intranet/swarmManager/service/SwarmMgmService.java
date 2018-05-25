package hydra.intranet.swarmManager.service;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SwarmMgmService {

	@Autowired
	private SwarmService swarmService;

	@Autowired
	private ConfigService configService;

	private Pattern pattern = Pattern.compile("(.+)\\/(.+):(.*)");

	private final Random random = new Random();

	public int getFreePort(final int min, final int max) {
		return nextRandomPort(getDeclaredPorts(), min, max);
	}

	public String deleteImageFromRegistry(final String name) {
		if (!StringUtils.isEmpty(name)) {
			final String requestName = name.replace("docker.loxon.eu/", "");
			final Matcher matcher = pattern.matcher(requestName);
			String repo = "";
			String imageName = "";
			String tag = "";
			while (matcher.find()) {
				try {
					repo = matcher.group(1);
					imageName = matcher.group(2);
					tag = matcher.group(3);
				} catch (final Exception e) {
				}
			}
			if (!StringUtils.isEmpty(repo) && !StringUtils.isEmpty(imageName) && !StringUtils.isEmpty(tag)) {
				delete("curl -k -L -u " + configService.getString("GERRIT_USER") + ":" + configService.getString("GERRIT_PWD")
						+ " -X DELETE --header 'Accept: text/plain' https://docker.loxon.eu/api/repositories/" + repo + "/" + imageName + "/tags/" + tag);
				return "success! repository: '" + repo + "' ; image: '" + imageName + "' ; tag: '" + tag + "'";
			} else {
				return "Invalid image name! Right image format: repositoryName/imageName:tag";
			}
		}
		return "Invalid request! Please add \"name\" query parameter!";
	}

	public void deleteStack(final String name) {
		delete("docker stack rm " + name);
	}

	public void deleteService(final String name) {
		delete("docker service rm " + name);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void generateDefaultSettings() {
		try {
			final String defaultVirtualhostConfig = configService.getString("DNSPROXY_DEFAULT_CONFIG_TEMPLATE").replaceAll("SWARM_DNS_NAME",
					configService.getString("DOCKER_HOST_DNS_NAME"));
			FileUtils.write(new File("/tmp/default.conf"), defaultVirtualhostConfig, Charset.forName("UTF-8"));

			Runtime.getRuntime().exec(configService.getString("DNSPROXY_DEFAULT_CONFIG_ACTIVATE"));

			Runtime.getRuntime().exec(configService.getString("DNSPROXY_SERVER_RELOAD"));

			log.info("Generated default virtualhost setup on proxy!");
		} catch (final Exception e) {
			log.error("Error in default virtualhost activation!", e);
		}
	}

	private void delete(final String cmd) {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (final Exception e) {
			log.error("Error in remove command", e);
		}
	}

	private int nextRandomPort(final Collection<Integer> declaredPorts, final int min, final int max) {
		final int randomNumber = random.nextInt((max + 1) - min) + min;
		if (!declaredPorts.contains(randomNumber) && isFreeWithTelnet(randomNumber)) {
			return randomNumber;
		} else {
			return nextRandomPort(declaredPorts, min, max);
		}
	}

	private Collection<Integer> getDeclaredPorts() {
		final Collection<Integer> ports = new HashSet<>();
		swarmService.getEcosystems().forEach(eco -> {
			eco.getPortConfig().forEach(p -> {
				ports.add(p.getPublishedPort());
			});
		});
		return ports;
	}

	private boolean isFreeWithTelnet(final int port) {
		try {
			final InetAddress remote = InetAddress.getByName(configService.getString("DOCKER_HOST_DNS_NAME"));
			final Socket s = new Socket(remote, port);
			s.close();
			return false;
		} catch (final Exception ex) {
			return true;
		}
	}

}
