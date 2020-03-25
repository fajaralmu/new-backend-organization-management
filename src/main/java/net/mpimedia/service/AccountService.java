package net.mpimedia.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.dto.WebRequest;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.entity.Division;
import net.mpimedia.entity.Event;
import net.mpimedia.entity.Program;
import net.mpimedia.entity.Section;
import net.mpimedia.entity.SessionData;
import net.mpimedia.entity.User;
import net.mpimedia.repository.EventRepository;
import net.mpimedia.repository.ProgramRepository;
import net.mpimedia.repository.SectionRepository;
import net.mpimedia.repository.UserRepository;
import net.mpimedia.util.LogProxyFactory;

@Service
@Slf4j
public class AccountService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TemporaryDataService temporaryDataService;
	@Autowired
	private SessionService sessionService ;
	@Autowired
	private ProgramRepository programRepository;
	@Autowired
	private SectionRepository sectionRepository;
	@Autowired
	private EventRepository eventRepository;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse DoLogin( WebRequest webRequest) {

		User requestUser = webRequest.getUser();

		if (null == requestUser) {
			return WebResponse.failed();
		}

		User AuthUser = userRepository.findByUsernameAndPassword(requestUser.getUsername(), requestUser.getPassword());

		if (AuthUser != null) {
			WebResponse response = WebResponse.success();

			User finalUser = new User();

			BeanUtils.copyProperties(AuthUser, finalUser, "password");

			response.setUser(finalUser);

			boolean updateSession = sessionService.putUser(webRequest.getRequestId(), finalUser);
			if (updateSession) {
				List<Division> divisionList = temporaryDataService.getDivision(finalUser);

				response.setDivisions(divisionList);
				return response;
			}
		}

		return WebResponse.failed();

	}

	public WebResponse DoLogout(  WebRequest webRequest) {
		if (sessionService.removeUser(webRequest.getRequestId())) {
			return WebResponse.success();
		}

		return WebResponse.failed();
	}
	
	public void updateEvent(SessionData sessionData)
	{
		if(sessionData.getDivision() == null) {
			log.error("NO DIVISION FOUND");
			return;
		}
		
		new Thread(()->{
			
			log.info("begin update event"); 
			log.info("Refresh events from database for sessionKey: {}", sessionData.getKey());
			
			List<Event> events = eventRepository.getByDivisionId(sessionData.getDivision().getId());
			
			/**
			 * remove unused things
			 */
			if(events != null) {
				for (Event event : events) {
					event.setUser(null);
					if(event.getProgram() != null)
						event.getProgram().setSection(null);
				}
			}
			
			sessionData.setEvents(events);
			
			sessionService.updateSessionData(sessionData.getKey(), sessionData); 
			
			log.info("end update event");
			
		}).start();
	}
	
	public void updateSelectedDivision(SessionData sessionData) {
		
		if(sessionData.getDivision() == null) {
			log.error("NO DIVISION FOUND");
			return;
		}
		
		new Thread(() ->{
			Thread thread1 = new Thread(() ->{ 
				
				log.info("Refresh programs from database for sessionKey: {}", sessionData.getKey());
			
				List<Program> programs = programRepository.getProgramsByDivisionId(sessionData.getDivision().getId());
				
				/**
				 * remove unused
				 */
				if(programs != null) {
					for (Program program : programs) {
						program.setSection(null);
					}
				}
				
				sessionData.setPrograms(programs); 
				 
			}); 
			Thread thread2 = new Thread(() ->{ 
				
				log.info("Refresh sections from database for sessionKey: {}", sessionData.getKey());
				
				List<Section> sections = sectionRepository.findByDivision(sessionData.getDivision());
				
				/**
				 * remove unused
				 */
				if(sections != null) {
					for (Section section : sections) {
						section.setDivision(null);
					}
				}
				
				sessionData.setSections(sections); 
			 
			}); 
			
			Thread thread3 = new Thread(()->{
				
				log.info("Refresh events from database for sessionKey: {}", sessionData.getKey());
				
				List<Event> events = eventRepository.getByDivisionId(sessionData.getDivision().getId());
				
				/**
				 * remove unused things
				 */
				if(events != null) {
					for (Event event : events) {
						event.setUser(null);
						if(event.getProgram() != null)
							event.getProgram().setSection(null);
					}
				}
				
				sessionData.setEvents(events);
			});
			
			
			thread1.start();
			thread2.start();
			thread3.start();
			
			try {
				thread1.join(); 
				thread2.join();
				thread3.join();
			} catch (InterruptedException e) {
				log.error("Thread interrupted: {}",e);
				e.printStackTrace();
			}
			sessionService.updateSessionData(sessionData.getKey(), sessionData); 
			log.info("End refresh session");
		})
		.start();
	}

	public WebResponse GetDivisions(WebRequest webRequest) {
		SessionData sessionData = this.sessionService.GetSessionData(webRequest);
		if (null != sessionData && null != sessionData.getUser()) {
			WebResponse response = WebResponse.success();

			List<Division> divisions = temporaryDataService.getDivision(sessionData.getUser());

			if (null == divisions) {
				divisions = new ArrayList<>();
			}
			response.setDivisions(divisions);

			return response;
		}
		
		if(null != sessionData && null == sessionData.getUser())
			return WebResponse.invalidSession();
		else
			return WebResponse.failed();
	}

	public WebResponse SetDivision(WebRequest webRequest) {
		
		SessionData sessionData = sessionService.GetSessionData(webRequest);
		
		if (null != sessionData && null != sessionData.getUser()) {
			WebResponse response = WebResponse.success();
			try {
				 Division  division = temporaryDataService.getDivisionByDivisionId(webRequest.getDivisionId());

				if (division != null) {
					
					response.setEntity(division );
					sessionData.setDivision(division );
					response.setSessionData(sessionData);
					
					updateSelectedDivision(sessionData);
					
					return response;
				}

			} catch (Exception ex) {
				System.out.println("==========exception handled: "+ex);
				ex.printStackTrace();
			}	
		}

		return WebResponse.failed();
	}

}