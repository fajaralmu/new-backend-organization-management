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

@Service
public class MessagingService {
	
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
		WebResponse receiverMessages = getMessages(senderReqId);
		
		realtimeService.sendMessageChat(senderMessages);
		realtimeService.sendMessageChat(receiverMessages);
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
		Set<String> sessionKeys = sessionService.getSessionKeys();
		
		WebResponse response = WebResponse.success();
		response.setSessionKeys(sessionKeys);
		return response;
		
	}

}
