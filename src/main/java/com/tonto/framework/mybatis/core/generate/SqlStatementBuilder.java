package com.tonto.framework.mybatis.core.generate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.type.JdbcType;

import com.tonto.base.ObjectContainer;
import com.tonto.framework.mybatis.core.SqlStatement;

public interface SqlStatementBuilder {

	public List<SqlStatement> build(BuildContainer buildContainer);

	public static class BuildColumn {

		String columnName;

		String fieldName;

		Class<?> javaType;

		JdbcType jdbcType;

		BuildTable referenceBuildTable;
		BuildColumn referenceBuildColumn;
		String referenceId;

		public BuildColumn(String columnName, String fieldName, Class<?> javaType, JdbcType jdbcType) {
			this.columnName = columnName;
			this.fieldName = fieldName;
			this.javaType = javaType;
			this.jdbcType = jdbcType;
		}

		public BuildTable getRefrenceBuildTable() {
			return referenceBuildTable;
		}

		public void setRefrenceBuildTable(BuildTable referenceBuildTable,BuildColumn referenceBuildColumn,String referenceId) {
			this.referenceBuildTable = referenceBuildTable;
			this.referenceBuildColumn = referenceBuildColumn;
			this.referenceId = referenceId;
		}

		public String getColumnName() {
			return columnName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public Class<?> getJavaType() {
			return javaType;
		}

		public JdbcType getJdbcType() {
			return jdbcType;
		}

		public String getReferenceId() {
			return referenceId;
		}

		public BuildColumn getReferenceBuildColumn() {
			return referenceBuildColumn;
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("{")
				.append("column_name:").append(columnName).append(",")
				.append("field_name:").append(fieldName).append(",")
				.append("java_type:").append(javaType).append(",")
				.append("jdbc_type:").append(jdbcType).append(",")
				.append("reference_id:").append(referenceId).append(",")
				.append("reference_table").append(referenceBuildTable == null ? "":referenceBuildTable.getTableName()).append(",")
				.append("reference_table").append(referenceBuildColumn == null ? "":referenceBuildColumn.getColumnName())
				.append("}");
			return sb.toString();
		}
		
		
	}

	public static class BuildTable extends ObjectContainer<String, BuildColumn> {

		String tableName;

		Class<?> modelType;

		List<BuildColumn> primaryBuildColumns;

		List<List<BuildColumn>> uniqueBuildColumns = new ArrayList<>();

		public BuildTable(String tableName, Class<?> modelType, List<BuildColumn> primaryBuildColumns) {
			this.tableName = tableName;
			this.modelType = modelType;
			this.primaryBuildColumns = primaryBuildColumns;
		}

		public void addUniqueBuildColumns(List<BuildColumn> uniqueBuildColumn) {
			if (uniqueBuildColumn != null && uniqueBuildColumn.size() > 0)
				uniqueBuildColumns.add(Collections.unmodifiableList(uniqueBuildColumn));
		}

		public List<List<BuildColumn>> getUniqueBuildColumns() {
			return Collections.unmodifiableList(uniqueBuildColumns);
		}

		public String getTableName() {
			return tableName;
		}

		public Class<?> getModelType() {
			return modelType;
		}

		public List<BuildColumn> getPrimaryBuildColumns() {
			return Collections.unmodifiableList(primaryBuildColumns);
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("{")
				.append("table_name:").append(tableName).append("\n")
				.append("model_type:").append(modelType).append("\n")
				.append("primary_column:[");
			
			if(primaryBuildColumns != null)
			{
				for(BuildColumn buildColumn : primaryBuildColumns)
					sb.append(buildColumn.columnName).append(",");	
			}
			
			sb.append("]\nunique_column:[");
			
			for(List<BuildColumn> uniqueList : uniqueBuildColumns)
			{
				sb.append("[");
				for(BuildColumn buildColumn : uniqueList)
					sb.append(buildColumn.columnName).append(",");	
				sb.append("]").append(",");
			}
			
			sb.append("]\ncolumns:[");
			
			for(BuildColumn buildColumn : getChildren())
			{
				sb.append("\n").append(buildColumn);			
			}
			
			sb.append("\n]}");
			
			return sb.toString();
		}
		
	}

	public static class BuildContainer extends ObjectContainer<String, BuildTable> {
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("[");
			
			for(BuildTable buildTable : getChildren())
				sb.append("\n").append(buildTable);
			
			sb.append("\n]");
			
			return sb.toString();
		}
		
	}

}
