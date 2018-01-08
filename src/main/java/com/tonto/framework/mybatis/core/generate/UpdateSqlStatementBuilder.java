package com.tonto.framework.mybatis.core.generate;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.type.JdbcType;

import com.tonto.framework.mybatis.core.SqlStatement;

public class UpdateSqlStatementBuilder implements SqlStatementBuilder {

	@Override
	public List<SqlStatement> build(BuildContainer buildContainer) {
		List<SqlStatement> sqlStatements = new ArrayList<>();

		for (BuildTable buildTable : buildContainer.getChildren()) {
			sqlStatements.add(buildUpdate(buildTable));
		}

		return sqlStatements;
	}

	private SqlStatement buildUpdate(BuildTable buildTable) {
		Class<?> modelType = buildTable.getModelType();
		String id = modelType.getName() + ".update";

		List<BuildColumn> primaryBuildColumns = buildTable.getPrimaryBuildColumns();

		if (primaryBuildColumns == null || primaryBuildColumns.size() == 0)
			return null;

		StringBuilder sql = new StringBuilder();

		sql.append("UPDATE ").append(buildTable.getTableName()).append(" SET ");

		BuildColumn[] buildColumns = buildTable.getChildren();
		
		if(buildColumns.length == primaryBuildColumns.size())
			return null;
		
		for (int i = 0; i < buildColumns.length; i++) {
			BuildColumn buildColumn = buildColumns[i];

			if (!primaryBuildColumns.contains(buildColumn)) {
				if (i > 0)
					sql.append(", ");
				sql.append(buildColumn.getColumnName()).append("=").append(getSetSql(buildColumn));
			}
		}

		for (int i = 0; i < primaryBuildColumns.size(); i++) {
			BuildColumn buildColumn = primaryBuildColumns.get(i);

			if (i == 0)
				sql.append(" WHERE ").append(buildColumn.getColumnName()).append("=").append(getSetSql(buildColumn));
			else
				sql.append(" AND ").append(buildColumn.getColumnName()).append("=").append(getSetSql(buildColumn));
		}

		return new SqlStatement(id, sql.toString(), SqlCommandType.UPDATE, Integer.class, modelType);
	}

	private String getSetSql(BuildColumn buildColumn) {
		JdbcType jdbcType = buildColumn.getJdbcType();
		return "#{" + buildColumn.fieldName + (jdbcType == null ? "" : ",jdbcType=" + buildColumn.getJdbcType()) + "}";
	}

}
