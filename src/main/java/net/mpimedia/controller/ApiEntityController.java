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
import net.mpimedia.service.ApplicationService;
import net.mpimedia.service.EntityService;
import net.mpimedia.service.RestUtil;
import net.mpimedia.util.LogProxyFactory;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/management")
public class ApiEntityController {
	
	@Autowired
	private EntityService entityService;
	@Autowired
	private RestUtil restUtil;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/add",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse add(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {  
		
		return entityService.updateRecord(restUtil.populateRequest(webRequest, httpRequest) , true);
	}
	@PostMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse update(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {  
		
		return entityService.updateRecord(restUtil.populateRequest(webRequest, httpRequest), false);
	}
	@PostMapping(value = "/get",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse get(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {  
		
		return entityService.filter(restUtil.populateRequest(webRequest, httpRequest));
	}
	@PostMapping(value = "/delete",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse delete(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {  
		
		return entityService.delete(restUtil.populateRequest(webRequest, httpRequest));
	}
	
	 
	
	 
}
