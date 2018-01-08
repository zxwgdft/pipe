package com.tonto.framework.mybatis.core.generate;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.type.JdbcType;

import com.tonto.framework.mybatis.core.SqlStatement;

public class InsertSqlStatementBuilder implements SqlStatementBuilder {

	@Override
	public List<SqlStatement> build(BuildContainer buildContainer) {

		List<SqlStatement> sqlStatements = new ArrayList<>();

		for (BuildTable buildTable : buildContainer.getChildren()) {
			sqlStatements.add(buildInsert(buildTable));
		}

		return sqlStatements;
	}

	private SqlStatement buildInsert(BuildTable buildTable) {
		Class<?> modelType = buildTable.getModelType();
		String id = modelType.getName() + ".insert";

		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();

		BuildColumn[] buildColumns = buildTable.getChildren();

		for (BuildColumn buildColumn : buildColumns) {
			sql1.append(buildColumn.getColumnName()).append(",");
			sql2.append(getSetSql(buildColumn)).append(",");
		}

		sql1.deleteCharAt(sql1.length() - 1);
		sql2.deleteCharAt(sql2.length() - 1);

		String sql = "INSERT INTO (" + sql1.toString() + ") VALUES (" + sql2.toString() + ")";

		return new SqlStatement(id, sql, SqlCommandType.INSERT, null, modelType);

	}

	private String getSetSql(BuildColumn buildColumn) {
		JdbcType jdbcType = buildColumn.getJdbcType();
		return "#{" + buildColumn.fieldName + (jdbcType == null ? "" : ",jdbcType=" + buildColumn.getJdbcType()) + "}";
	}

}
