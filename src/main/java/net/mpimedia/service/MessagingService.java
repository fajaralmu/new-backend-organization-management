package net.mpimedia.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebRequest;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.entity.Message;
import net.mpimedia.entity.SessionData; 

@Service
public class MessagingService {
	
	private static final String CODE_NEW_LIVE = "12";
	private static final String CODE_DISMISS_LIVE = "13";

	private Map<String, List<Message>> messagesStore = new HashMap<String, List<Message>>();
	
	@Autowired
	private SessionService sessionService;
	
	@Autowired
	private RealtimeService realtimeService;

	public WebResponse getMessages(String requestId) {

		List<Message> messages 	= new ArrayList<>();
		List<String> receivers  = new ArrayList<>();
		Set<String> storeKeys 	= messagesStore.keySet();
		WebResponse response 	= WebResponse.success(); 
		
		for (String key : storeKeys) {
			if(key.contains(requestId)) {
				messages.addAll(messagesStore.get(key));
			}
		}
		
		for (Message message : messages) {
			receivers.add(message.getReceiver());
		}
		
		response.setCode(requestId);
		response.setMessages(messages);
		response.setReceivers(receivers);
		return response;
	} 
	
	public synchronized void updateChat(String senderReqId, String receiver) {
		WebResponse senderMessages = getMessages(senderReqId); 
		WebResponse receiverMessages = getMessages(receiver);
		
		realtimeService.sendMessageChat(senderMessages, senderReqId);
		realtimeService.sendMessageChat(receiverMessages, receiver);
	}

	public synchronized WebResponse sendMessage(WebRequest webRequest) {
		WebResponse response = WebResponse.success();
		
		if(webRequest.getMessage() == null) {
			return WebResponse.failed("Invalid Message!");
		}
		
		String requestId = webRequest.getRequestId();
		String message	 = webRequest.getMessage().getText();
		String receiver	 = webRequest.getMessage().getReceiver();
		String storeKey	 = requestId+"-"+receiver;
		String storeKey2 = receiver+"-"+requestId;
		
		Message newMessage = new Message(requestId, message, receiver);
		
		boolean keyOneExist = messagesStore.get(storeKey) != null;
		boolean keyTwoExist = messagesStore.get(storeKey2) != null;
		
		if( keyOneExist )
		{
			messagesStore.get(storeKey).add(newMessage);
		}else if(keyTwoExist)
		{
			messagesStore.get(storeKey2).add(newMessage);
		}else
		{
			messagesStore.put(storeKey, new ArrayList<Message>() {
				{
					add(newMessage);
				}
			});
		}
		
		updateChat(requestId, receiver);
		
		return response ;
	}

	 
	public WebResponse getAvailableSessions(WebRequest webRequest) {
		
		WebResponse response = WebResponse.success();
		
		Set<String> sessionKeys = sessionService.getSessionKeys();  
		List<Map<String, Object>> sessionMap = new ArrayList<>();
		 
		for(String key:sessionKeys) {
			final SessionData sessionData = sessionService.GetSessionData(key);
			sessionMap.add(new HashMap<String, Object>() {
				{
					put("key", key);
					put("userAgent", sessionData.getUserAgent());
				}
			});
		}
		
		response.setSessionKeys(sessionMap );
		return response;
		
	}
	
	public void handleNewUserLive(String requestId) {
		Set<String> sessionKeys = sessionService.getSessionKeys();  
		
		 
		for(String key:sessionKeys) {
			
			if(key.equals(requestId)) {
				continue;
			}
			
			final SessionData sessionData = sessionService.GetSessionData(requestId);
			
			if(sessionData == null) {
				continue;
			}
			
			List<Map<String, Object>> sessionMap = new ArrayList<>();
			sessionMap.add(new HashMap<String, Object>() {
				{
					put("key", requestId);
					put("userAgent", sessionData.getUserAgent());
				}
			});
			
			WebResponse response = new WebResponse();
			response.setCode(CODE_NEW_LIVE);
			response.setSessionKeys(sessionMap);
			
			realtimeService.sendNewOnlineUser(response, key);
		}
	}

}
