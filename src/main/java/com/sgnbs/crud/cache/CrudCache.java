package com.sgnbs.crud.cache;

import com.sgnbs.crud.annotation.*;
import com.sgnbs.crud.autoconfig.CrudProperties;
import com.sgnbs.crud.eum.AbstractAutoConfigEums;
import com.sgnbs.crud.exception.AnnoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
		try {
			run();
		} catch (AnnoException e) {
			e.printStackTrace();
		}
	}


	public  void run() throws AnnoException {
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
			Method[] methods = clz.getMethods();
			for(Method method : methods) {
				ListDo listdo = method.getAnnotation(ListDo.class);
				SaveDo saveDo = method.getAnnotation(SaveDo.class);
				ToDetail toDetail = method.getAnnotation(ToDetail.class);
				ToSave toSave = method.getAnnotation(ToSave.class);
				DelDo delDo = method.getAnnotation(DelDo.class);
				Islist islist = method.getAnnotation(Islist.class);
				addToMap(clz, method, null != saveDo, savedo_map, null!=saveDo?saveDo.value().getSimpleName():"");
				addToMap(clz, method, null!=toDetail, todetail_map, null!=toDetail?toDetail.value().getSimpleName():"");
				addToMap(clz, method, null!=delDo, deldo_map, null!=delDo?delDo.value().getSimpleName():"");
				addToMap(clz, method, null!=toSave, tosave_map, null!=toSave?toSave.value().getSimpleName():"");
				addToMap(clz, method, null!=islist, tosave_map, null!=islist?modelDAO.value().getSimpleName()+islist.num():"");
				if(null!=listdo){
					int []nums = listdo.nums();
					for(int num : nums){
						addToMap(clz, method, true, listdo_map, listdo.value().getSimpleName()+num);
					}
				}

			}
        }

        log.info("model relationship finished");
	}

	private void addToMap(Class<?> clz, Method method, boolean b, Map<String, ClassMethod> cachemap, String simpleName) throws AnnoException {
		if(b) {
			if(null!=clz.getAnnotation(ModelService.class)) {
				if(null!= cachemap.get(simpleName)) {
					throw new AnnoException();
				}
				cachemap.put(simpleName, new ClassMethod(clz, method));
			}else {
				throw new AnnoException();
			}
		}
	}


	/**
	 * key-驼峰实体类名.value-对应modelclass
	 */
	public static final Map<String,Class<?>> model_map = new HashMap<>();
	/**
	 * key-驼峰实体类名.value-对应daoclass
	 */
	public static final Map<String,Class<?>> dao_map = new HashMap<>();
	/**
	 * key-驼峰实体类名.value-对应ClassMethod
	 */
	public static final Map<String,ClassMethod> savedo_map = new HashMap<>();
	/**
	 * key-驼峰实体类名.value-对应ClassMethod
	 */
	public static final Map<String,ClassMethod> listdo_map = new HashMap<String,ClassMethod>();
	/**
	 * key-驼峰实体类名.value-对应ClassMethod
	 */
	public static final Map<String,ClassMethod> deldo_map = new HashMap<>();

	/**
	 * key-驼峰实体类名.value-对应ClassMethod
	 */
	public static final Map<String,ClassMethod> todetail_map = new HashMap<>();
	/**
	 * key-驼峰实体类名.value-对应ClassMethod
	 */
	public static final Map<String,ClassMethod> isList_map = new HashMap<>();
	/**
	 * key-驼峰实体类名.value-对应ClassMethod
	 */
	public static final Map<String,ClassMethod> tosave_map = new HashMap<>();
	/**
	 * key-驼峰实体类名.value-对应ClassMethod
	 */
	public static final Map<String,ClassMethod> tran_map = new HashMap<>();

	public static class ClassMethod {
		private Class<?> clz;
		private Method method;
		public Class<?> getClz() {
			return clz;
		}
		public void setClz(Class<?> clz) {
			this.clz = clz;
		}
		public Method getMethod() {
			return method;
		}
		public void setMethod(Method method) {
			this.method = method;
		}
		public ClassMethod(Class<?> clz, Method method) {
			super();
			this.clz = clz;
			this.method = method;
		}

	}

}
