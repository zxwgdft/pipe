package com.tonto.framework.mybatis.core.generate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataReference {
	
	public String table() default "";
	
	public String column() default "";
	
	/**
	 * 如果是组合外键需要设置该ID
	 * @return
	 */
	public String id() default "";
	
}
