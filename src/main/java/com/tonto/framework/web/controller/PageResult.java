package com.tonto.framework.web.controller;

import com.tonto.framework.service.PageBound;

public  class PageResult {
	
	private int page;
	
	private int limit;
	
	private int totalCount;
	
	private Object dataList;
	
	public PageResult(PageBound pageBound)
	{
		this.page = pageBound.getPage();
		this.limit = pageBound.getLimit();
		this.totalCount = pageBound.getTotalCount();
		this.dataList = pageBound.getDataList();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Object getDataList() {
		return dataList;
	}

	public void setDataList(Object dataList) {
		this.dataList = dataList;
	}
	
}
