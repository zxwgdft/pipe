package com.tonto.framework.web.controller;

public class StatusResponse{
	
	private Status status;
	private String message;
	
	public StatusResponse(){
		
	}
	
	public StatusResponse(Status status)
	{
		this.status = status;
	}
	
	
	public StatusResponse(Status status, String message)
	{
		this.status = status;
		this.message = message;
	}
	
	public static enum Status {
		
		SUCCESS,
		ERROR,
		DEFAULT,
		PROCESSING,
		WARNING;
		
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
