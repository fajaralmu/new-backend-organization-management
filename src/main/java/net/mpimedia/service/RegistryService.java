package net.mpimedia.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.entity.SessionData;
import net.mpimedia.repository.SessionDataRepository;

@Service
@Slf4j
public class RegistryService {

	public static final String PAGE_REQUEST = "page_req_id";

	public static final String PAGE_REQUEST_ID = "requestId";

	public static final String JSESSSIONID = "JSESSIONID";

	private static final String SESSION_DATA = "session_data";

	@Autowired
	private SessionDataRepository sessionDataRepository;
	
	private Map<String, SessionData> sessions = new HashMap<>();
	

	public RegistryService() {
		System.out.println("======================= RegistryService ===============================");
	}

	/**
	 * get remote object
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public SessionData getModel(String key) {
		
		if(sessions.get(key)!=null) {
			log.info("Exist in current hashmap: {}",sessions.get(key));
			
			return sessions.get(key);
		}
		
		log.info("Will get from DB");
		
		try {
			SessionData object = sessionDataRepository.findTop1ByKey(key); 
			sessions.put(key, object);
			return object;
			
		} catch (Exception ex) { 
			ex.printStackTrace();
			return null;
			
		}
	}

	public SessionData getSessionData(String requestId) { 
		return this.getModel(requestId);
	}

	public void putSession(String requestId, SessionData existingSessionData) {  
		
		sessions.put(requestId, existingSessionData);
		
		Thread thread = new Thread(new Runnable() { 
				@Override
				public void run() {
					
					log.info("Will save to DB");
					
					sessionDataRepository.save(existingSessionData);
					
					log.info("Saved to DB");
					
				}
			} 
		);
		
		thread.start();
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
