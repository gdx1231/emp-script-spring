package com.gdxsoft.easyweb.spring.backAdmin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.web.acl.Login;
import com.gdxsoft.web.module.HtModBackAdmin;
import com.gdxsoft.web.module.HtModule;

/**
 * 元数据管理系统后台，包含菜单主页，登录页面
 * @author admin
 *
 */
@Controller
public class EwaBackAdminController {

	/**
	 * 元数据管理主页
	 * URL of the define home
	 */
	@RequestMapping({ "/back_admin/", "/back_admin/index.jsp" })
	@ResponseBody
	public String ewaBackAdminIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HtModule m = HtModBackAdmin.getIntance().getModelIndex();
		return m.executeHtmlControl(request, response);
	}

	/**
	 * 登录页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/back_admin/login", "/back_admin/login.jsp" })
	@ResponseBody
	public String ewaBackAdminLogin(HttpServletRequest request, HttpServletResponse response) {
		HtModule m = HtModBackAdmin.getIntance().getModelLogin();
		return m.executeHtmlControl(request, response);
	}

	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping({ "/back_admin/login_exit", "/back_admin/login_exit.jsp" })
	@ResponseBody
	public String ewaBackAdminLoginExit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Login.clearLoginCredentials(request, response);
		
		response.sendRedirect(request.getContextPath() + "/back_admin/");

		return null;

	}

}
