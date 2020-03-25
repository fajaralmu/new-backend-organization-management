package net.mpimedia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebResponse;
import net.mpimedia.util.LogProxyFactory;

@Service
public class RealtimeService {
	Logger log = LoggerFactory.getLogger(RealtimeService.class);

	@Autowired
	private SimpMessagingTemplate webSocket; 

	public RealtimeService() {
		LogProxyFactory.setLoggers(this);
		log.info("=======================REALTIME SERVICE 2======================="); 
	}


	public boolean sendUpdateSession(Object payload) {
 
		webSocket.convertAndSend("/wsResp/sessions", payload);

		return true;
	}

	public void sendProgress(double progress, String requestId) {
	//	System.out.println(">>>>>>>>>>SEND PROGRESS:" + progress + " (" + requestId + ")");
		sendProgress(WebResponse.builder().requestId(requestId).percentage(progress).build());
	}

	public void sendProgress(WebResponse shopApiResponse) {
		webSocket.convertAndSend("/wsResp/progress", shopApiResponse);
	}


	public void sendMessageChat(WebResponse response, String requestId) {
		webSocket.convertAndSend("/wsResp/messages/"+requestId, response);
		
	}
	
	/**
	 * notify user with reqID = requestId that there is a new user live
	 * @param response
	 * @param requestId
	 */
	public void sendNewOnlineUser(WebResponse response, String requestId) {
		log.info("Notify {} that there is new user", requestId);
		webSocket.convertAndSend("/wsResp/live/"+requestId, response);
	}

}
