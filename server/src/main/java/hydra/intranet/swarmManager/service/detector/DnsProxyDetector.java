package hydra.intranet.swarmManager.service.detector;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Override
	public void detect(final Collection<Ecosystem> ecosystems) {
		if (configService.isTrue("ENABLE_DNS_PROXY_FEATURE")) {
			try {
				execService.exec("rm -rf /tmp/gen-dns && mkdir -p /tmp/gen-dns");
			} catch (Exception e1) {
				log.error("Can not clean temp directory");
			}
			boolean foundDns = false;
			for (Ecosystem eco : ecosystems) {
				if (eco.getLabels().containsKey(configService.getString("DNS_PROXY_LABEL"))) {
					foundDns = true;
					eco.getLabels().get(configService.getString("DNS_PROXY_LABEL")).forEach(lbl -> {
						try {
							if (!StringUtils.isEmpty(lbl)) {
								execService.exec("/opt/generate_conf.sh " + eco.getName() + " " + lbl);
							}
						} catch (final Exception e) {
							log.error("Error in DNS Proxy feature generation! {}", eco.getName(), e);
						}
					});
					String cmd = configService.getString("DNS_NAMESERVER_REGISTER_CURL")
							.replace("ECO-NAME", eco.getName());
					try {
						execService.exec(cmd);
						// log.info("DNS registration command is: {}", cmd);
					} catch (Exception e1) {
						log.error("DNS registration command error {}", cmd, e1);
					}
				}
			}
			try {
				if (foundDns) {
					execService.exec("/opt/reload_proxy.sh");
				}
			} catch (Exception e) {
				log.error("Can not reload nginx");
			}
		}
	}

}
