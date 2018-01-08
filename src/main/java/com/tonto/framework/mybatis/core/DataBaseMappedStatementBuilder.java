package com.tonto.framework.mybatis.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.JdbcType;
import org.apache.log4j.Logger;

import com.tonto.data.database.DataTypeUtil;
import com.tonto.data.database.model.Column;
import com.tonto.data.database.model.DataBase;
import com.tonto.data.database.model.Table;
import com.tonto.data.database.DataBaseType;
import com.tonto.framework.mybatis.JdbcTypeUtil;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder.BuildColumn;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder.BuildContainer;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder.BuildTable;
import com.tonto.utils.reflect.NameUtil;

/*
 * 未完成
 */
@Deprecated
public class DataBaseMappedStatementBuilder extends DefaultMappedStatementBuilder {

	private static Logger log = Logger.getLogger(DataBaseMappedStatementBuilder.class);
	
	List<SqlStatementBuilder> sqlStatementBuilders;
	
	public DataBaseMappedStatementBuilder(DataBase dataBase, DataBaseType dbType, Map<String, Class<?>> table2modelMap) {
		
		BuildContainer buildContainer = createBuildContainer(dataBase, dbType, table2modelMap);
		setSqlStatements(createSqlStatements(buildContainer));		
	}
	
	private BuildContainer createBuildContainer(DataBase dataBase, DataBaseType dbType, Map<String, Class<?>> table2modelMap) {
		
		Table[] tables = dataBase.getChildren();
		BuildContainer buildContainer = new BuildContainer();

		for (Table table : tables) {
			Column[] columns = table.getChildren();

			List<BuildColumn> primaryBuildColumns = new ArrayList<>();
			List<BuildColumn> buildColumns = new ArrayList<>(columns.length);

			for (Column column : columns) {
				String columnName = column.getName();
				String fieldName = NameUtil.underline2hump(columnName.toLowerCase());
				String dataType = column.getDataType();
				JdbcType jdbcType = JdbcTypeUtil.getJdbcType(dbType, dataType);
				Class<?> javaType = DataTypeUtil.getJavaType(column, dbType);

				BuildColumn buildColumn = new BuildColumn(columnName, fieldName, javaType, jdbcType);

				buildColumns.add(buildColumn);

				if (column.isPrimary())
					primaryBuildColumns.add(buildColumn);
			}
			String tableName = table.getName();
			Class<?> modelType = table2modelMap.get(tableName);
			BuildTable buildTable = new BuildTable(tableName, modelType, primaryBuildColumns);

			for (BuildColumn buildColumn : buildColumns)
				buildTable.addChild(buildColumn.getColumnName(), buildColumn);

			buildContainer.addChild(tableName, buildTable);
			
			// 外键 引用 TODO
		}
		
		return buildContainer;
		
	}
	
	
	private List<SqlStatement> createSqlStatements(BuildContainer buildContainer)
	{		
		Map<String,SqlStatement> sqlStatementMap = new HashMap<>();	
		
		if(buildContainer != null && sqlStatementBuilders!= null)
		{
			for(SqlStatementBuilder builder : sqlStatementBuilders)
			{
				List<SqlStatement> sqlStatements = builder.build(buildContainer);
				
				for(SqlStatement sqlStatement:sqlStatements)
				{
					String id = sqlStatement.getId();
					
					if(sqlStatementMap.containsKey(id))
					{
						log.warn("SQL-STATEMENT ID["+id+"] REPEAT");
					}
					
					sqlStatementMap.put(id, sqlStatement);
					
					if(log.isDebugEnabled())
						log.debug("Add SqlStatement =>" + sqlStatement);
					
				}
				
			}			
		}
				
		return  new ArrayList<>(sqlStatementMap.values());
	}


	public List<SqlStatementBuilder> getSqlStatementBuilders() {
		return sqlStatementBuilders;
	}


	public void setSqlStatementBuilders(List<SqlStatementBuilder> sqlStatementBuilders) {
		this.sqlStatementBuilders = sqlStatementBuilders;
	}

}
