package net.mpimedia.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("app")
public class AppController {

	@RequestMapping(value = { "/main" })
	public String main(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("baseResourcePath", request.getContextPath().concat("/res/app"));
		return "app/default";
	}
}
