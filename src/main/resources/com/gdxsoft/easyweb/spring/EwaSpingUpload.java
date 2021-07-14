package com.gdxsoft.easyweb.spring;

import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.script.servlets.RestfulResult;
import com.gdxsoft.easyweb.uploader.FileUpload;
import com.gdxsoft.easyweb.uploader.Upload;
import com.gdxsoft.easyweb.utils.UFile;
import com.gdxsoft.easyweb.utils.Utils;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * SpringBoot Upload
 * 
 * @author admin
 *
 */
public class EwaSpingUpload extends Upload {
	private static Logger LOGGER = LoggerFactory.getLogger(EwaSpingUpload.class);

	public static void handleUpload(RequestValue rv, HttpServletRequest request, RestfulResult<Object> result) {
		MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);

		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			LOGGER.warn(e1.getMessage());
		}

		EwaSpingUpload up = new EwaSpingUpload();
		up.setRv(rv);
		try {
			up.init(request);
			String uploadName = up.getUploadName();
			List<MultipartFile> items = params.getFiles(uploadName);
			up.setUploadItems(items);

			String s = up.upload();

			result.setSuccess(true);
			result.setCode(200);
			result.setHttpStatusCode(200);
			result.setData(new JSONArray(s));

		} catch (Exception e2) {
			result.setSuccess(false);
			result.setCode(500);
			result.setHttpStatusCode(500);
			result.setData(e2.getMessage());
			LOGGER.error(e2.getMessage());
		}
	}

	public String upload() throws Exception {
		if (super.getUploadDir() == null) {
			throw new Exception("Fail to initialized -> UploadDir is null");
		}

		int fileIndex = 0;

		// 处理文件上传
		for (int i = 0; i < super.getUploadItems().size(); i++) {
			MultipartFile item = (MultipartFile) super.getUploadItems().get(i);

			try {
				FileUpload fu = this.takeFileUpload(item, fileIndex);
				super.handleSubs(fu);
				if (this.checkValidExt(fu)) {
					super.getAlFiles().add(fu);
					super.createNewSizeImages(fu);
					fileIndex++;
				} else {
					LOGGER.error("upload:" + item.getName() + ", invalid ext");
					throw new Exception("upload:" + item.getName() + ", invalid ext");
				}
			} catch (Exception err) {
				LOGGER.error("upload:" + item.getName() + ", " + err.getMessage());
				throw err;
			}

		}

		super.handleClientNewSizes();

		// 调用SQL
		if (super.getUpSql() != null && super.getUpSql().trim().length() > 0) {
			this.writeToDatabase();
		}
		return createJSon();
	}

	@Override
	public FileUpload takeFileUpload(Object uploadItem, int index) {
		MultipartFile file = (MultipartFile) uploadItem;
		FileUpload fu = new FileUpload();
		fu.setContextType(file.getContentType());
		String name = file.getOriginalFilename();

		// 用户本地文件名称
		fu.setUserLocalPath(name);
		String ext = UFile.getFileExt(name);
		String guid = Utils.getGuid();
		String fileName = guid + "_" + index;

		if (ext.length() > 0) {
			fileName = fileName + "." + ext;
		}
		String serverName = super.getUploadDir() + "/" + fileName;

		fu.setExt(ext);
		fu.setSaveFileName(fileName);
		fu.setSavePath(this.getUploadRealDir() + File.separator + fileName);
		fu.setFileUrl(serverName);
		fu.setUnid(guid);

		File uploadedFile = new File(fu.getSavePath());
		String url = super.createURL(uploadedFile);
		fu.setFileUrl(url);
		try {
			UFile.createBinaryFile(uploadedFile.getAbsolutePath(), file.getBytes(), true);
			fu.setUploadedFile(uploadedFile);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return fu;
	}
}
