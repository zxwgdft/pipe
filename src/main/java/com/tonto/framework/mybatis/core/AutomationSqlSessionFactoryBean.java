package com.tonto.framework.mybatis.core;

import java.io.IOException;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

public class AutomationSqlSessionFactoryBean extends SqlSessionFactoryBean {

	private List<MappedStatementBuilder> mappedStatementBuilders;
	
	private Configuration configuration;

	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {

		SqlSessionFactory factory = super.buildSqlSessionFactory();
		configuration = factory.getConfiguration();
		createBaseStatement(configuration);
		return factory;
	}

	protected void createBaseStatement(Configuration configuration) {

		if (mappedStatementBuilders != null) {

			for (MappedStatementBuilder builder : mappedStatementBuilders) {
				List<MappedStatement> mappedStatements = builder.build(configuration);

				for (MappedStatement mappedStatement : mappedStatements)
					configuration.addMappedStatement(mappedStatement);
			}
		}
	}

	public List<MappedStatementBuilder> getMappedStatementBuilders() {
		return mappedStatementBuilders;
	}

	public void setMappedStatementBuilders(List<MappedStatementBuilder> mappedStatementBuilders) {
		this.mappedStatementBuilders = mappedStatementBuilders;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

}
