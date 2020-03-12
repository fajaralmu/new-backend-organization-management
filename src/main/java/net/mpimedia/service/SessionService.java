package net.mpimedia.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebRequest;
import net.mpimedia.entity.SessionData;
import net.mpimedia.entity.User;

@Service
public class SessionService {

	@Autowired
	private RuntimeDataService runtimeDataService;

	public boolean putUser(String requestId, User finalUser) {

		SessionData existingSessionData = runtimeDataService.getSessionData(requestId);

		if (existingSessionData != null) {
			existingSessionData.setUser(finalUser);
			/**
			 * Clear Division if logout
			 */
			if (finalUser == null) {
				existingSessionData.setDivision(null);
			}

			runtimeDataService.storeSessionData(requestId, existingSessionData);
			return true;
		}

		return false;
	}

	public Boolean removeUser(String requestId) {
		return putUser(requestId, null);
	}

	public SessionData GetSessionData(WebRequest request) {

		SessionData existingSessionData = runtimeDataService.getSessionData(request.getRequestId());
		return existingSessionData;
	}

	public boolean updateSessionData(String requestId, SessionData session) {

		SessionData existingSessionData = runtimeDataService.getSessionData(requestId);

		if (existingSessionData != null) {
			runtimeDataService.storeSessionData(requestId, session);
			return true;
		}

		return false;
	}
	
	public Set<String> getSessionKeys(){
		return runtimeDataService.getSessionKeys();
	}

}