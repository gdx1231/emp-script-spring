package com.gdxsoft.easyweb.spring.business.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdxsoft.easyweb.script.PageValue;
import com.gdxsoft.easyweb.script.PageValueTag;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.script.display.frame.FrameParameters;
import com.gdxsoft.easyweb.utils.UJSon;
import com.gdxsoft.web.acl.Login;
import com.gdxsoft.web.http.HttpOaFileView;
import com.gdxsoft.web.http.HttpOaSysAttView;
import com.gdxsoft.web.http.HttpQRCode;
import com.gdxsoft.web.module.HtModBusiness;
import com.gdxsoft.web.module.HtModule;
import com.gdxsoft.web.user.ValidBase;

@Controller
public class BusinessController {
	/**
	 * 商户系统首页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/business", "/business/", "/business/index", "/business/index.jsp" })
	@ResponseBody
	public String businessIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HtModule m = HtModBusiness.getIntance().getModelIndex();

		RequestValue rv = new RequestValue(request);
		if ("adm_menu.Menu.Modify".equalsIgnoreCase(rv.s("itemName"))) {
			// 调用菜单列表
			m.setItemName("adm_menu.Menu.Modify");
		}

		return m.executeHtmlControl(request, response);
	}

	/**
	 * 商户系统登录页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/business/login", "/business/login.jsp" })
	@ResponseBody
	public String businessLogin(HttpServletRequest request, HttpServletResponse response) {
		HtModule m = HtModBusiness.getIntance().getModelLogin();

		RequestValue rv = new RequestValue(request, request.getSession());
		if ("ADM_USER.Frame.SysChangePwd".equalsIgnoreCase(rv.s("itemName"))) {
			// 修改密码
			m.setItemName("ADM_USER.Frame.SysChangePwd");
		}
		boolean fpLogin = rv.isNotBlank(ValidBase.FP_UNID) && rv.isNotBlank(ValidBase.FP_VALIDCODE);
		if (fpLogin) {
			PageValue pv = new PageValue();
			pv.setName(FrameParameters.EWA_VALIDCODE_CHECK);
			pv.setValue("NOT_CHECK");
			pv.setDataType("string");
			pv.setLength(100);
			pv.setPVTag(PageValueTag.SYSTEM );
			
			rv.getPageValues().addValue(pv);
			
			rv.addOrUpdateValue(FrameParameters.EWA_POST, 1);
		}
		rv.getPageValues().remove("ADM_ID");

		String result = m.executeHtmlControl(rv, response);
		if (fpLogin) {
			// && rv.isNotBlank("ref")
			if (m.getHtmlControl().getRequestValue().isNotBlank("ADM_ID")) { // 登录成功
				if (rv.isNotBlank("ref")) {
					String ref = rv.s("ref");
					try {
						response.sendRedirect(ref);
						return null;
					} catch (IOException e) {
						return UJSon.rstFalse(e.getLocalizedMessage()).toString();
					}
				}
				return UJSon.rstTrue("logined").toString();
			}
		}
		return result;
	}

	/**
	 * 商户系统退出登录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping({ "/business/login_exit", "/business/login_exit.jsp" })
	@ResponseBody
	public String businessLoginExit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Login.clearLoginCredentials(request, response);

		response.sendRedirect(request.getContextPath() + "/business/");

		return null;

	}

	/**
	 * 输出二维码，参数如下：<br>
	 * msg 二维码信息<br>
	 * width 二维码宽带和高度，限制为100 - 1000<br>
	 * logo 附加到二维码中心的图片url，必须是本地上传的图片在UPath.getPATH_UPLOAD()定义的目录里 <br>
	 * show 显示方式：=base64 返回图片的base64编码，=url图片的网址，=其它，图片的二进制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/business/qrcode", "/business/qrcode.jsp" })
	@ResponseBody
	public String qrCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequestValue rv = new RequestValue(request);
		if (!Login.isSupplyLogined(rv)) {
			return "not logined";
		}
		HttpQRCode q = new HttpQRCode();
		return q.response(request, response);
	}

	/**
	 * 在线查看或下载oa文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/business/oa-file-view", "/business/oa-file-view.jsp" })
	@ResponseBody
	public String oaFileView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pdfjs = request.getContextPath() + "/opensource/pdfjs/pdfjs-2.10.377-dist/web/viewer.html";
		HttpOaFileView o = new HttpOaFileView(pdfjs);

		return o.response(request, response);
	}

	/**
	 * 在线查看或下载 sys_atts 文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/business/sys-att", "/business/sys-att.jsp" })
	@ResponseBody
	public String oaSysAttView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pdfjs = request.getContextPath() + "/opensource/pdfjs/pdfjs-2.10.377-dist/web/viewer.html";
		HttpOaSysAttView o = new HttpOaSysAttView(pdfjs);

		return o.response(request, response);
	}

}
