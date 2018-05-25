package hydra.intranet.swarmManager.service.detector;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.service.ConfigService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DnsProxyDetector extends AbstractDetector implements IDetector {

	@Autowired
	private ConfigService configService;

	private long elapsedLoopWithoutReload = 0l;

	@Override
	public void detect(final Collection<Ecosystem> ecosystems) {
		if (configService.isTrue("ENABLE_DNS_PROXY_FEATURE")) {
			ecosystems.forEach(eco -> {
				if (eco.getLabels().containsKey(configService.getString("DNS_PROXY_LABEL"))) {
					eco.getLabels().get(configService.getString("DNS_PROXY_LABEL")).forEach(lbl -> {
						try {
							final String[] split = lbl.split("->");
							final String dnsName = split[0];
							final String targetAddress = split[1];

							// Generate, and write proxy config
							final String templateStr = configService.getString("DNSPROXY_MULTI_SERVER_CONFIG_TEMPLATE").replaceAll("DNS_NAME", dnsName)
									.replaceAll("FULL_TARGET_ADDRESS", targetAddress);
							FileUtils.write(new File("/tmp/gen." + dnsName + ".conf"), templateStr, Charset.forName("UTF-8"));

							// Activate proxy config
							Runtime.getRuntime().exec(configService.getString("DNSPROXY_MULTI_SERVER_CONFIG_ACTIVATE").replaceAll("DNS_NAME", dnsName));

							// Active DNS entry
							Runtime.getRuntime().exec(configService.getString("DNSPROXY_DNS_REGISTER_CMD").replaceAll("DNS_NAME", dnsName));

						} catch (final Exception e) {
							log.error("Error in DNS Proxy feature generation!", e);
						}
					});
				}
			});
			elapsedLoopWithoutReload++;

			if (elapsedLoopWithoutReload > configService.getLong("RELOAD_PROXY_INTERVAL_IN_LOOP")) {
				elapsedLoopWithoutReload = 0l;
				try {
					Runtime.getRuntime().exec(configService.getString("DNSPROXY_SERVER_RELOAD"));
				} catch (final Exception e) {
					log.error("Error in proxy reload!", e);
				}
			}
		}
	}

}
