package com.tonto.framework.web.controller;

public abstract class Response {
	
	public static final int STATUS_NO_LOGIN=-1;
	public static final int STATUS_NO_PERMISSION=-2;
	
	public static final int STATUS_ERROR=0;	// 系统异常
	public static final int STATUS_SUCCESS=1;		// 成功
	public static final int STATUS_FAIL=2;		// 失败
}
