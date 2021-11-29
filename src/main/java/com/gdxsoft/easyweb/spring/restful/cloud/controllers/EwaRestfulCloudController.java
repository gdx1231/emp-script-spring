package com.gdxsoft.easyweb.spring.restful.cloud.controllers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.gdxsoft.easyweb.conf.ConfRestful;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.script.servlets.RestfulResult;
import com.gdxsoft.easyweb.script.servlets.ServletRestful;
import com.gdxsoft.easyweb.spring.EwaSpingUpload;
import com.gdxsoft.easyweb.utils.UHtml;
import com.gdxsoft.easyweb.utils.UUrl;

public class EwaRestfulCloudController extends ServletRestful {
	public static String SRPING_APPLICATION_NAME = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3196557292600026445L;

	private static Logger LOGGER = LoggerFactory.getLogger(EwaRestfulCloudController.class);
	@Autowired
	private DiscoveryClient discoveryClient;
	@Autowired
	private RestTemplate restTemplate;

	@Bean
	@LoadBalanced // 负载均衡
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		RestTemplate restTemplate = builder.build();
		// 忽略http !=2xx 的错误
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {

			}
		});

		return restTemplate;
	}

	@Override
	public void handleUpload(ConfRestful conf, RequestValue rv, HttpServletRequest request,
			RestfulResult<Object> result) {
		try {
			super.initUploadParameters(conf, rv, result);
		} catch (Exception e2) {
			result.setSuccess(false);
			result.setCode(500);
			result.setHttpStatusCode(500);
			result.setData(e2.getMessage());
			LOGGER.error(e2.getMessage());
			return;
		}
		EwaSpingUpload.handleUpload(rv, request, result);
	}

	/**
	 * 消费者调用，从服务中心获取请求地址
	 * 
	 * @param pathCloud
	 * @param pathTarget
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */

	public String ewaConsumerRestfulHandler(String pathCloud, String pathTarget, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String serviceName = SRPING_APPLICATION_NAME;
		if (StringUtils.isBlank(serviceName)) {
			LOGGER.error("请在Applcation.run的时候，设置 STRING_APPLICATION_NAME");
			return "请在Applcation.run的时候，设置 STRING_APPLICATION_NAME";
		}

		List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
		StringBuilder urls = new StringBuilder();
		for (ServiceInstance instance : instances) {
			urls.append(instance.getHost() + ":" + instance.getPort()).append(",");
		}
		LOGGER.debug("{}: {}", serviceName, urls.toString());

		// 这里配置的是我们要调用的服务实例名
		String restUrlPrefix = "http://" + serviceName;

		UUrl u = new UUrl(request);
		String path = u.getName();
		// path = path.replace("/ewa-api-cloud/", "/ewa-api/");
		path = path.replace(pathCloud, pathTarget);

		String url = restUrlPrefix + request.getContextPath() + path;
		LOGGER.debug(url);

		String httpMethod = request.getMethod();
		httpMethod = httpMethod == null ? "" : httpMethod.toUpperCase().trim();
		HttpMethod restMethod = HttpMethod.resolve(httpMethod);

		// 传递 headers
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			String val = request.getHeader(key);
			headers.add(key, val);
		}
		// 传递body
		String body = this.loadBodyContetnt(request);

		HttpEntity<String> restHttpEntity = new HttpEntity<String>(body, headers);

		// 转发请求
		ResponseEntity<String> resp = restTemplate.exchange(url, restMethod, restHttpEntity, String.class);

		return resp.getBody();

	}

	private String loadBodyContetnt(HttpServletRequest request) {
		String bodyContent = null;
		try {
			bodyContent = UHtml.getHttpBody(request);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
		}
		return bodyContent;

	}
}
