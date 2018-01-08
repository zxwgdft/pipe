package com.tonto.framework.web.controller;

import com.tonto.framework.service.PageBound;

public class PageRequest {

	private int limit;
	private int offset;
	private int page;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public PageBound getPageBound() {

		if (limit <= 0)
			limit = PageBound.DEFAULT_LIMIT;

		if (offset != 0 && page == 0)
			page = offset / limit + 1;

		return new PageBound(page, limit);
	}

}
