package com.tonto.framework.mybatis.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.log4j.Logger;

import com.tonto.framework.mybatis.core.generate.FindSqlStatementBuilder;
import com.tonto.framework.mybatis.core.generate.InsertSqlStatementBuilder;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder.BuildColumn;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder.BuildContainer;
import com.tonto.framework.mybatis.core.generate.SqlStatementBuilder.BuildTable;
import com.tonto.framework.mybatis.core.generate.UpdateSqlStatementBuilder;
import com.tonto.framework.mybatis.core.generate.annotation.DataColumn;
import com.tonto.framework.mybatis.core.generate.annotation.DataReference;
import com.tonto.framework.mybatis.core.generate.annotation.DataTable;
import com.tonto.utils.reflect.NameUtil;

public class AnnotationMappedStatementBuilder extends DefaultMappedStatementBuilder {

	private static final Logger logger = Logger.getLogger(AnnotationMappedStatementBuilder.class);

	private String packageName;
	
	private List<SqlStatementBuilder> sqlStatementBuilders; 


	@Override
	public List<MappedStatement> build(Configuration configuration) {

		if(sqlStatementBuilders == null)
		{
			sqlStatementBuilders = new ArrayList<>();
			sqlStatementBuilders.add(new FindSqlStatementBuilder());
			sqlStatementBuilders.add(new InsertSqlStatementBuilder());
			sqlStatementBuilders.add(new UpdateSqlStatementBuilder());
		}
		
		BuildContainer  buildContainer = createBuildContainer();
		List<SqlStatement> sqlStatements = createSqlStatement(buildContainer);		
		setSqlStatements(sqlStatements);
		
		return super.build(configuration);
	}
	
	private List<SqlStatement> createSqlStatement(BuildContainer  buildContainer) {
		
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
						logger.warn("SQL-STATEMENT ID["+id+"] REPEAT");
					}
					
					sqlStatementMap.put(id, sqlStatement);
					
					//if(logger.isDebugEnabled())
					logger.info("Add SqlStatement =>" + sqlStatement);
					
				}				
			}			
		}
				
		return  new ArrayList<>(sqlStatementMap.values());	
	}

	private BuildContainer createBuildContainer() {

		ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
		resolverUtil.findAnnotated(DataTable.class, packageName);
		Set<Class<? extends Class<?>>> classes = resolverUtil.getClasses();

		BuildContainer buildContainer = new BuildContainer();

		for (Class<?> type : classes) {
			BuildTable buildTable = createBuildTable(type);
			buildContainer.addChild(buildTable.getTableName(), buildTable);
		}

		for (BuildTable buildTable : buildContainer.getChildren()) {

			for (BuildColumn buildColumn : buildTable.getChildren()) {
				BuildColumnWrap buildColumnWrap = (BuildColumnWrap) buildColumn;
				if (buildColumnWrap.referenceId != null) {
					BuildTable refBuildTable = buildContainer.getChild(buildColumnWrap.referenceTable);
					if (refBuildTable != null) {
						BuildColumn refBuildColumn = refBuildTable.getChild(buildColumnWrap.referenceColumn);
						if (refBuildColumn != null) {
							String referenceId = buildColumnWrap.referenceId;
							buildColumnWrap.setRefrenceBuildTable(refBuildTable, refBuildColumn, referenceId.equals("") ? null
									: referenceId);
							continue;
						}
					}
					logger.warn("未找到关联表列[" + buildColumnWrap.referenceTable + "].[" + buildColumnWrap.referenceColumn + "]");
				}
			}
		}
		
		logger.info("create [BuildContainer] from " + packageName + ":\n" + buildContainer);
		
		return buildContainer;
	}

	private BuildTable createBuildTable(Class<?> type) {

		DataTable dataTable = type.getAnnotation(DataTable.class);

		Method[] methods = type.getMethods();

		List<BuildColumn> primaryColList = new ArrayList<>();
		List<BuildColumn> allColList = new ArrayList<>();
		Map<String, List<BuildColumn>> uniqueColListMap = new HashMap<>();

		for (Method method : methods) {
			DataColumn dataColumn = method.getAnnotation(DataColumn.class);

			if (dataColumn != null) {

				String columnName = dataColumn.name();
				String fieldName = NameUtil.removeGetOrSet(method.getName());
				JdbcType jdbcType = dataColumn.jdbcType();

				if (jdbcType == JdbcType.OTHER)
					jdbcType = null;

				// get function 返回类型
				Class<?> javaType = method.getReturnType();
				if (javaType == null) {
					// set function 第一个参数
					Class<?>[] paramTypes = method.getParameterTypes();
					if (paramTypes != null && paramTypes.length == 1)
						javaType = paramTypes[0];
				}

				if (javaType == null) {
					logger.info("--->" + dataColumn + "<--- 应该注释在get/set方法上");
					continue;
				}

				BuildColumnWrap buildColumn = new BuildColumnWrap(columnName, fieldName, javaType, jdbcType);

				allColList.add(buildColumn);

				if (dataColumn.primary())
					primaryColList.add(buildColumn);

				if (dataColumn.unique()) {
					String uniqueId = dataColumn.uniqueId();
					List<BuildColumn> uniqueColList = uniqueColListMap.get(uniqueId);
					if (uniqueColList == null) {
						uniqueColList = new ArrayList<>();
						uniqueColListMap.put(uniqueId, uniqueColList);
					}

					uniqueColList.add(buildColumn);
				}

				DataReference reference = method.getAnnotation(DataReference.class);

				if (reference != null) {
					buildColumn.referenceId = reference.id();
					buildColumn.referenceTable = reference.table();
					buildColumn.referenceColumn = reference.column();
				}
			}
		}

		String tableName = dataTable.value();
		Class<?> modelType = type;

		BuildTable buildTable = new BuildTable(tableName, modelType, primaryColList);

		for (BuildColumn buildColumn : allColList)
			buildTable.addChild(buildColumn.getColumnName(), buildColumn);

		for (List<BuildColumn> uniqueColList : uniqueColListMap.values())
			buildTable.addUniqueBuildColumns(uniqueColList);

		return buildTable;

	}

	private static class BuildColumnWrap extends BuildColumn {

		public BuildColumnWrap(String columnName, String fieldName, Class<?> javaType, JdbcType jdbcType) {
			super(columnName, fieldName, javaType, jdbcType);
		}

		String referenceId;
		String referenceTable;
		String referenceColumn;

	}

	
	public List<SqlStatementBuilder> getSqlStatementBuilders() {
		return sqlStatementBuilders;
	}

	public void setSqlStatementBuilders(List<SqlStatementBuilder> sqlStatementBuilders) {
		this.sqlStatementBuilders = sqlStatementBuilders;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
