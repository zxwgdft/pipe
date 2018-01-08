package com.tonto.framework.mybatis.core;

import org.apache.ibatis.mapping.SqlCommandType;

public class SqlStatement {

	String id;
	String sql;
	SqlCommandType sqlCommandType;
	Class<?> resultType;
	Class<?> parameterType;

	public SqlStatement(String id, String sql, SqlCommandType sqlCommandType, Class<?> resultType, Class<?> parameterType) {
		this.id = id;
		this.sql = sql;
		this.sqlCommandType = sqlCommandType;
		this.resultType = resultType;
		this.parameterType = parameterType;
	}

	public SqlStatement(String id) {
		this.id = id;
	}

	public SqlStatement() {

	}

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

	public Class<?> getResultType() {
		return resultType;
	}

	public void setResultType(Class<?> resultType) {
		this.resultType = resultType;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}

	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ID:").append(id).append(",TYPE:").append(sqlCommandType).append(",SQL:").append(sql).append(",PARAMETER:")
				.append(parameterType).append(",RESULT:").append(resultType).append("]");
		return sb.toString();
	}

}
