package com.tonto.framework.web.controller.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tonto.framework.service.PageBound;
import com.tonto.framework.service.mybatis.BatisService;
import com.tonto.framework.service.mybatis.pojo.BatisStatement;
import com.tonto.framework.web.controller.CommonResponse;
import com.tonto.framework.web.controller.PageResult;

@Controller
@RequestMapping(value = "/batis")
public class BatisController {

	@Autowired
	BatisService service;

	@RequestMapping(value = "/view")
	public ModelAndView mainView() {
		return new ModelAndView("batis/batisMain");
	}

	@RequestMapping(value = "/statement")
	@ResponseBody
	public CommonResponse getBatisStatements(PageBound pageBound) {	
		BatisStatement[] source = service.getStatements();		
		pageBound.setDataSource(source);	
		return CommonResponse.getSuccessResponse(new PageResult(pageBound));
	}

}
