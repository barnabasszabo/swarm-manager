package hydra.intranet.swarmManager.service.detector;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.service.ConfigService;
import hydra.intranet.swarmManager.service.ExecService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DnsProxyDetector extends AbstractDetector implements IDetector {

	@Autowired
	private ConfigService configService;

	@Autowired
	private ExecService execService;

	private Map<String, String> generatedCache = new HashMap<>();

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

							if (!StringUtils.isEmpty(targetAddress) && !targetAddress.equals(generatedCache.get(dnsName))) {

								// Generate, and write proxy config
								final String templateStr = configService.getString("DNSPROXY_MULTI_SERVER_CONFIG_TEMPLATE").replaceAll("DNS_NAME", dnsName)
										.replaceAll("FULL_TARGET_ADDRESS", targetAddress);
								FileUtils.write(new File("/tmp/gen." + dnsName + ".conf"), templateStr, Charset.forName("UTF-8"));

								// Activate proxy config
								execService.exec(configService.getString("DNSPROXY_MULTI_SERVER_CONFIG_ACTIVATE").replaceAll("DNS_NAME", dnsName));

								// Activate DNS entry
								execService.httpGet(configService.getString("DNSPROXY_DNS_REGISTER_CMD").replaceAll("DNS_NAME", dnsName));

								// Reload proxy server
								execService.exec(configService.getString("DNSPROXY_SERVER_RELOAD"));

								generatedCache.put(dnsName, targetAddress);

								log.info("New DNS proxy setup is finished! {}", dnsName);
							}
						} catch (final Exception e) {
							log.error("Error in DNS Proxy feature generation!", e);
						}
					});
				}
			});
		}
	}

	@Scheduled(fixedDelay = 1000 * 60 * 5)
	public void remove() {
		generatedCache = new HashMap<>();
	}

}
