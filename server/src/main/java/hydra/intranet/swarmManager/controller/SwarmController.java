package hydra.intranet.swarmManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hydra.intranet.swarmManager.service.SwarmMgmService;

@RestController
public class SwarmController extends BaseController {

	@Autowired
	private SwarmMgmService swarmMgmService;

	@GetMapping("/freeport")
	public String getFreePort(@RequestParam(name = "from", required = false, defaultValue = "10000") final int from,
			@RequestParam(name = "to", required = false, defaultValue = "30000") int to) {
		if (to == 0) {
			to = 30000;
		}
		return swarmMgmService.getFreePort(from, to) + "";
	}

	@GetMapping("/delete/stack")
	public String deleteStack(@RequestParam(name = "name", required = false) final String name) {
		swarmMgmService.deleteStack(name);
		return "done";
	}

	@GetMapping("/delete/service")
	public String deleteService(@RequestParam(name = "name", required = false) final String name) {
		swarmMgmService.deleteService(name);
		return "done";
	}
}
