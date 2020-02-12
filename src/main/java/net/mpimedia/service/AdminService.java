 

package net.mpimedia.service;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebRequest;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.entity.Event;
import net.mpimedia.entity.SessionData;
import net.mpimedia.repository.EventRepository;
import net.mpimedia.util.CollectionUtil;

@Service
public class AdminService  {
	@Autowired
	private RegistryService registryService;
	@Autowired
	private EventRepository eventRepository;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse getEvent(WebRequest webRequest) {
		SessionData sessionData = registryService.getSessionData(webRequest.getRequestId());
		if (sessionData == null || sessionData.getDivision() == null) {
			return WebResponse.failed();
		}
		long divisionId = sessionData.getDivision().getId();
		List<Event> events = eventRepository.getByMonthAndYear(webRequest.getMonth(), webRequest.getYear(), divisionId);
		WebResponse response = WebResponse.success();
		response.setEntities(CollectionUtil.convertList(events));
		return response;
	}
}