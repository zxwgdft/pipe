package com.tonto.framework.service;

import java.lang.reflect.Array;
import java.util.List;

public final class PageBound {

	public final static int DEFAULT_LIMIT = 20;

	private int page;

	private int limit;

	private int totalCount;

	private Object dataList;

	public PageBound(int page, int limit) {

		if (page <= 0)
			page = 1;
		if(limit <= 0)
			limit = DEFAULT_LIMIT;
			

		this.page = page;
		this.limit = limit;
	}

	public PageBound() {
		this(1, DEFAULT_LIMIT);
	}

	public PageBound(int limit) {
		this(1, limit);
	}

	public int getOffset() {
		return (page - 1) * limit;
	}

	public int getLimit() {
		return limit;
	}

	public int getPage() {
		return page;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {

		if (totalCount < 0)
			throw new IllegalArgumentException("数据记录总数不能小于0");

		this.totalCount = totalCount;

		if (totalCount == 0) {
			page = 0;
		} else {

			int totalPage = ((totalCount % limit == 0) ? totalCount / limit : totalCount / limit + 1);
			if (page > totalPage)
				page = totalPage;
		}

	}

	private void setEmpty() {

		totalCount = 0;
		page = 0;
		dataList = null;

	}

	/**
	 * 设置数据源，自动获取到PAGE LIST
	 * 
	 * @param dataSource
	 */
	public void setDataSource(Object dataSource) {

		if (dataSource == null) {
			setEmpty();
		} else {
			if (dataSource instanceof List) {
				List<?> listSource = (List<?>) dataSource;
				int length = listSource.size();

				if (length == 0)
					setEmpty();
				else {
					setTotalCount(length);
					int from = getOffset();
					int to = from + limit;
					if (to > length)
						to = length;

					dataList = listSource.subList(from, to);

				}
			} else if (dataSource.getClass().isArray()) {
				int length = Array.getLength(dataSource);
				if (length == 0)
					setEmpty();
				else {

					setTotalCount(length);
					int from = getOffset();
					int to = from + limit;
					if (to > length)
						to = length;

					int newLength = to - from;
					Class<?> type = dataSource.getClass();

					Object copy = (type == Object[].class) ? new Object[newLength] : Array.newInstance(type.getComponentType(), newLength);
					System.arraycopy(dataSource, from, copy, 0, newLength);
					dataList = copy;
				}

			} else {
				throw new IllegalArgumentException("数据源必须为List或者Array数组");
			}

		}

	}

	public Object getDataList() {
		return dataList;
	}

	public void setDataList(Object dataList) {
		this.dataList = dataList;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
