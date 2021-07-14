package com.gdxsoft.easyweb.spring.restful.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gdxsoft.easyweb.conf.ConfRestful;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.script.servlets.RestfulResult;
import com.gdxsoft.easyweb.script.servlets.ServletRestful;
import com.gdxsoft.easyweb.spring.EwaSpingUpload;

@Controller
public class EwaRestfulController extends ServletRestful {
	
	/**
	 * 解决Jackson导致Long型数据精度丢失问题
	 * 
	 * @return
	 */
	@Bean("jackson2ObjectMapperBuilderCustomizer")
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		Jackson2ObjectMapperBuilderCustomizer customizer = new Jackson2ObjectMapperBuilderCustomizer() {
			@Override
			public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
				jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance)
						.serializerByType(Long.TYPE, ToStringSerializer.instance);
			}
		};
		return customizer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5150979543940223199L;

	private static Logger LOGGER = LoggerFactory.getLogger(EwaRestfulController.class);

	/**
	 * EWA restful api ,defined int ewa_conf.xml<br>
	 * &lt;restfuls path="/ewa-api/v1.1"&gt;
	 */
	@RequestMapping(value = "/ewa-api/**", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String ewaRestfulHandler(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String result = super.ewaRestfulHandler(request, response);

		return result;
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

}
