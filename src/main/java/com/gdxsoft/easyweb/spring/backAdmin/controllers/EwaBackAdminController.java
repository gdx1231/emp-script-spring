package com.gdxsoft.easyweb.spring.backAdmin.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.easyweb.utils.UCookies;
import com.gdxsoft.web.module.HtModBackAdmin;
import com.gdxsoft.web.module.HtModule;

@Controller
public class EwaBackAdminController {

	/**
	 * URL of the define home
	 */
	@RequestMapping({ "/back_admin/", "/back_admin/index.jsp" })
	@ResponseBody
	public String ewaBackAdminIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HtModule m = HtModBackAdmin.getIntance().getModelIndex();
		return m.executeHtmlControl(request, response);
	}

	@RequestMapping({ "/back_admin/login", "/back_admin/login.jsp" })
	@ResponseBody
	public String ewaBackAdminLogin(HttpServletRequest request, HttpServletResponse response) {
		HtModule m = HtModBackAdmin.getIntance().getModelLogin();
		return m.executeHtmlControl(request, response);
	}
	
	@RequestMapping({ "/back_admin/login_exit", "/back_admin/login_exit.jsp" })
	@ResponseBody
	public String ewaBackAdminLoginExit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<String> skipNames = Arrays.asList("APP_LANG","JSESSIONID","EWA_TIMEDIFF");
		UCookies.clearCookies(request, response, skipNames);

		response.sendRedirect( request.getContextPath() +"/back_admin/");
		
		return null;
		
	}
	

}
