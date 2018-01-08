package com.tonto.framework.web.custom;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.tonto.framework.service.PageBound;
import com.tonto.framework.web.custom.util.ParameterMapHelper;

public class PaginationMethodProcessor implements HandlerMethodArgumentResolver {

	private String limitField = "limit";
	private String pageField = "page";
	private String offsetField = "offset";

	private int defaultLimit = 20;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return PageBound.class == parameter.getParameterType();
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		Integer limit = null, page = null, offset = null;
		
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		
		if (parameterMap != null) {
			ParameterMapHelper paramMapHelper = new ParameterMapHelper(parameterMap);

			limit = paramMapHelper.getIntegerParameter(limitField);
			page = paramMapHelper.getIntegerParameter(pageField);
			offset = paramMapHelper.getIntegerParameter(offsetField);

		}

		int l = (limit == null || limit <= 0) ? defaultLimit : limit;
		int p = (page == null || page <= 0) ? 1 : page;
		int o = (offset == null || offset < 0) ? 0 : offset;

		if (page == null)
			p = o / l + 1;

		return new PageBound(p, l);
	}

	// ----------------------------- 返回对象处理 ----------------------------


	// ----------- CONFIG ----------

	public String getLimitField() {
		return limitField;
	}

	public void setLimitField(String limitField) {
		this.limitField = limitField;
	}

	public String getPageField() {
		return pageField;
	}

	public void setPageField(String pageField) {
		this.pageField = pageField;
	}

	public String getOffsetField() {
		return offsetField;
	}

	public void setOffsetField(String offsetField) {
		this.offsetField = offsetField;
	}

}
