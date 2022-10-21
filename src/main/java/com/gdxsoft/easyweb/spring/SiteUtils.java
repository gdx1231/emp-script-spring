package com.gdxsoft.easyweb.spring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import com.gdxsoft.easyweb.data.DTTable;
import com.gdxsoft.easyweb.script.HtmlControl;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.utils.UImages;
import com.gdxsoft.easyweb.utils.UObjectValue;
import com.gdxsoft.easyweb.utils.UPath;
import com.gdxsoft.web.dao.NwsCat;
import com.gdxsoft.web.dao.NwsCatDao;
import com.gdxsoft.web.site.Site;

public class SiteUtils {
	/**
	 * 网站的SITE_UNID，从在ewa_conf的 initparas-&gt;para中定义
	 */
	public static String SITE_UNID;
	/**
	 * 新闻默认的配置文件
	 */
	public static String NEWS_XMLNAME = "/front-web/news.xml";
	static {
		UPath.initPath();
		// 网站的SITE_UNID，从在ewa_conf的 initparas-&gt;para中定义
		String siteUnid = UPath.getInitPara("site_unid");
		SITE_UNID = siteUnid;
	}

	private RequestValue rv;
	private HtmlControl ewa;
	private HtmlControl ewa1;
	private HtmlControl ewa2;
	private HtmlControl ewa3;
	private HtmlControl ewa4;

	public SiteUtils(HttpServletRequest request) {
		this.rv = new RequestValue(request);
	}

