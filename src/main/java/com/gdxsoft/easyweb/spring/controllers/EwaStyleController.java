package com.gdxsoft.easyweb.spring.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdxsoft.easyweb.define.servlets.ServletWorkflow;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.script.servlets.ServletError;
import com.gdxsoft.easyweb.script.servlets.ServletMain;
import com.gdxsoft.easyweb.script.servlets.ServletResources;
import com.gdxsoft.easyweb.script.servlets.ServletStatus;
import com.gdxsoft.easyweb.spring.EwaSpingUpload;
import com.gdxsoft.easyweb.utils.UJSon;

@Controller
public class EwaStyleController {

	/**
	 * EWA Script 
	 */
	@RequestMapping({ "/EWA_STYLE/cgi-bin/", "/EWA_STYLE/cgi-bin/index.jsp", "/ewa", "/ewa1" })
	@ResponseBody
	public String ewa(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletMain r = new ServletMain();
		r.doGet(request, response);
		return null;
	}

	/**
	 * Resources
	 */
	@RequestMapping({ "/EWA_STYLE/cgi-bin/_re_/", "/EWA_STYLE/cgi-bin/_re_/index.jsp", "/r.ewa", "/r1.ewa" })
	@ResponseBody
	public String re(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletResources r = new ServletResources();
		r.doGet(request, response);
		return null;
	}

	/**
	 * ValidCode ，用EWA Script 的 EWA_AJAX=ValidCode进行替换
	 */
	/*
	 * @RequestMapping({ "/EWA_STYLE/cgi-bin/_co_/", "/EWA_STYLE/cgi-bin/_co_/index.jsp" })
	 * 
	 * @ResponseBody public String co(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	 * IOException { ServletCode r = new ServletCode(); r.doGet(request, response); return null; }
	 */

	/**
	 * tree status
	 */
	@RequestMapping({ "/EWA_STYLE/cgi-bin/_st_/", "/EWA_STYLE/cgi-bin/_st_/index.jsp" })
	@ResponseBody
	public String st(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletStatus r = new ServletStatus();
		r.doGet(request, response);
		return null;
	}

	/**
	 * Upload
	 */
	@RequestMapping({ "/EWA_STYLE/cgi-bin/_up_/", "/EWA_STYLE/cgi-bin/_up_/index.jsp" })
	@ResponseBody
	public String up(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// the upload of SpringBoot is different from Tomcat
		MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);

		RequestValue rv = new RequestValue(request, request.getSession());
		request.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();

		EwaSpingUpload up = new EwaSpingUpload();
		up.setRv(rv);
		try {
			up.init(request);
			String uploadName = up.getUploadName();
			List<MultipartFile> items = params.getFiles(uploadName);
			up.setUploadItems(items);

			String s = up.upload();
			out.println(s);
			// response.setHeader("X-EWA_UP_RET", s);
		} catch (Exception e) {
			out.print(UJSon.rstFalse(e.getMessage()));
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Error
	 */
	@RequestMapping({ "/EWA_STYLE/cgi-bin/_er_/", "/EWA_STYLE/cgi-bin/_er_/index.jsp" })
	@ResponseBody
	public String er(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletError r = new ServletError();
		r.doGet(request, response);
		return null;
	}

	/**
	 * Workflow
	 */
	@RequestMapping({ "/EWA_STYLE/cgi-bin/_wf_/", "/EWA_STYLE/cgi-bin/_wf_/index.jsp" })
	@ResponseBody
	public String ewaWorkflow(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletWorkflow r = new ServletWorkflow();
		r.doGet(request, response);
		return null;
	}

}
