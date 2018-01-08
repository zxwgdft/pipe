package com.tonto.data.database.model;

public class DataBase extends PathContainer<Table>{
	
	String name;	
	
	public DataBase(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
