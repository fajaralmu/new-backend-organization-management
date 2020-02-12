﻿package net.mpimedia.service;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.SessionData;
import net.mpimedia.dto.WebResponse;

@Service
public class ApplicationService {
	@Autowired
	private RegistryService registryService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse generateAppId(String requestId) {
		WebResponse response = WebResponse.success();

		String RandomChar = UUID.randomUUID().toString();

		boolean exist = false;

		if (null != requestId) {
			SessionData sessionData = registryService.getSessionData(requestId);

			if (null != sessionData) {
				RandomChar = requestId;
				exist = true;

				if (sessionData.getUser() != null) {
					response.setLoggedIn(true);
				}
			}
		}

		response.setMessage(RandomChar);

		if (!exist) {
			registryService.putSession(RandomChar,
					SessionData.builder().message(RandomChar).requestDate(new Date()).build());
		}

		return response;
	}

}