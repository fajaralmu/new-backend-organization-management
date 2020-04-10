package net.mpimedia.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.dto.WebRequest;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.service.LiveStreamingService;
import net.mpimedia.service.MessagingService;
import net.mpimedia.util.LogProxyFactory;
import net.mpimedia.util.RestUtil;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/livestreaming")
public class ApiLiveStreamingController {
	
	@Autowired
	private LiveStreamingService liveStreamingService;
	 
	@Autowired
	private RestUtil restUtil;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	 
	@PostMapping(value = "/sendimage", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse sendmessage(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException { 
		return liveStreamingService.sendImage(restUtil.populateRequest(webRequest, httpRequest));
	}
	 
	 
	
	 
}
