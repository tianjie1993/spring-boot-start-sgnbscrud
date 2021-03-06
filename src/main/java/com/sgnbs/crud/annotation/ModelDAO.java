package com.sgnbs.crud.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for dao 
 * @author: tianj 
 * @since 3.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelDAO {
	
	 Class<?> value();
	 
}
