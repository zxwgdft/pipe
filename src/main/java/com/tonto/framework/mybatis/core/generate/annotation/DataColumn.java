package com.tonto.framework.mybatis.core.generate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.type.JdbcType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataColumn {
	
	public String name();
	
	public JdbcType jdbcType() default JdbcType.OTHER;
	
	public boolean primary() default false;
	
	public boolean unique() default false;
	
	/**
	 * 组合唯一键时ID
	 * @return
	 */
	public String uniqueId() default "";
}
