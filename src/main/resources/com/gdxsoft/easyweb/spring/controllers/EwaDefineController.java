package com.gdxsoft.easyweb.spring.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.easyweb.conf.ConfAdmins;
import com.gdxsoft.easyweb.define.servlets.ServletGroup;
import com.gdxsoft.easyweb.define.servlets.ServletIndex;
import com.gdxsoft.easyweb.define.servlets.ServletRemoteSync;
import com.gdxsoft.easyweb.define.servlets.ServletXml;

@Controller
public class EwaDefineController {

	/**
	 * URL of the define home
	 */
	@RequestMapping({ "/EWA_DEFINE", "/EWA_DEFINE/", "/EWA_DEFINE/index.jsp" })
	@ResponseBody
	public String ewaDefineIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// initial the define admins
		ConfAdmins.getInstance();
		ServletIndex r = new ServletIndex();
		r.doGet(request, response);
		return null;
	}

	/**
	 * URL of operation configurations
	 */
	@RequestMapping({ "/EWA_DEFINE/cgi-bin/xml/", "/EWA_DEFINE/cgi-bin/xml/index.jsp" })
	@ResponseBody
	public String ewaDefineXml(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletXml r = new ServletXml();
		r.doGet(request, response);
		return null;
	}

	/**
	 * URL of synchronize local and remote resources
	 */
	@RequestMapping({ "/EWA_DEFINE/remoteSync", "/EWA_DEFINE/cgi-bin/remoteSync/",
			"/EWA_DEFINE/cgi-bin/remoteSync/index.jsp" })
	@ResponseBody
	public String ewaDefineRemoteSync(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletRemoteSync r = new ServletRemoteSync();
		r.doGet(request, response);
		return null;
	}

	/**
	 * URL of operation the group files
	 */
	@RequestMapping({ "/EWA_DEFINE/cgi-bin/group/", "/EWA_DEFINE/cgi-bin/group/index.jsp" })
	@ResponseBody
	public String ewaDefineGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletGroup r = new ServletGroup();
		r.doGet(request, response);
		return null;
	}
}
