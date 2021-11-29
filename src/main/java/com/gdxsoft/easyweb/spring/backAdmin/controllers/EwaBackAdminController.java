package com.gdxsoft.easyweb.spring.backAdmin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class EwaBackAdminController {

	/**
	 * URL of the define home
	 */
	@RequestMapping({ "/back_admin/", "/back_admin/index.jsp" })
	@ResponseBody
	public String ewaBackAdminIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 return "";
	}
	@RequestMapping({ "/back_admin/login", "/back_admin/login.jsp" })
	@ResponseBody
	public String ewaBackAdminLogin(HttpServletRequest request, HttpServletResponse response) {
		return "";
	}

	 
}
