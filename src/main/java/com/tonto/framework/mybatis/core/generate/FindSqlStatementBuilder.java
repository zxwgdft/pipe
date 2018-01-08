package com.tonto.framework.mybatis.core.generate;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.type.JdbcType;

import com.tonto.framework.mybatis.core.SqlStatement;
import com.tonto.utils.reflect.NameUtil;

public class FindSqlStatementBuilder implements SqlStatementBuilder {

	@Override
	public List<SqlStatement> build(BuildContainer buildContainer) {

		List<SqlStatement> sqlStatements = new ArrayList<>();

		for (BuildTable buildTable : buildContainer.getChildren()) {
			sqlStatements.add(buildFind(buildTable));
			sqlStatements.addAll(buildGet(buildTable));
			
		}

		return sqlStatements;
	}

	/**
	 * Find All
	 * 
	 * @param buildTable
	 * @return
	 */
	private SqlStatement buildFind(BuildTable buildTable) {

		Class<?> modelType = buildTable.getModelType();

		String id = modelType.getName() + ".find";
		String sql = getBaseSelectSql(buildTable);

		return new SqlStatement(id, sql, SqlCommandType.SELECT, modelType, null);
	}
	
	/**
	 * Get Unique Result By Primary Key Or Unique Key
	 * 
	 * @param buildTable
	 * @return
	 */
	private List<SqlStatement> buildGet(BuildTable buildTable) {

		List<SqlStatement> sqlStatements = new ArrayList<>();
		SqlStatement sqlStatement = buildGet(buildTable, buildTable.getPrimaryBuildColumns(), true);
		if (sqlStatement != null)
			sqlStatements.add(sqlStatement);

		List<List<BuildColumn>> uniqueBuildColumns = buildTable.getUniqueBuildColumns();
		if (uniqueBuildColumns != null && uniqueBuildColumns.size() > 0) {
			for (List<BuildColumn> uniqueBuildColumn : uniqueBuildColumns) {
				sqlStatement = buildGet(buildTable, uniqueBuildColumn, false);
				if (sqlStatement != null)
					sqlStatements.add(sqlStatement);
			}
		}

		return sqlStatements;
	}

	/**
	 * 
	 * Get Unique Result By Unique Key
	 * 
	 * @param buildTable
	 * @param uniqueColumns
	 * @param isPrimary
	 * @return
	 */
	private SqlStatement buildGet(BuildTable buildTable, List<BuildColumn> uniqueColumns, boolean isPrimary) {

		if (uniqueColumns != null && uniqueColumns.size() > 0) {

			Class<?> modelType = buildTable.getModelType();
			StringBuilder sql = new StringBuilder(getBaseSelectSql(buildTable));
			String id = modelType.getName() + (isPrimary ? ".get" : ".getBy");
			Class<?> paramType = null;

			if (uniqueColumns.size() == 1) {
				BuildColumn uniqueColumn = uniqueColumns.get(0);
				sql.append(" WHERE ").append(uniqueColumn.getColumnName()).append("=").append(getSetSql(uniqueColumn));

				if (!isPrimary)
					id += NameUtil.firstUpperCase(uniqueColumn.getFieldName());

				paramType = uniqueColumn.getJavaType();
			} else {
				for (int i = 0; i < uniqueColumns.size(); i++) {
					BuildColumn uniqueColumn = uniqueColumns.get(i);

					if (i == 0) {
						sql.append(" WHERE ").append(uniqueColumn.getColumnName()).append("=").append(getSetSql(uniqueColumn));

						if (!isPrimary)
							id += NameUtil.firstUpperCase(uniqueColumn.getFieldName());

					} else {
						sql.append(" AND ").append(uniqueColumn.getColumnName()).append("=").append(getSetSql(uniqueColumn));

						if (!isPrimary)
							id += "And" + NameUtil.firstUpperCase(uniqueColumn.getFieldName());
					}
				}
				paramType = modelType;
			}

			return new SqlStatement(id, sql.toString(), SqlCommandType.SELECT, modelType, paramType);

		}

		return null;
	}
	

	private String getSetSql(BuildColumn buildColumn) {
		JdbcType jdbcType = buildColumn.getJdbcType();
		return "#{" + buildColumn.fieldName + (jdbcType == null ? "" : ",jdbcType=" + buildColumn.getJdbcType()) + "}";
	}
	

	private String getBaseSelectSql(BuildTable buildTable) {

		StringBuilder sb = new StringBuilder("SELECT ");

		BuildColumn[] buildColumns = buildTable.getChildren();
		if (buildColumns == null || buildColumns.length == 0) {
			sb.append(" *");
		} else {
			for (int i = 0; i < buildColumns.length; i++) {
				BuildColumn buildColumn = buildColumns[i];

				if (i == 0)
					sb.append(buildColumn.getColumnName()).append(" AS ").append(buildColumn.getFieldName());
				else
					sb.append(", ").append(buildColumn.getColumnName()).append(" AS ").append(buildColumn.getFieldName());

			}
		}

		sb.append(" FROM ").append(buildTable.getTableName());

		return sb.toString();

	}

}
