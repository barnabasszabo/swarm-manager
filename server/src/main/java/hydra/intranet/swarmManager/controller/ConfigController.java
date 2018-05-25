package hydra.intranet.swarmManager.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hydra.intranet.swarmManager.domain.Config;
import hydra.intranet.swarmManager.service.ConfigService;

@RestController
public class ConfigController extends BaseController {

	@Autowired
	private ConfigService configService;

	@GetMapping("/config")
	public Collection<Config> getAllConfig() {
		return configService.getAll();
	}
}
