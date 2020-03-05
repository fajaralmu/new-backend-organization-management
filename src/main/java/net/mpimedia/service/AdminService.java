 

package net.mpimedia.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import net.mpimedia.util.LogProxyFactory;

@Service
public class AdminService  {
	@Autowired
	private RuntimeDataService registryService;
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
		int  month		= webRequest.getMonth();
		int  year		= webRequest.getYear();
		
		List<Event> events = getEventByPeriod(month, year, sessionData);
		// eventRepository.getByMonthAndYear(month, year, divisionId);
		WebResponse response = WebResponse.success();
		response.setEntities(CollectionUtil.convertList(events));
		return response;
	}
	
	private List<Event> getEventByPeriod(int month, int year, SessionData sessionData){
		
		List<Event> events 		 = new ArrayList<Event>();
		List<Event> sessionEvent = sessionData.getEvents( ) == null ? new ArrayList<>() : sessionData.getEvents();
		
		System.out.println("Session events: "+sessionEvent.size());
		
		for (Event event : sessionEvent) {
			
			Date eventDate 	= event.getDate();
			Calendar cal 	= Calendar.getInstance();
			
			cal.setTime(eventDate);
			
			int calMonth = cal.get(Calendar.MONTH) + 1;
			int calYear  = cal.get(Calendar.YEAR);
			
			if(month == calMonth && year == calYear) {
				events.add(event);
			}
		}
		
		System.out.println("Events filtered: "+events.size());
		
		return events ;
	}
	
}