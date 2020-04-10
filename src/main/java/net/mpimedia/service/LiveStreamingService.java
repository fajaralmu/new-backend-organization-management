package net.mpimedia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebRequest;
import net.mpimedia.dto.WebResponse;

@Service
public class LiveStreamingService {
	
	@Autowired
	private RealtimeService realtimeService;

	public WebResponse sendImage(WebRequest populateRequest) {
		
		String requestId = populateRequest.getDestination();
		String imageData = populateRequest.getImageData();
		System.out.println("sendImage.."+requestId);
		realtimeService.sendImageData(populateRequest.getRequestId(), imageData);
		
		WebResponse response = WebResponse.success();
		return response ;
	}
	

}
