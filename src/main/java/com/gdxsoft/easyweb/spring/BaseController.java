package com.gdxsoft.easyweb.spring;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import com.gdxsoft.easyweb.data.DTTable;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.web.dao.SiteMain;
import com.gdxsoft.web.dao.SiteNavCat;
import com.gdxsoft.web.site.Site;

public class BaseController {

	private SiteUtils siteUtils;
	private static Map<String, Object> CACHE = new ConcurrentHashMap<>();

	/**
	 * 跳转到 Error页面
	 * 
	 * @param msg
	 * @param model
	 */
	public String showError(String msg, Model model) {
		model.addAttribute("msg", msg);
		return "error";
	}

	@SuppressWarnings("unchecked")
	public SiteUtils commonData(HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			request.setCharacterEncoding("utf8");
			response.setCharacterEncoding("utf8");
		} catch (UnsupportedEncodingException e) {
		}

		SiteUtils su = new SiteUtils(request);

		if (su.getRv().s("EWA_AJAX") != null && !"INSTALL".equalsIgnoreCase(su.getRv().s("EWA_AJAX"))) {
			return su;
		}

		boolean isNew = su.getRv().s("new") != null;

		Site site = su.getSite();
		SiteMain siteMain = site.getSiteMain();

		String sitename = su.isEn() ? siteMain.getSitNameEn() : siteMain.getSitName();

		// 头部导航
		String tag = "navsHeader." + su.isEn();
		ArrayList<SiteNavCat> navsHeader;
		if (CACHE.containsKey(tag) && !isNew) {
			navsHeader = (ArrayList<SiteNavCat>) CACHE.get(tag);
		} else {
			navsHeader = site.getNavsHeader();
			if (navsHeader != null) {
				CACHE.put(tag, navsHeader);
			}
		}
		// 头部导航-右侧
		String tagRight = "navsHeaderRight." + su.isEn();
		ArrayList<SiteNavCat> navsHeaderRight;
		if (CACHE.containsKey(tagRight) && !isNew) {
			navsHeaderRight = (ArrayList<SiteNavCat>) CACHE.get(tagRight);
		} else {
			navsHeaderRight = site.getNavsByName("SITE_NAVS_HEADER_R");
			if (navsHeaderRight != null) {
				CACHE.put(tagRight, navsHeaderRight);
			}
		}

		// 头部导航-右侧
		String tagRightUsr = "navsHeaderRightUsr." + su.isEn();
		ArrayList<SiteNavCat> navsHeaderRightUsr;
		if (CACHE.containsKey(tagRightUsr) && !isNew) {
			navsHeaderRightUsr = (ArrayList<SiteNavCat>) CACHE.get(tagRightUsr);
		} else {
			navsHeaderRightUsr = site.getNavsByName("SITE_NAVS_HEADER_USR");
			if (navsHeaderRightUsr != null) {
				CACHE.put(tagRightUsr, navsHeaderRightUsr);
			}
		}

		// 底部导航
		String tagFooter = "navsFooter." + su.isEn();
		ArrayList<SiteNavCat> navsFooter;
		if (CACHE.containsKey(tagFooter) && !isNew) {
			navsFooter = (ArrayList<SiteNavCat>) CACHE.get(tagFooter);
		} else {
			navsFooter = site.getNavsFooter();
			if (navsFooter != null) {
				CACHE.put(tagFooter, navsFooter);
			}
		}

		model.addAttribute("isEn", su.isEn());
		model.addAttribute("isMin", su.isMin());

		model.addAttribute("title", sitename);
		model.addAttribute("cp", request.getContextPath() + "/");
		model.addAttribute("navsHeader", navsHeader);
		model.addAttribute("navsFooter", navsFooter);
		model.addAttribute("navsHeaderRight", navsHeaderRight);
		model.addAttribute("navsHeaderRightUsr", navsHeaderRightUsr);

		model.addAttribute("ewa_lang", su.getRv().getLang());

