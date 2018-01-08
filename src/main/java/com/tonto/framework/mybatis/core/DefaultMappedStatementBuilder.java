package com.tonto.framework.mybatis.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.Configuration;

public class DefaultMappedStatementBuilder implements MappedStatementBuilder {

	List<SqlStatement> sqlStatements;

	@Override
	public List<MappedStatement> build(Configuration configuration) {

		if (sqlStatements == null || sqlStatements.size() == 0)
			return null;

		List<MappedStatement> mappedStatements = new ArrayList<>(sqlStatements.size());

		for (SqlStatement sqlStatement : sqlStatements) {
			TextSqlNode sqlNode = new TextSqlNode(sqlStatement.getSql());

			SqlSource sqlSource = new RawSqlSource(configuration, sqlNode, null);

			MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, sqlStatement.getId(), sqlSource,
					sqlStatement.getSqlCommandType());
			statementBuilder.resource("");
			statementBuilder.fetchSize(null);
			statementBuilder.statementType(StatementType.PREPARED);
			statementBuilder.keyGenerator(new NoKeyGenerator());
			statementBuilder.keyProperty(null);
			statementBuilder.keyColumn(null);
			statementBuilder.databaseId(null);
			statementBuilder.lang(configuration.getDefaultScriptingLanuageInstance());
			statementBuilder.resultOrdered(false);
			statementBuilder.resulSets(null);
			statementBuilder.timeout(configuration.getDefaultStatementTimeout());

			if (sqlStatement.getParameterType() != null) {
				List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
				ParameterMap.Builder inlineParameterMapBuilder = new ParameterMap.Builder(configuration, statementBuilder.id() + "-Inline",
						sqlStatement.getParameterType(), parameterMappings);
				statementBuilder.parameterMap(inlineParameterMapBuilder.build());
			}

			if (sqlStatement.getResultType() != null) {
				List<ResultMap> resultMaps = new ArrayList<ResultMap>();
				ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, statementBuilder.id() + "-Inline",
						sqlStatement.getResultType(), new ArrayList<ResultMapping>(), null);
				resultMaps.add(inlineResultMapBuilder.build());
				
				statementBuilder.resultMaps(resultMaps);
				statementBuilder.resultSetType(null);
			}

		
			statementBuilder.flushCacheRequired(false);
			statementBuilder.useCache(true);
			statementBuilder.cache(null);

			mappedStatements.add(statementBuilder.build());
		}

		return mappedStatements;
	}

	public List<SqlStatement> getSqlStatements() {
		return sqlStatements;
	}

	public void setSqlStatements(List<SqlStatement> sqlStatements) {
		this.sqlStatements = sqlStatements;
	}

}
