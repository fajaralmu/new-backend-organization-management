package net.mpimedia.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.SessionData;
import net.mpimedia.dto.WebRequest;
import net.mpimedia.entity.User;

@Service
public class SessionService {
	
	 
	private final  RegistryService registryService = new RegistryService();
	
	 
	 
	
	public boolean putUser(String requestId, User finalUser) {

		System.out.println(" o o o o o "+registryService);
		
		SessionData existingSessionData = registryService.getSessionData(requestId);

		if (existingSessionData != null) {
			existingSessionData.setUser( finalUser);
			/**
			 * Clear Division if logout
			 */
			if (finalUser == null) {
				existingSessionData.setDivision(null);
			}

			registryService.putSession(requestId, existingSessionData);
			return true;
		}

		return false;
	}

	public Boolean removeUser(String requestId) {
		return putUser(requestId, null);
	}

	public SessionData GetSessionData(WebRequest request) {

		SessionData existingSessionData = registryService.getSessionData(request.getRequestId());
		return existingSessionData;
	}

	public boolean updateSessionData(String requestId, SessionData session) {

		SessionData existingSessionData = registryService.getSessionData(requestId);

		if (existingSessionData != null) {
			registryService.putSession(requestId, session);
			return true;
		}

		return false;
	}

}