package com.tonto.framework.service.mybatis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tonto.framework.mybatis.core.AutomationSqlSessionFactoryBean;
import com.tonto.framework.service.mybatis.pojo.BatisStatement;

@Service
public class BatisService {

	@Autowired
	AutomationSqlSessionFactoryBean sessionFactory;

	BatisStatement[] statements;

	public BatisStatement[] getStatements() {
		if (statements == null) {
			synchronized (BatisService.class) {
				if (statements == null) {

					Configuration configuration = sessionFactory.getConfiguration();
					Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
					List<BatisStatement> statementList = new ArrayList<>(mappedStatements.size());

					for (Object object : mappedStatements) {

						if (object instanceof MappedStatement) {

							MappedStatement mappedStatement = (MappedStatement) object;

							BatisStatement statement = new BatisStatement();
							String sql = mappedStatement.getSqlSource().getBoundSql(null).getSql();
							String id = mappedStatement.getId();
							SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

							ParameterMap parameterMap = mappedStatement.getParameterMap();
							List<ParameterMapping> parameterMappings = parameterMap.getParameterMappings();
							Class<?>[] paramTypes = new Class<?>[parameterMappings.size()];
							for (int i = 0; i < parameterMappings.size(); i++)
								paramTypes[i] = parameterMappings.get(i).getJavaType();

							List<ResultMap> resultMaps = mappedStatement.getResultMaps();
							Class<?>[] resultTypes = new Class<?>[resultMaps.size()];
							for (int i = 0; i < resultMaps.size(); i++)
								resultTypes[i] = resultMaps.get(i).getType();

							statement.setId(id);
							statement.setSql(sql);
							statement.setSqlCommandType(sqlCommandType);
							statement.setResultTypes(resultTypes);
							statement.setParameterTypes(paramTypes);

							statementList.add(statement);
						}
					}

					Collections.sort(statementList, new Comparator<BatisStatement>() {

						@Override
						public int compare(BatisStatement bs1, BatisStatement bs2) {
							return bs1.getId().compareTo(bs2.getId());
						}

					});

					statements = statementList.toArray(new BatisStatement[statementList.size()]);
				}
			}
		}

		return statements;
	}

	public int getStatementCount() {
		return getStatements().length;
	}


}
