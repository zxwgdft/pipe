package com.tonto.framework.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		
		//response.setHeader("Access-Control-Allow-Headers", "Content-Type,Accept");
		String allowHeaders = request.getHeader("access-control-request-headers");
		if (allowHeaders != null)
			response.setHeader("Access-Control-Allow-Headers", allowHeaders);

		filterChain.doFilter(request, response);

	}

}
