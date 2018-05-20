package hydra.intranet.swarmManager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import hydra.intranet.swarmManager.domain.Ecosystem;
import hydra.intranet.swarmManager.event.EcosystemRemoved;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailService {

	@Autowired
	public JavaMailSender emailSender;

	@Autowired
	public ConfigService configService;

	@EventListener
	public void ecosystemRemoved(final EcosystemRemoved event) {
		final Ecosystem ecosystem = event.getEcosystem();
		final String title = "Ecosystem removed from " + configService.getString("DOCKER_HOST_DNS_NAME") + " ; ecosystem: " + ecosystem.getName();
		final String body = ecosystem.getName() + " Ecosystem removed from " + configService.getString("DOCKER_HOST_DNS_NAME") + "! \n\n" + ecosystem.toString();

		this.sendToSupport(title, body);

		final String ecoEmailAddress = ecosystem.getLastLabel(configService.getString("EMAIL_LABEL"));
		if (ecoEmailAddress != null) {
			this.send(ecoEmailAddress, title, body);
		}
	}

	public void sendToSupport(final String title, final String body) {
		this.send(configService.getString("SUPPORT_EMAIL"), title, body);
	}

	private void send(final String to, final String title, final String body) {
		try {
			final SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(configService.getString("SUPPORT_EMAIL"));
			message.setTo(to);
			message.setSubject(configService.getString("EMAIL_SUBJECT_PREFIX") + title);
			message.setText(body);
			emailSender.send(message);
		} catch (final Exception e) {
			log.error("Can not send email! ", e);
		}
	}

}
