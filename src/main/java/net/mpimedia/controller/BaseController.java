//package net.mpimedia.controller;
//
//import java.util.UUID;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ModelAttribute;
//
//import net.mpimedia.entity.ShopProfile;
//import net.mpimedia.entity.User;
//import net.mpimedia.service.UserAccountService;
//import net.mpimedia.service.RegistryService;
//import net.mpimedia.service.UserSessionService;
//import net.mpimedia.service.WebConfigService;
//import net.mpimedia.util.DateUtil;
//import net.mpimedia.util.MvcUtil;
//@Controller
//public class BaseController {
//	
//	@Autowired
//	private WebConfigService webAppConfiguration;
//	@Autowired
//	private UserSessionService userSessionService;
//	@Autowired
//	private UserAccountService accountService;
//	@Autowired
//	private RegistryService registryService;
//
//	@ModelAttribute("shopProfile")
//	public ShopProfile getProfile(HttpServletRequest request) {
////		System.out.println("Has Session: "+userSessionService.hasSession(request, false));
//		return webAppConfiguration.getProfile();
//	}
//	
//	@ModelAttribute("timeGreeting")
//	public String timeGreeting(HttpServletRequest request) {
//		return DateUtil.getTimeGreeting();
//	}
//	
//	@ModelAttribute("loggedUser")
//	public User getLoggedUser(HttpServletRequest request) {
//		if(userSessionService.hasSession(request, false)) {
//			return userSessionService.getUserFromSession(request);
//		}
//		else return null;
//	} 
//	
//	@ModelAttribute("host")
//	public String getHost(HttpServletRequest request) {
//		return MvcUtil.getHost(request);
//	}
//	
//	@ModelAttribute("contextPath")
//	public String getContextPath(HttpServletRequest request) {
//		return request.getContextPath();
//	}
//	
//	@ModelAttribute("fullImagePath")
//	public String getFullImagePath(HttpServletRequest request) {
//		return getHost(request)+ getContextPath(request)+"/"+getUploadedImagePath(request)+"/";
//	}
//	
//	@ModelAttribute("imagePath")
//	public String getUploadedImagePath(HttpServletRequest request) {
//		return webAppConfiguration.getUploadedImagePath();
//	}
//	
//	@ModelAttribute("pageToken")
//	public String pageToken(HttpServletRequest request) {
//		  return accountService.getToken(request);
//	}
//	
//	@ModelAttribute("requestId")
//	public String requestId(HttpServletRequest request) {
//		Cookie cookie = getCookie(RegistryService.JSESSSIONID, request.getCookies());
//		String cookieValue = cookie == null ? UUID.randomUUID().toString():cookie.getValue();
//		return	registryService.addPageRequest(  cookieValue);
//		 
//	}
//	
//	public static Cookie getCookie(String name, Cookie[] cookies) {
//		try {
//			for (Cookie cookie : cookies) {
//				if(cookie.getName().equals(name)) { return cookie; }
//			}
//		}catch(Exception ex) { ex.printStackTrace(); }
//		return null;
//	}
//}
