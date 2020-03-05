package net.mpimedia.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.GroupCacheAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.entity.SessionData;
import net.mpimedia.repository.SessionDataRepository;

@Service
@Slf4j
public class RuntimeDataService {

	public static final String PAGE_REQUEST = "page_req_id";

	public static final String PAGE_REQUEST_ID = "requestId";

	public static final String JSESSSIONID = "JSESSIONID";

	public static final String SESSION_DATA = "session_data";
	
	static final ObjectMapper objectMapper = new ObjectMapper();
 
	private static final Map<String, Object> SESSION_MAP = Collections.synchronizedMap(new HashMap<>());
	
//	private static final CacheAccess<String, Object> SESSION_MAP = JCS.getInstance(SESSION_DATA);
//	private static final GroupCacheAccess<Object, Object> CACHE_GROUP = JCS.getGroupCacheInstance(SESSION_DATA);

	
	@Autowired
	private SessionDataRepository sessionDataRepository;
	
	
	public RuntimeDataService() {
		 
		
		
		System.out.println("======================= RuntimeDataService ===============================");
	}
	
	

	/**
	 * get remote object
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public SessionData getModel(String key) {
		
		if(SESSION_MAP.get(key)!=null) {
			return (SessionData) SESSION_MAP.get(key);
//			
//			try {
//				return (SessionData) objectMapper.readValue(sessionString, SessionData.class);
//			} catch (Exception e) {
//				log.error("Error getting session {}", e);
//				e.printStackTrace();
//			}
		}
		return null;
		
//		log.info("Will get from DB");
//		
//		try {
//			SessionData object = sessionDataRepository.findTop1ByKey(key); 
//			sessions.put(key, object);
//			return object;
//			
//		} catch (Exception ex) { 
//			ex.printStackTrace();
//			return null;
//			
//		}
	}

	public SessionData getSessionData(String requestId) { 
		return this.getModel(requestId);
	}

	public void storeSessionData(String requestId, SessionData existingSessionData) {  
		
		if(existingSessionData != null) {
			existingSessionData.setModifiedDate(new Date());
		}
		 
		SESSION_MAP.put(requestId, existingSessionData);
	 
		
		Thread thread = new Thread(new Runnable() { 
				@Override
				public void run() {
					
					log.info("Will save to DB: {}",existingSessionData);
					
					if(existingSessionData!=null) {
						sessionDataRepository.save(existingSessionData); 
					}
					
					log.info("Saved to DB");
					
				}
			} 
		);
		
		thread.start();
	}
	
	public void refreshSession(String requestId) {
		
		log.info("Will refresh session: {}", requestId);
		
		SessionData sessionData = getModel(requestId);
		
		if(sessionData!=null) {
			storeSessionData(requestId, sessionData);
		}	
	}
	
	/**======================ACCESSED BY SCHEDULER================== **/
	
	public void clear() {
		this.SESSION_MAP.clear();
	}
	
	public void remove(String key) {
		log.info("Will remove from session: {}", key);
		this.SESSION_MAP.remove(key);
	}
	
	public Set<String> getSessionKeys(){
		Set<String> groupNames = this.SESSION_MAP.keySet();
		
		System.out.println(groupNames);
		return groupNames;
	}

	/**
	 * check page request against cookie jsessionID
	 * 
	 * @param req
	 * @return
	 */
//	public boolean validatePageRequest(HttpServletRequest req) {
//		System.out.println("Will validate page request");
//		try {
//			RegistryModel model = getModel(PAGE_REQUEST);
//
//			if (null == model) {
//				return false;
//			}
//
//			Cookie jsessionCookie = BaseController.getCookie(JSESSSIONID, req.getCookies());
//			String pageRequestId = req.getHeader(PAGE_REQUEST_ID);
//			boolean exist = model.getTokens().get(pageRequestId) != null;
//			if (exist) {
//				String reuqestIdValue = (String) model.getTokens().get(pageRequestId);
//
//				System.out.println(" . . . . . Request ID value: " + reuqestIdValue + " vs JSessionId: "
//						+ jsessionCookie.getValue());
//
//				return reuqestIdValue.equals(jsessionCookie.getValue());
//			} else {
//				System.out.println("x x x x Request ID not found x x x x");
//			}
//
//			return false;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return false;
//		}
//	}

}
