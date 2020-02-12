package net.mpimedia.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.RegistryModel;
import net.mpimedia.entity.SessionData;
import net.mpimedia.repository.SessionDataRepository;

@Service
public class RegistryService {

	public static final String PAGE_REQUEST = "page_req_id";

	public static final String PAGE_REQUEST_ID = "requestId";

	public static final String JSESSSIONID = "JSESSIONID";

	private static final String SESSION_DATA = "session_data";

	@Autowired
	private SessionDataRepository sessionDataRepository;

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
		try {
			SessionData object = sessionDataRepository.findTop1ByKey(key);
			System.out.println("==registry model: " + object);
			return object;
		} catch (Exception ex) {
			System.out.println("Unexpected error");
			ex.printStackTrace();
			return null;
		}
	}

	public SessionData getSessionData(String requestId) { 
		return this.getModel(requestId);
	}

	public void putSession(String requestId, SessionData existingSessionData) { 
		sessionDataRepository.save(existingSessionData);
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
