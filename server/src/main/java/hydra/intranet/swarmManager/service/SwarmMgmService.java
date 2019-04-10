package hydra.intranet.swarmManager.service;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SwarmMgmService {

	@Autowired
	private SwarmService swarmService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private ExecService execService;

	private final Random random = new Random();

	public int getFreePort(final int min, final int max) {
		return nextRandomPort(getDeclaredPorts(), min, max);
	}

	public void deleteStack(final String name) {
		delete("docker stack rm " + name);
	}

	public void deleteService(final String name) {
		delete("docker service rm " + name);
	}

	private void delete(final String cmd) {
		try {
			execService.exec(cmd);
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
