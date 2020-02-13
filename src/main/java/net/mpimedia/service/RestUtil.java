package net.mpimedia.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebRequest;

@Service
public class RestUtil {

	
	@Autowired
	private RegistryService registryService;
	
	
	public  WebRequest populateRequest(WebRequest webRequest, HttpServletRequest httpRequest) { 
		
		final String requestId = httpRequest.getHeader("requestId");
		
		if(requestId!=null) {
			registryService.refreshSession(requestId);
			webRequest.setRequestId(requestId);
		}
		return webRequest ;
	}
}
