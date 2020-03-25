package net.mpimedia.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.service.ApplicationService;
import net.mpimedia.service.RuntimeDataService;
import net.mpimedia.util.LogProxyFactory;
import net.mpimedia.util.RestUtil;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/public")
public class ApiPublicController {

	@Autowired
	private ApplicationService appService;

	@Autowired
	private RestUtil restUtil;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/generateappid", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse generateappid(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws IOException {
		restUtil.printRequest(httpRequest);
		return appService.generateAppId(httpRequest);
	}

	/** TEST **/

	@Autowired
	private RuntimeDataService runtimeDataService;

	@RequestMapping(value = { "/test/clearsessions" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearsessions(HttpServletRequest request, HttpServletResponse response) {
		runtimeDataService.clear();
		return WebResponse.success();
	}

}
