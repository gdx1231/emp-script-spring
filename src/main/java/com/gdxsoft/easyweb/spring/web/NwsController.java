package com.gdxsoft.easyweb.spring.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdxsoft.easyweb.data.DTTable;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.spring.BaseController;
import com.gdxsoft.easyweb.spring.SiteUtils;
import com.gdxsoft.easyweb.utils.UImages;
import com.gdxsoft.easyweb.utils.UPath;

@Controller
public class NwsController extends BaseController {

	/**
	 * 新闻的封面图片
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/news-cover-pic")
	public String newsCoverPic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RequestValue rv = new RequestValue(request);
		String sql = "select nws_head_pic from nws_main where nws_id = @nws_id";
		DTTable tb = DTTable.getJdbcTable(sql, "", rv);
		if (tb.getCount() == 0) {
			response.sendRedirect("/static/images/transparent.png");
			return null;
		}
		String phy = tb.getCell(0, 0).toString();
		if (phy == null || phy.trim().length() == 0) {
			response.sendRedirect("/static/images/transparent.png");
			return null;
		}

		String f1 = UPath.getPATH_UPLOAD() + "../" + phy;

		File file = new File(f1);
		if (!file.exists()) {
			response.sendRedirect("/static/images/transparent.png");
			return null;
		}

		String md5 = "GdX" + phy.hashCode() + "";

		String IfNoneMatch = request.getHeader("If-None-Match");
		if (IfNoneMatch != null) {
			if (("W/" + md5).equals(IfNoneMatch)) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return null;
			}
		}
		if (md5 != null) {
			response.addHeader("ETag", "W/" + md5);
		}

		String resize = "300x300";

		String[] resizes = resize.toLowerCase().split("x");
		int w = Integer.parseInt(resizes[0]);
		int h = Integer.parseInt(resizes[1]);
		String exitspic = f1 + "$resized/" + w + "x" + h + ".jpg";
		String newUrl = phy + "$resized/" + w + "x" + h + ".jpg";
		File fileSmallPic = new File(exitspic);
		try {
			if (!fileSmallPic.exists()) {
				UImages.createSmallImage(f1, w, h);
			}
			response.sendRedirect(newUrl);
		} catch (Exception err) {
			response.sendRedirect("/static/images/transparent.png");

		}
		return null;
	}

	/**
	 * 游学资讯
	 * 
	 * @param ewaLang
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/{ewa_lang}/news")
	public String news(@PathVariable("ewa_lang") String ewaLang, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		request.setAttribute("ewa_lang", ewaLang);
		// 574 游学资讯
		String nwsCatUuid = "8bdbf4b6-5dad-4882-82e5-3224319767f9";
		SiteUtils su = this.commonData(request, response, model);

		String returnName = "nwsGrid";

		String return1 = su.nwsCatGridByNwsCatUnid(model, nwsCatUuid, returnName);

		model.addAttribute("channelLink", su.getRv().getLang() + "/news");

		return return1;
	}

	@RequestMapping("/{ewa_lang}/news/{NWS_CAT_UNID}")
	public String news(@PathVariable("ewa_lang") String ewaLang, @PathVariable("NWS_CAT_UNID") String nwsCatUuid,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		request.setAttribute("ewa_lang", ewaLang);

		SiteUtils su = this.commonData(request, response, model);
		String returnName = "nws";

		return su.nwsCatAndDocByNwsCatUnid(model, nwsCatUuid, null, returnName);
	}

	@RequestMapping("/news-info")
	public String news(HttpServletRequest request, HttpServletResponse response, Model model) {
		SiteUtils su = this.commonData(request, response, model);
		String returnName = "nws";
		String nwsCatUuid = su.getRv().s("nws_cat_unid");
		String nwsGuid = su.getRv().s("nws_guid");

		String returnName1 = su.nwsCatAndDocByNwsCatUnid(model, nwsCatUuid, nwsGuid, returnName);

		try {
			// 跳转到第三方网址
			String nws_src2 = su.getEwa1().getLastTable().getCell(0, "nws_src2").toString();
			if (!StringUtils.isBlank(nws_src2)) {
				String s = nws_src2.trim();
				response.getWriter().println(
						"<html><head><meta http-equiv=\"refresh\" content=\"0; url=" + s + "\"></head></html>");
				return null;
			}
		} catch (Exception e) {
		}
		model.addAttribute("channelLink", su.getRv().getLang() + "/news");
		return returnName1;
	}

	@RequestMapping("/{ewa_lang}/news/{NWS_CAT_UNID}/{NWS_GUID}")
	public String news(@PathVariable("ewa_lang") String ewaLang, @PathVariable("NWS_CAT_UNID") String nwsCatUuid,
			@PathVariable("NWS_GUID") String nwsGuid, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		request.setAttribute("ewa_lang", ewaLang);

		SiteUtils su = this.commonData(request, response, model);
		String returnName = "nws";
		return su.nwsCatAndDocByNwsCatUnid(model, nwsCatUuid, nwsGuid, returnName);
	}

	@RequestMapping("/news_att_download/{nws_att_guid}")
	public String newsAttDownload(@PathVariable("nws_att_guid") String nwsAttGuid, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		RequestValue rv = new RequestValue(request);
		rv.addOrUpdateValue("nws_att_guid", nwsAttGuid);
		String sql = "select * from nws_att where nws_att_guid=@nws_att_guid";

		DTTable tb = DTTable.getJdbcTable(sql, rv);

		if (tb.getCount() == 0) {
			String errcontent = "The file not exists";
			model.addAttribute("errcontent", errcontent);
			return "error";
		}

		try {
			String url = tb.getCell(0, "NWS_ATT_URL").toString();
			String ext = tb.getCell(0, "NWS_ATT_EXT").toString();
			String des = tb.getCell(0, "NWS_ATT_DES").toString();
			if ("vod".equals(ext)) {
				String htmlcode = tb.getCell(0, "NWS_ATT_MEMO").toString();
				this.commonData(request, response, model);
				model.addAttribute("html_vod", htmlcode);
				model.addAttribute("title", des);
				return "nws_3vod";
			}
			response.sendRedirect(url);
		} catch (Exception e) {
			model.addAttribute("errcontent", e.getMessage());
			return "error";
		}

		return null;

	}
}
