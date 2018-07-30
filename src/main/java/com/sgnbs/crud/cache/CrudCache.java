package com.sgnbs.crud.cache;

import com.sgnbs.crud.annotation.EumConfig;
import com.sgnbs.crud.annotation.ModelDAO;
import com.sgnbs.crud.annotation.Table;
import com.sgnbs.crud.autoconfig.CrudProperties;
import com.sgnbs.crud.eum.AbstractAutoConfigEums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 系统缓存统一管理处
 * @author tianj
 *
 */
@Component
@Order(value=1)
@Slf4j
public class CrudCache{
	

	private CrudProperties crudProperties;
	private ApplicationContext applicationContext;


	@Autowired
	public CrudCache(CrudProperties crudProperties,ApplicationContext applicationContext){
		this.crudProperties = crudProperties;
		this.applicationContext = applicationContext;
		run();
	}


	public  void run() {
		log.info("config model to dao and Service etc...,now scan package is {}",crudProperties.getPackageScan());
		Field field = null;
		Vector<Class<?>> classes = new Vector<>();
		try {
			field = ClassLoader.class.getDeclaredField("classes");
			field.setAccessible(true);
			classes.addAll( (Vector<Class<?>>) field.get(Thread.currentThread().getContextClassLoader()));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
        for(Class<?> clz : classes) {
        	if(!clz.getName().startsWith(crudProperties.getPackageScan())){
				continue;
			}
        	ModelDAO modelDAO = clz.getAnnotation(ModelDAO.class);
        	if(null!=modelDAO) {
        		Class<?> model_class = modelDAO.value();
				if(null!=model_class.getAnnotation(Table.class)) {
					model_map.put(model_class.getSimpleName(), model_class);
				}
        		dao_map.put(clz.getAnnotation(ModelDAO.class).value().getSimpleName(), clz);
        	}
        	if(null!=clz.getAnnotation(EumConfig.class)){
        		Object o = applicationContext.getBean(clz);
        		if(o instanceof AbstractAutoConfigEums){
        			((AbstractAutoConfigEums) o).bindEums();
				}
			}
        }

        log.info("model relationship finished");
	}


	/**
	 * key-驼峰实体类名.value-对应modelclass
	 */
	public static final Map<String,Class<?>> model_map = new HashMap<>();
	/**
	 * key-驼峰实体类名.value-对应daoclass
	 */
	public static final Map<String,Class<?>> dao_map = new HashMap<String,Class<?>>();

}
