package net.mpimedia.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
 
import net.mpimedia.dto.RealtimeResponse;
import net.mpimedia.entity.Message;
import net.mpimedia.service.LogProxyFactory;
import net.mpimedia.service.RealtimeService2;

@CrossOrigin
@RestController
public class SocketController {
	Logger log = LoggerFactory.getLogger(SocketController.class);
	@Autowired
	private SimpMessagingTemplate webSocket;
	@Autowired
	RealtimeService2 realtimeUserService;
	
	public SocketController() {
		log.info("------------------SOCKET CONTROLLER #1-----------------");
	}
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	 
	
	//@MessageMapping("/move")
	//@SendTo("/wsResp/players")
	public RealtimeResponse join2( RealtimeResponse response) throws IOException {
		webSocket.convertAndSend("/wsResp/players", response);
		return response;
	}
	
//	@MessageMapping("/addUser")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse join( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.connectUser(request);
//	}
//	
//	@MessageMapping("/addEntity")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse addEntity( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.addEntity(request);
//	}
//	
//	@MessageMapping("/move")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse move( RealtimeRequest request) throws IOException {
//		log.info("MOVE: {},",request);
//		return realtimeUserService.move(request);
//	}
//	
//	@MessageMapping("/leave")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse leave( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.disconnectUser(request);
//	}
	
	
	
	@MessageMapping("/chat")
	@SendTo("/wsResp/players")
	public RealtimeResponse send(Message message){
		RealtimeResponse response = new RealtimeResponse();
		System.out.println("Message > "+message);
	    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
	  
	    return response;
	}
	
	
	
}
