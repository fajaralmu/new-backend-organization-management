package net.mpimedia.service;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebResponse;
import net.mpimedia.entity.SessionData;
import net.mpimedia.util.LogProxyFactory;

@Service
public class ApplicationService {
	@Autowired
	private RuntimeDataService registryService;

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

		String RandomChar = UUID.randomUUID().toString();
		String requestId  = httpServletRequest.getHeader("requestId");
		String userAgent  = httpServletRequest.getHeader("user-agent");
		
		boolean exist = false;

		if (null != requestId) {
			SessionData sessionData = registryService.getSessionData(requestId);

			if (null != sessionData) {
				RandomChar = requestId;
				exist = true;

				if (sessionData.getUser() != null) {
					response.setLoggedIn(true);
				}
				
				registryService.refreshSession(requestId);
			}
			response.setSessionData(sessionData);
		}

		response.setMessage(RandomChar);
		

		if (!exist) {
			registryService.storeSessionData(RandomChar,
					SessionData.builder().key(RandomChar).userAgent(userAgent).message(RandomChar).requestDate(new Date()).build());
		}
		
		return response;
	}

}