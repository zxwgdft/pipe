package com.tonto.framework.mybatis.core;

import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

/**
 * 
 * MyBatis MappedStatement构建器
 * 
 * @author TontoZhou
 *
 */
public interface MappedStatementBuilder {

	public List<MappedStatement> build(Configuration configuration);
}
