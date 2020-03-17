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

	/**
	 * put user in session
	 * @param requestId
	 * @param finalUser
	 * @return
	 */
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

	/**
	 * remove user from session
	 * @param requestId
	 * @return
	 */
	public Boolean removeUser(String requestId) {
		return putUser(requestId, null);
	}

	/**
	 * get session data by request
	 * @param request
	 * @return
	 */
	public SessionData GetSessionData(final WebRequest request) {

		SessionData existingSessionData = runtimeDataService.getSessionData(request.getRequestId());
		return existingSessionData;
	}
	
	/**
	 * get session data by requestId
	 * @param requestId
	 * @return
	 */
	public SessionData GetSessionData(final String requestId) {

		SessionData existingSessionData = runtimeDataService.getSessionData(requestId);
		return existingSessionData;
	}

	/**
	 * update entire session by requestId
	 * @param requestId
	 * @param session
	 * @return
	 */
	public boolean updateSessionData(String requestId, SessionData session) {

		SessionData existingSessionData = runtimeDataService.getSessionData(requestId);

		if (existingSessionData != null) {
			runtimeDataService.storeSessionData(requestId, session);
			return true;
		}

		return false;
	}
	
	/**
	 * get session keys
	 * @return
	 */
	public Set<String> getSessionKeys(){
		return runtimeDataService.getSessionKeys();
	}

}