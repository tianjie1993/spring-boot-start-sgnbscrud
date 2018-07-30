package com.sgnbs.crud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 更为通用的关联关系注解。
 * 作用的字段为另一个表的主键，多个以“,”号隔开
 * @author tianj
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RltsField {
	
	Class<?> value();
	
	boolean otm();
	
	String result_container();

}
