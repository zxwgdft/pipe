package com.tonto.framework.service;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.tonto.utils.reflect.ReflectUtil;

/**
 * <h2>DAO通用服务</h2>
 * <P>
 * 省略基础DAO的代码，形成通用简略的数据访问服务，但是需要有对应的SQL MAPPER
 * </P>
 * <P>
 * SQL MAPPER 规则：泛型对象类名+insert/update/delete/get
 * </P>
 * 
 * @author TontZhou
 * 
 * @param <T>
 */
public abstract class DaoSupport<T> {

	private String namespace;

	public DaoSupport() {
		Class<?> clazz = ReflectUtil.getSuperClassArgument(this.getClass(), DaoSupport.class, 0);
		if (clazz == null)
			throw new RuntimeException("[" + this.getClass() + "]的父类DaoService的泛型参数必须明确定义");
		namespace = clazz.getName();
	}

	@Autowired
	protected SqlSessionTemplate sqlSession;

	@Autowired
	protected SqlSessionTemplate reuseSqlSession;

	@Autowired
	protected SqlSessionTemplate batchSqlSession;

	/**
	 * SQL MAPPER中的对应域名，通过这个拼接处完整STATEMENT
	 * 
	 * @return
	 */
	protected String getDaoNamespace() {
		return namespace;
	}

	public List<T> find() {
		return sqlSession.selectList(getDaoNamespace() + ".find");
	}

	@SuppressWarnings("unchecked")
	public PageList<T> find(PageBounds pageBounds) {
		return (PageList<T>) sqlSession.selectList(getDaoNamespace() + ".find", pageBounds);
	}

	public int save(T t) {
		return sqlSession.insert(getDaoNamespace() + ".insert", t);
	}

	public int update(T t) {
		return sqlSession.update(getDaoNamespace() + ".update", t);
	}

	public int delete(T t) {
		return sqlSession.delete(getDaoNamespace() + ".delete", t);
	}

	public T get(Object id) {
		List<T> result = sqlSession.selectList(getDaoNamespace() + ".get", id);
		return (result != null && result.size() > 0) ? result.get(0) : null;
	}

	
	
}
