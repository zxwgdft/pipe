package com.tonto.framework.service.mybatis.pojo;

import org.apache.ibatis.mapping.SqlCommandType;

public class BatisStatement {
	
	String id;
	String sql;
	SqlCommandType sqlCommandType;
	
	Class<?>[] resultTypes;	
	Class<?>[] parameterTypes;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}
	public void setSqlCommandType(SqlCommandType sqlCommandType) {
		this.sqlCommandType = sqlCommandType;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Class<?>[] getResultTypes() {
		return resultTypes;
	}
	public void setResultTypes(Class<?>[] resultTypes) {
		this.resultTypes = resultTypes;
	}

}
