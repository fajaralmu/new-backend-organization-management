package net.mpimedia.util;

import javax.servlet.http.HttpServletRequest;

import net.mpimedia.dto.WebRequest;

public class RestUtil {

	
	public static WebRequest populateRequest(WebRequest webRequest, HttpServletRequest httpRequest) { 
		
		webRequest.setRequestId(httpRequest.getHeader("requestId"));
		return webRequest ;
	}
}
