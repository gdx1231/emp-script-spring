package com.gdxsoft.easyweb.spring.business.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.web.http.HttpOaSysAttView;
import com.gdxsoft.web.http.HttpShortUrlVerify;
import com.gdxsoft.web.http.HttpUploadResource;

@Controller
public class OAController {

	 

	@RequestMapping({ "/ups/**" })
	@ResponseBody
	/**
	 * 上传目录资源读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String uploadResource(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpUploadResource w = new HttpUploadResource("/ups/", HttpUploadResource.DAYS_30);
		return w.response(request, response);

	}

	/**
	 * 短地址跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@GetMapping({ "/s", "/short-url-verify" })
	@ResponseBody
	public String verifyShortUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 短地址跳转
		// # nginx 配置 短地址
		// rewrite ^/s/([^/]+)$ /your-context-path/short-url-verify?uid=$1 last;
		HttpShortUrlVerify s = new HttpShortUrlVerify();

		return s.response(request, response);
	}

	/**
	 * 在线查看或下载 sys_atts 文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/sys-att" })
	@ResponseBody
	public String oaSysAttView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pdfjs = request.getContextPath() + "/opensource/pdfjs/pdfjs-2.10.377-dist/web/viewer.html";
		// pdfjs 其实是无效了，因为 firefox已经支持
		HttpOaSysAttView o = new HttpOaSysAttView(pdfjs);

		return o.response(request, response);
	}
}
