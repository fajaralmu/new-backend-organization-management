package net.mpimedia.service;

import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.entity.SessionData;

@Service
@Slf4j
public class SchedulerService {
	
	@Autowired
	private RegistryService registryService;
	@Autowired
	private TemporaryDataService temporaryDataService;
	
	private static final Long MAX_IDLE_TIME = 60000L;
	
	@PostConstruct
	public void init() {
		log.info("////////////////SCHEDULER SERVICE////////////////");
		LogProxyFactory.setLoggers(this);
		
		this.start();
		this.temporaryDataService.init();
	}
	
	public void start() {
	
		log.info("...................Starting scheduller...............");
		
		 
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) { 
					Set<String> keys = registryService.getSessionKeys();
					
					if(keys!=null)
						for (String string : keys) {
							SessionData sessionData = registryService.getSessionData(string);
							
							Long systemDate = new Date().getTime();
							Long delta = systemDate - sessionData.getModifiedDate().getTime();
							
							if (delta >= MAX_IDLE_TIME  ) {
								
								log.warn("**WILL REMOVE SESSION WITH KEY: {}, idle time: {}",string,delta);
								registryService.remove(string);
							}
						}
					
				
				}
			}
		});
		
		thread.start();
	}

}