	public String newsCoverPic( HttpServletResponse response, String resize) throws IOException {
		String transparent= "/EmpScriptV2/EWA_STYLE/images/transparent.png";
		String sql = "select nws_head_pic from nws_main where nws_guid = @nws_guid";
		DTTable tb = DTTable.getJdbcTable(sql, "", rv);
		if (tb.getCount() == 0) {
			response.sendRedirect(transparent);
			return null;
		}
		String phy = tb.getCell(0, 0).toString();
		if (phy == null || phy.trim().length() == 0) {
			response.sendRedirect(transparent);
			return null;
		}

		String f1 = UPath.getPATH_UPLOAD() + "../" + phy;

		File file = new File(f1);
		if (!file.exists()) {
			response.sendRedirect(transparent);
			return null;
		}

		String md5 = "GdX" + phy.hashCode() + "";

		String IfNoneMatch = rv.getRequest().getHeader("If-None-Match");
		if (IfNoneMatch != null) {
			if (("W/" + md5).equals(IfNoneMatch)) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return null;
			}
		}
		if (md5 != null) {
			response.addHeader("ETag", "W/" + md5);
		}


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
			response.sendRedirect(transparent);
		}
		return null;
	}
	
	public void handleKeyWords(Model model, String keyWords, String channelLink) {
		if (keyWords == null || keyWords.trim().length() == 0) {
			return;
		}

		String[] arr = keyWords.split(",");
		for (int i = 0; i < arr.length; i++) {
			String keyWord = arr[i].trim().toLowerCase();
			String html = this.handleKeyWord(model, keyWord, channelLink);
			model.addAttribute("html_keyword" + i, html);
		}

	}

	public String handleKeyWord(Model model, String keyWord, String channelLink) {
		return "";

	}

	public NwsCat getNwsCatByNwsCatTag(String nwsCatTag) {
		int siteId = this.getSite().getSiteMain().getSitId();
		NwsCatDao d = new NwsCatDao();
		RequestValue rv = this.getRv();
		rv.addOrUpdateValue("nws_cat_tag", nwsCatTag);
		rv.addOrUpdateValue("site_id", siteId);
		d.setRv(rv);
		String where = " nws_cat_tag = @nws_cat_tag and nws_cat_status='USED' and sit_id=@site_id";

		ArrayList<NwsCat> al = d.getRecords(where);
		if (al.size() == 0) {
			return null;
		} else {
			return al.get(0);
		}

	}

	public String nwsCatAndDocByNwsCatUnid(Model model, String nwsCatUnid, String nwsGuid, String returnName) {
		rv.addOrUpdateValue("nws_cat_Unid", nwsCatUnid);
		String sql = "select NWS_CAT_ID, NWS_CAT_NAME, NWS_CAT_NAME_EN from nws_cat where nws_cat_Unid=@nws_cat_Unid  and SIT_ID="
				+ this.getSite().getSiteMain().getSitId();
		DTTable tb = DTTable.getJdbcTable(sql, rv);
		String errcontent = "";
		if (tb.getCount() == 0) {
			errcontent = "找不到对应的目录编号 ";
			model.addAttribute("errcontent", errcontent);
			return "error";
		}

		int catId = tb.getCell(0, 0).toInt();

		this.nwsCatAndDocByCatId(model, catId, nwsGuid, returnName + "/" + nwsCatUnid);

		model.addAttribute("channelLink", rv.getLang() + "/nws/" + nwsCatUnid);
		model.addAttribute("channelText", tb.getCell(0, this.isEn() ? 2 : 1).toString());
		return returnName;
	}

	/**
	 * 返回Grid格式的列表
	 * 
	 * @param model
	 * @param nwsCatUnid
	 * @param returnName
	 * @return
	 */
	public String nwsCatGridByNwsCatUnid(Model model, String nwsCatUnid, String linkName) {
		rv.addOrUpdateValue("nws_cat_Unid", nwsCatUnid);
		String sql = "select NWS_CAT_ID, NWS_CAT_NAME, NWS_CAT_NAME_EN from nws_cat where nws_cat_Unid=@nws_cat_Unid  and SIT_ID="
				+ this.getSite().getSiteMain().getSitId();
		DTTable tb = DTTable.getJdbcTable(sql, rv);
		String errcontent = "";
		if (tb.getCount() == 0) {
			errcontent = "找不到对应的目录编号  ";
			model.addAttribute("errcontent", errcontent);
			return "error";
		}

		HtmlControl ht = new HtmlControl();
		String xmlName = NEWS_XMLNAME;
		String itemName = "nws_main.LF.GRID.PIC";

		// 添加上语言属性的的 link
		String channel = this.rv.getLang() + "/" + linkName;
		String paras = "channel=" + channel;

		ht.init(xmlName, itemName, paras, this.rv, null);
		model.addAttribute("ewa", ht);
		this.ewa = ht;
		if (this.rv.s("ewa_ajax") != null) {
			return "ewaAjax";
		}

		model.addAttribute("channelLink", rv.getLang() + "/nws/" + nwsCatUnid);
		model.addAttribute("channelText", tb.getCell(0, this.isEn() ? 2 : 1).toString());
		return "nwsGrid";
	}

	public String nwsCatGridByNwsCatTag(Model model, String nwsCatTag, String linkName) {
		NwsCat cat = this.getNwsCatByNwsCatTag(nwsCatTag);
		String errcontent = "";
		if (cat == null) {
			errcontent = "找不到对应的目录编号";
			model.addAttribute("errcontent", errcontent);
			return "error";
		}
		return this.nwsCatGridByNwsCatUnid(model, cat.getNwsCatUnid(), linkName);
	}

	/**
	 * 获取文章列表和指定的文章内容
	 * 
	 * @param model
	 * @param nwsCatTag
	 * @param nwsGuid
	 * @param returnName
	 * @return
	 */
	public String nwsCatAndDocByNwsCatTag(Model model, String nwsCatTag, String nwsGuid, String linkExp) {
		NwsCat cat = this.getNwsCatByNwsCatTag(nwsCatTag);
		String errcontent = "";
		if (cat == null) {
			errcontent = "找不到对应的目录编号";
			model.addAttribute("errcontent", errcontent);
			return "error";
		}
		int catId = cat.getNwsCatId().intValue();
		rv.addOrUpdateValue("cid", cat.getNwsCatUnid());

		String rtName = this.nwsCatAndDocByCatId(model, catId, nwsGuid, linkExp);

		model.addAttribute("channelLink", rv.getLang() + "/" + rtName);
		model.addAttribute("channelText", this.isEn() ? cat.getNwsCatNameEn() : cat.getNwsCatName());
		return rtName;
	}

	/**
	 * 获取文章列表和指定的文章内容
	 * 
	 * @param model
	 * @param nwsCatTag
	 * @param returnName
	 * @return
	 */
	public String nwsCatAndDocByNwsCatTag(Model model, String nwsCatTag, String linkExp) {
		NwsCat cat = this.getNwsCatByNwsCatTag(nwsCatTag);
		String errcontent = "";
		if (cat == null) {
			errcontent = "找不到对应的目录编号";
			model.addAttribute("errcontent", errcontent);
			return "error";
		}
		int catId = cat.getNwsCatId().intValue();
		rv.addOrUpdateValue("cid", cat.getNwsCatUnid());

		// 获取第一篇文章
		String nwsGuid = this.getFirstDocGuidOfCatalog(catId);

		String rtName = this.nwsCatAndDocByCatId(model, catId, nwsGuid, linkExp);

		model.addAttribute("channelLink", rv.getLang() + "/" + rtName);
		model.addAttribute("channelText", this.isEn() ? cat.getNwsCatNameEn() : cat.getNwsCatName());
		return rtName;
	}

	/**
	 * 获取目录下的第一篇文章
	 * 
	 * @param catId
	 * @return
	 */
	public String getFirstDocGuidOfCatalog(int catId) {
		// 找到第一篇文章
		String sql1 = "select nws_guid from v_nws_main_cat where NWS_CAT_ID=" + catId
				+ "  AND NWS_TAG = 'WEB_NWS_DLV' ";
		if (this.isEn()) {
			sql1 += " and nws_auth1 = 'en'";
		} else {
			sql1 += " and nws_auth1 != 'en'";
		}
		sql1 += " order by NRM_ORD limit 1";
		DTTable tb1 = DTTable.getJdbcTable(sql1);
		if (tb1.getCount() == 0) {
			return null;
		}
		String nwsGuid = tb1.getCell(0, 0).toString();
		return nwsGuid;
	}

	/**
	 * 
	 * @param model
	 * @param catId
	 * @param nwsGuid
	 * @param channel 新闻列表链接表达式<br>
	 *                /zhcn/news/[NWS_CAT_UNID]/[NWS_GUID].html
	 * @return 模板名称
	 */
	public String nwsCatAndDocByCatId(Model model, int catId, String nwsGuid, String channel) {
		String errcontent = "";
		if (nwsGuid == null) {
			// 找到第一篇文章
			nwsGuid = this.getFirstDocGuidOfCatalog(catId);
			if (nwsGuid == null) {
				errcontent = "找不到可用的文章编号 nws_cat_id=" + catId;
				model.addAttribute("errcontent", errcontent);
				return "error";
			}
		}
		model.addAttribute("nwsGuid", nwsGuid);

		HtmlControl ht = new HtmlControl();

		String itemName = "nws_main.LF.title";

		// 添加上语言属性的的 link
		String paras = "NWS_CAT_ID=" + catId + "&channel=" + channel;

		ht.init(NEWS_XMLNAME, itemName, paras, this.rv, null);
		model.addAttribute("ewa", ht);
		this.ewa = ht;
		if (this.rv.s("ewa_ajax") != null) {
			return "ewaAjax";
		}

		// 文章信息
		String itemName1 = "nws_main.F.V";
		HtmlControl ht1 = new HtmlControl();
		String paras1 = "uid=" + nwsGuid;
		ht1.init(NEWS_XMLNAME, itemName1, paras1, this.rv, null);
		model.addAttribute("ewa1", ht1);

		this.ewa1 = ht1;
		DTTable tbDoc = ht1.getLastTable();

		String channelLink = channel + "/" + nwsGuid;
		try {
			model.addAttribute("title", tbDoc.getCell(0, "NWS_SUBJECT").toString());
			// 处理keywords定义的内容
			String keyWords = tbDoc.getCell(0, "NWS_KEYWORDS").toString();
			this.handleKeyWords(model, keyWords, channelLink);
		} catch (Exception e) {
		}

		// 关联附件
		String itemName2 = "NWS_ATT.Lf.V";
		HtmlControl ht2 = new HtmlControl();
		String paras2 = "nws_guid=" + nwsGuid;
		ht2.init(NEWS_XMLNAME, itemName2, paras2, this.rv, null);
		this.ewa2 = ht2;
		DTTable tbAtts = ht2.getLastTable();
		if (tbAtts.getCount() > 0) {
			model.addAttribute("ewa2", ht2);
		} else {
			// 输出 html
			FakeEwa f = new FakeEwa();
			model.addAttribute("ewa2", f);
		}

		return "nws";
	}

	/**
	 * @return the ewa
	 */
	public HtmlControl getEwa() {
		return ewa;
	}

	/**
	 * @return the ewa1
	 */
	public HtmlControl getEwa1() {
		return ewa1;
	}

	/**
	 * @return the ewa2
	 */
	public HtmlControl getEwa2() {
		return ewa2;
	}

	/**
	 * @return the ewa3
	 */
	public HtmlControl getEwa3() {
		return ewa3;
	}

	/**
	 * @return the ewa4
	 */
	public HtmlControl getEwa4() {
		return ewa4;
	}

	public Site getSite() {
		if (this.rv.s("new") == null) {
			return Site.getInstance(SITE_UNID, 3600 * 1000); // 3600秒
		} else {
			UObjectValue.GLOBL_CACHED.clear();
			return Site.getInstance(SITE_UNID, 0); // 3600秒
		}
	}

	public boolean isEn() {
		return this.rv.getLang().equals("enus");
	}

	public boolean isMin() {
		return this.rv.s("min") != null;
	}

	/**
	 * @return the rv
	 */
	public RequestValue getRv() {
		return rv;
	}

	/**
	 * @param rv the rv to set
	 */
	public void setRv(RequestValue rv) {
		this.rv = rv;
	}
}
