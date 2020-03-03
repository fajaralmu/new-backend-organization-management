package net.mpimedia.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import net.mpimedia.service.RealtimeService2;
import net.mpimedia.util.LogProxyFactory;

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
	
	
//	
//	@MessageMapping("/chat")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse send(Message message){
//		RealtimeResponse response = new RealtimeResponse();
//		System.out.println("Message > "+message);
//	    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//	  
//	    return response;
//	}
	
	
	
}