		if (navsFooter != null) {
			String footerLinks = this.createFooterLinks(navsFooter, su.getRv());
			model.addAttribute("footerLinks", footerLinks);
		}

		String domain = su.getRv().getRequest().getServerName();
		if (domain == null) {
			domain = "";
		} else {
			domain = domain.toLowerCase();
		}

		// 备案信息
		model.addAttribute("beian", site.getSiteMain().getSitBeian());
		// copyright
		String copyright = su.isEn() ? su.getSite().getSiteMain().getSitCwEn() : su.getSite().getSiteMain().getSitCw();
		if (StringUtils.isBlank(copyright)) {
			copyright = "";
		} else {
			copyright = su.getRv().replaceParameters(copyright);
		}
		model.addAttribute("copyright", copyright);

		String ua=su.getRv().s("sys_user_agent");
		
		this.siteUtils = su;
		return su;
	}

	private String createFooterLinks(ArrayList<SiteNavCat> navsFooter, RequestValue rv) {
		boolean isEn = rv.getLang().equals("enus");

		String tag = "createFooterLinks." + isEn;
		if (CACHE.containsKey(tag) && rv.s("new") == null) {
			return CACHE.get(tag).toString();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select a.NWS_SUBJECT, a.nws_guid, a.NWS_CAT_UNID,NWS_SRC2 from v_nws_main_cat a ");
		sb.append("where NWS_CAT_TAG = @nws_cat_tag \n");
		sb.append("and sit_id=@sit_id");
		if (isEn) {
			sb.append(" and nws_auth1 = 'en'");
		} else {
			sb.append(" and nws_auth1 != 'en'");
		}
		sb.append(" AND NWS_TAG = 'WEB_NWS_DLV' order by NRM_ORD");
		String sql1 = sb.toString();

		StringBuilder sb1 = new StringBuilder();
		for (int i = 0; i < navsFooter.size(); i++) {
			SiteNavCat nav = navsFooter.get(i);
			rv.addOrUpdateValue("NWS_CAT_TAG", nav.getSitNavName());
			rv.addOrUpdateValue("SIT_ID", nav.getSitId());
			DTTable tb1 = DTTable.getJdbcTable(sql1, rv);
			sb1.append("<div class='col-xl-3 col-md-3'>");
			sb1.append("<h3>" + (isEn ? nav.getSitNavNameEn() : nav.getSitNavName()) + "</h3>");
			String links;
			try {
				links = this.createLinks(tb1, rv);
			} catch (Exception e) {
				links = e.getMessage();
			}
			sb1.append(links);
			sb1.append("</div>");

		}
		CACHE.put(tag, sb1.toString());
		return sb1.toString();
	}

	private String createLinks(DTTable tb1, RequestValue rv) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<ul>");
		for (int m = 0; m < tb1.getCount(); m++) {
			String subject = tb1.getCell(m, "NWS_SUBJECT").toString();
			String guid = tb1.getCell(m, "nws_guid").toString();
			String catUnid = tb1.getCell(m, "NWS_CAT_UNID").toString();

			String target = "";
			String url;
			if (tb1.getCell(m, "NWS_SRC2").isNull() || tb1.getCell(m, "NWS_SRC2").toString().trim().length() == 0) {
				url = rv.getLang() + "/nws/" + catUnid + "/" + guid;
			} else {
				url = tb1.getCell(m, "NWS_SRC2").toString();
				if (url.toLowerCase().endsWith("http://") || url.toLowerCase().endsWith("https://")) {
					target = "target='_blank'";
				} else {
					url = rv.getLang() + "/" + url;
				}
			}

			String li = "<li><a href='" + url + "' " + target + ">" + subject + "</a></li>";
			sb.append(li);
		}
		sb.append("</ul>");
		return sb.toString();
	}

	/**
	 * @return the siteUtils
	 */
	public SiteUtils getSiteUtils() {
		return siteUtils;
	}

	/**
	 * @param siteUtils the siteUtils to set
	 */
	public void setSiteUtils(SiteUtils siteUtils) {
		this.siteUtils = siteUtils;
	}

}
