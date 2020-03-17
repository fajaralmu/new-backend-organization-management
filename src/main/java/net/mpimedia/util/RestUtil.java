package net.mpimedia.util;

import java.security.InvalidParameterException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.dto.WebRequest;
import net.mpimedia.service.RuntimeDataService;

@Service
@Slf4j
public class RestUtil {

	@Autowired
	private RuntimeDataService registryService;

	public WebRequest populateRequest(WebRequest webRequest, HttpServletRequest httpRequest) {

		printRequest(httpRequest);
		final String requestId = httpRequest.getHeader("requestId");

		if (requestId != null) {
			registryService.refreshSession(requestId);
			webRequest.setRequestId(requestId);
		} else {
			throw new InvalidParameterException("invalid requestId");
		}
		return webRequest;
	}

	public void printRequest(HttpServletRequest httpServletRequest) {
		log.info("-----will print request-----");

		Enumeration<String> keys = httpServletRequest.getHeaderNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			log.info("REQUEST HEADER :" + key + ":" + httpServletRequest.getHeader(key));
		}
	}
}
