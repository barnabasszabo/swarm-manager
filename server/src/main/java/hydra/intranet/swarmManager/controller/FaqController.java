package hydra.intranet.swarmManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hydra.intranet.swarmManager.domain.Faq;
import hydra.intranet.swarmManager.service.FaqService;

@RestController
public class FaqController extends BaseController {

	@Autowired
	private FaqService faqService;

	@GetMapping("/faq")
	public Faq getFaq() {
		return faqService.getLastFaq();
	}
}
