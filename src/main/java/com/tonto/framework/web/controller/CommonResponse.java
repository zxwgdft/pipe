package com.tonto.framework.web.controller;

public class CommonResponse {

	private int status;
	private String message;
	private Object result;

	public CommonResponse() {
	}

	public CommonResponse(int status) {
		this.status = status;
	}

	public CommonResponse(int status, Object result, String msg) {
		this.status = status;
		this.result = result;
		this.message = msg;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static CommonResponse getSuccessResponse() {
		return new CommonResponse(Response.STATUS_SUCCESS);
	}

	public static CommonResponse getSuccessResponse(String msg) {
		return new CommonResponse(Response.STATUS_SUCCESS, null, msg);
	}

	public static  CommonResponse getSuccessResponse(String msg, Object result) {
		return new CommonResponse(Response.STATUS_SUCCESS, result, msg);
	}

	public static  CommonResponse getSuccessResponse(Object result) {
		return new CommonResponse(Response.STATUS_SUCCESS, result, null);
	}

	public static CommonResponse getUnLoginResponse() {
		return new CommonResponse(Response.STATUS_NO_LOGIN);
	}

	public static CommonResponse getUnLoginResponse(String msg) {
		return new CommonResponse(Response.STATUS_NO_LOGIN, null, msg);
	}

	public static CommonResponse getNoPermissionResponse() {
		return new CommonResponse(Response.STATUS_NO_PERMISSION);
	}

	public static CommonResponse getNoPermissionResponse(String msg) {
		return new CommonResponse(Response.STATUS_NO_PERMISSION, null, msg);
	}

	public static CommonResponse getErrorResponse() {
		return new CommonResponse(Response.STATUS_ERROR);
	}

	public static CommonResponse getErrorResponse(String msg) {
		return new CommonResponse(Response.STATUS_ERROR, null, msg);
	}

	public static  CommonResponse getErrorResponse(String msg, Object result) {
		return new CommonResponse(Response.STATUS_ERROR, result, msg);
	}

	public static  CommonResponse getErrorResponse(Object result) {
		return new CommonResponse(Response.STATUS_ERROR, result, null);
	}

	public static CommonResponse getFailResponse() {
		return new CommonResponse(Response.STATUS_FAIL);
	}

	public static CommonResponse getFailResponse(String msg) {
		return new CommonResponse(Response.STATUS_FAIL, null, msg);
	}

	public static  CommonResponse getFailResponse(String msg, Object result) {
		return new CommonResponse(Response.STATUS_FAIL, result, msg);
	}

	public static  CommonResponse getFailResponse(Object result) {
		return new CommonResponse(Response.STATUS_FAIL, result, null);
	}

}
