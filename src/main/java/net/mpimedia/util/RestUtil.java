package net.mpimedia.util;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebRequest;
import net.mpimedia.service.RuntimeDataService;

@Service
public class RestUtil {

	@Autowired
	private RuntimeDataService registryService;

	public WebRequest populateRequest(WebRequest webRequest, HttpServletRequest httpRequest) {

		final String requestId = httpRequest.getHeader("requestId");

		if (requestId != null) {
			registryService.refreshSession(requestId);
			webRequest.setRequestId(requestId);
		} else {
			throw new InvalidParameterException("invalid requestId");
		}
		return webRequest;
	}
}
