package net.mpimedia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

import net.mpimedia.dto.WebRequest;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.entity.Division;
import net.mpimedia.entity.SessionData;
import net.mpimedia.entity.User;
import net.mpimedia.repository.DivisionRepository;
import net.mpimedia.repository.UserRepository;

@Service
public class AccountService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DivisionRepository divisionRepository;
	@Autowired
	private SessionService sessionService ;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse DoLogin( WebRequest webRequest) {

		User requestUser = webRequest.getUser();

		if (null == requestUser) {
			return WebResponse.failed();
		}

		User AuthUser = userRepository.findByUsernameAndPassword(requestUser.getUsername(), requestUser.getPassword());

		if (AuthUser != null) {
			WebResponse response = WebResponse.success();

			User finalUser = new User();

			BeanUtils.copyProperties(AuthUser, finalUser, "password");

			response.setUser(finalUser);

			boolean updateSession = sessionService.putUser(webRequest.getRequestId(), finalUser);
			if (updateSession) {
				List<Division> divisionList = divisionRepository.findByInstitution(finalUser.getInstitution());

				response.setDivisions(divisionList);
				return response;
			}
		}

		return WebResponse.failed();

	}

	public WebResponse DoLogout(  WebRequest webRequest) {
		if (sessionService.removeUser(webRequest.getRequestId())) {
			return WebResponse.success();
		}

		return WebResponse.failed();
	}

	public WebResponse GetDivisions(WebRequest webRequest) {
		SessionData sessionData = this.sessionService.GetSessionData(webRequest);
		if (null != sessionData && null != sessionData.getUser()) {
			WebResponse response = WebResponse.success();

			List<Division> divisions = divisionRepository.findByInstitution(sessionData.getUser().getInstitution());

			if (null == divisions) {
				divisions = new ArrayList<>();
			}
			response.setDivisions(divisions);

			return response;
		}

		return WebResponse.failed();
	}

	public WebResponse SetDivision(WebRequest webRequest) {
		SessionData sessionData = this.sessionService.GetSessionData(webRequest);
		if (null != sessionData && null != sessionData.getUser()) {
			WebResponse response = WebResponse.success();
			try {
				Optional<Division> division = divisionRepository.findById(webRequest.getDivisionId());

				if (division.isPresent()) {
					response.setEntity(division.get());
					sessionData.setDivision(division.get());
					sessionService.updateSessionData(webRequest.getRequestId(), sessionData);

					response.setSessionData(this.sessionService.GetSessionData(webRequest));
					return response;
				}

			} catch (Exception ex) {

			}
		}

		return WebResponse.failed();
	}

}