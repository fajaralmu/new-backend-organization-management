package net.mpimedia.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import net.mpimedia.dto.RegistryModel;
import net.mpimedia.dto.SessionData;

@Service
public class RegistryService {

	public static final String PAGE_REQUEST = "page_req_id";

	public static final String PAGE_REQUEST_ID = "requestId";

	public static final String JSESSSIONID = "JSESSIONID";

	private static final String SESSION_DATA = "session_data";

	static Map<String, Object> sessions = new HashMap<>();

	 
	
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
	public <T> T getModel(String key) {
		try {
			T object = (T) sessions.get(key);
			System.out.println("==registry model: " + object);
			return object;
		} catch (Exception ex) {
			System.out.println("Unexpected error");
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * set registry remote object
	 * 
	 * @param key
	 * @param registryModel
	 * @return
	 */
	public boolean set(String key, Remote registryModel) {
		try {
			if (getModel(key) == null) {
				sessions.put(key, registryModel);
			} else {
				sessions.replace(key, registryModel);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * unbind remote object
	 * 
	 * @param key
	 * @return
	 */
	public boolean unbind(String key) {
		try {
			sessions.remove(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * register new page request to request list
	 * 
	 * @param cookie
	 * @return
	 */
	public String addPageRequest(String cookie) {
		String pageRequestId = UUID.randomUUID().toString();
		if (getModel(PAGE_REQUEST) != null) {
			RegistryModel model = getModel(PAGE_REQUEST);
			model.getTokens().put(pageRequestId, cookie);
			if (set(PAGE_REQUEST, model)) {
				return pageRequestId;
			}

		} else {
			try {
				RegistryModel model = new RegistryModel();
				model.setTokens(new HashMap<>());
				model.getTokens().put(pageRequestId, cookie);
				if (set(PAGE_REQUEST, model)) {
					return pageRequestId;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}

	public SessionData getSessionData(String requestId) {
		// TODO Auto-generated method stub
		return this.getModel(requestId);
	}

	public void putSession(String requestId, SessionData existingSessionData) {
		// TODO Auto-generated method stub
		this.set(requestId, existingSessionData);
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
