package com.tonto.framework.web.custom.util;

import java.util.Map;

import org.apache.log4j.Logger;

public class ParameterMapHelper {

	private static final Logger logger = Logger.getLogger(ParameterMapHelper.class);

	Map<String, String[]> parameterMap;

	public ParameterMapHelper(Map<String, String[]> parameterMap) {
		if (parameterMap == null)
			throw new IllegalArgumentException("ParameterMap参数不能为Null");
		this.parameterMap = parameterMap;
	}

	public Integer getIntegerParameter(String name) {

		String[] valStrs = parameterMap.get(name);
		if (valStrs != null && valStrs.length > 0) {
			try {
				return Integer.parseInt(valStrs[0]);
			} catch (Exception e) {
				if (logger.isDebugEnabled())
					logger.debug("参数[" + name + ":" + valStrs[0] + "] 无法转化为Integer");
			}
		}

		return null;
	}

	public String getParameter(String name) {
		String[] valStrs = parameterMap.get(name);
		return valStrs != null ? valStrs[0] : null;
	}

}
