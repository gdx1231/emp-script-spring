package com.gdxsoft.easyweb.spring.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.spring.BaseController;
import com.gdxsoft.easyweb.spring.SiteUtils;
import com.gdxsoft.web.app.App;


@Controller
public class IndexController extends BaseController {
	private static Logger LOG = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping("/")
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		RequestValue rv = new RequestValue(request, request.getSession());

		App app = new App(rv);
		String lang = app.appStartLang(response);

		return this.indexByLang(lang, request, response, model);
	}

	@RequestMapping({ "{ewa_lang}", "{ewa_lang}/" })
	public String indexByLang(@PathVariable("ewa_lang") String ewaLang, HttpServletRequest request,
			HttpServletResponse response, Model model) throws Exception {
		request.setCharacterEncoding("utf8");

		request.setAttribute("ewa_Lang", ewaLang);
		// 用户的语言设定 APP_LANG
		javax.servlet.http.Cookie ck = new javax.servlet.http.Cookie("APP_LANG", ewaLang);
		ck.setPath("/");

		SiteUtils su = this.commonData(request, response, model);

		 

		return "index";
	}

	 

	@RequestMapping("/{ewa_lang}/footer")
	public String footer(@PathVariable("ewa_lang") String ewaLang, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		request.setAttribute("ewa_Lang", ewaLang);
		this.commonData(request, response, model);
		return "footer";
	}

	@RequestMapping("/{ewa_lang}/header")
	public String header(@PathVariable("ewa_lang") String ewaLang, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		request.setAttribute("ewa_Lang", ewaLang);
		this.commonData(request, response, model);
		return "header";
	}

}
