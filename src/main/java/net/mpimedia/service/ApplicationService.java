package net.mpimedia.service;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.entity.SessionData;
import net.mpimedia.util.LogProxyFactory;

@Service
@Slf4j
public class ApplicationService {
	@Autowired
	private RuntimeDataService registryService;
	@Autowired
	private MessagingService messagingService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	/**
	 * generate request id for accessing app
	 * @param requestId
	 * @return
	 */
	public WebResponse generateAppId(HttpServletRequest httpServletRequest) {
		WebResponse response = WebResponse.success();

		String generatedSessionKey = UUID.randomUUID().toString();
		String requestId  = httpServletRequest.getHeader("requestId");
		String userAgent  = httpServletRequest.getHeader("user-agent");
		
		boolean exist = false;

		if (null != requestId) {
			SessionData sessionData = registryService.getSessionData(requestId);

			if (null != sessionData) {
				generatedSessionKey = requestId;
				exist = true;

				if (sessionData.getUser() != null) {
					response.setLoggedIn(true);
				}
				
				registryService.refreshSession(requestId);
			}
			response.setSessionData(sessionData);
		}

		response.setMessage(generatedSessionKey);
		

		if (!exist) {
			/**
			 * notify others
			 */
			final String sessionKey = generatedSessionKey;
			
			log.info("Will notify other");
			Thread thread = new Thread(()->  { 
				registryService.storeSessionData(sessionKey,
						SessionData.builder().key(sessionKey).userAgent(userAgent).message(sessionKey).requestDate(new Date()).build());
				
				messagingService.handleNewUserLive(sessionKey);
				
			});
			
			thread.start();
		}else {
			
		}
		
		return response;
	}

}