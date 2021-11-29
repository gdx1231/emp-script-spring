package com.gdxsoft.easyweb.spring.staticResources.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.easyweb.resources.Resource;
import com.gdxsoft.easyweb.resources.Resources;
import com.gdxsoft.easyweb.utils.UUrl;

@Controller
public class EwaStaticController {

	/**
	 * EWA static files map
	 */
	@RequestMapping({ "/EmpScriptV2/**" })
	@ResponseBody
	public String staticResources(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		UUrl u = new UUrl(request);

		String path = u.getName().replace("/EmpScriptV2/", "/");

		Resource r = Resources.getResource(path);
		response.setStatus(r.getStatus());
		if (r.getStatus() != 200) {
			return null;
		}
		response.setContentType(r.getType());
		response.addHeader("cache-control", "max-age=86400");
		if (r.isBinary()) {
			response.getOutputStream().write(r.getBuffer());
		} else {
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(r.getContent());
		}
		return null;

	}
 

}
