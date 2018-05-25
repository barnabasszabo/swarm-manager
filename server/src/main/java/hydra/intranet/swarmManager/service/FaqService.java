package hydra.intranet.swarmManager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import hydra.intranet.swarmManager.domain.Faq;
import hydra.intranet.swarmManager.repository.FaqRepository;

@Component
public class FaqService {

	@Autowired
	private FaqRepository faqRepository;

	public Faq getLastFaq() {
		Faq faq = Faq.builder().build();
		final List<Faq> findAll = faqRepository.findAll();
		if (!CollectionUtils.isEmpty(findAll)) {
			faq = findAll.get(findAll.size() - 1);
		}
		return faq;
	}
}
