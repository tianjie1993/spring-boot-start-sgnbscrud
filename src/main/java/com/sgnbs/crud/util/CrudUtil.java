package com.sgnbs.crud.util;

import com.sgnbs.crud.annotation.ID;
import com.sgnbs.crud.annotation.IsEum;
import com.sgnbs.crud.annotation.RltsField;
import com.sgnbs.crud.annotation.Table;
import com.sgnbs.crud.autoconfig.CrudProperties;
import com.sgnbs.crud.cache.CrudCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class CrudUtil{

	private CrudProperties crudProperties;
	private ApplicationContext applicationContext;


	@Autowired
	public CrudUtil(CrudProperties crudProperties,ApplicationContext applicationContext){
		this.crudProperties = crudProperties;
		this.applicationContext = applicationContext;
	}
	

	public void insertEntity(Object o) {
		try {
			Class<?> daoclz = CrudCache.dao_map.get(o.getClass().getSimpleName());
			if(null!=daoclz) {
				Object dao = applicationContext.getBean(daoclz);
				Method insertMethod = dao.getClass().getMethod(crudProperties.getInsertName(), o.getClass());
				insertMethod.invoke(dao, o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  void updateEntity(Object o){
		try {
			Class<?> daoclz = CrudCache.dao_map.get(o.getClass().getSimpleName());
			if(null!=daoclz) {
				Object dao = applicationContext.getBean(daoclz);
				Method insertMethod = dao.getClass().getMethod(crudProperties.getUpdateName(), o.getClass());
				insertMethod.invoke(dao, o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  void delEntitys(Class<?> clz,String ids){
		try {
			Class<?> daoclz = CrudCache.dao_map.get(clz.getSimpleName());
			Class<?> idtype = getIdType(clz);
			if(null!=daoclz) {
				Object dao = applicationContext.getBean(daoclz);
				Method insertMethod = dao.getClass().getMethod(crudProperties.getDelelteName(), idtype);
				for(String id : ids.split(",")) {
					if(idtype==Integer.class) {
						insertMethod.invoke(dao, Integer.parseInt(id));
					}else {
						insertMethod.invoke(dao, id);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  void delEntity(Object o){
		if(null==o)  return;
		try {
			if(o instanceof Collection) {
				Collection<?> os = (Collection<?>) o;
				for(Object temp : os) {
					Method getm = temp.getClass().getMethod("get"+StrUtil.firstCharToUpperCase( getIdStr(temp.getClass())));
					Object idvalue = getm.invoke(temp);
					delEntitys(temp.getClass(),String.valueOf(idvalue));
				}
			}else {
				Method getm = o.getClass().getMethod("get"+StrUtil.firstCharToUpperCase( getIdStr(o.getClass())));
				Object idvalue = getm.invoke(o);
				delEntitys(o.getClass(),String.valueOf(idvalue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public <T> T getEntity(Class<T> clz,Object id){
		return getEntity(clz,id,false);
	}

	
	public <T> T getEntity(Class<T> clz,Object id,boolean addannodata){
		Object o = null;
		try {
			Class<?> daoclz = CrudCache.dao_map.get(clz.getSimpleName());
			Class<?> idtype = getIdType(clz);
			if(null!=daoclz) {
				Object dao = applicationContext.getBean(daoclz);
				Method insertMethod = dao.getClass().getMethod(crudProperties.getSelectOneName(), idtype);
				if(Integer.class==idtype) {
					o = insertMethod.invoke(dao, Integer.parseInt(String.valueOf(id)));
				}else {
					o = insertMethod.invoke(dao, String.valueOf(id));
				}
			}
			if(null!=o && addannodata) addAnnoData(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return clz.cast(o);
	}
	public <T> List<T> getEntitys(Class<T> clz,String ids){
		if(StrUtil.isBlank(ids)){
			return new ArrayList<>();
		}
		String [] idstr = ids.split(",");
		Set<String> idset = new HashSet<String>();
		for(String id : idstr) {
			idset.add(id);
		}
		StringBuffer sb = new StringBuffer();
		Iterator<String> iterator = idset.iterator();
		while(iterator.hasNext()){
			sb.append(","+iterator.next());
		}
		return getEntitys(clz,sb.substring(1,sb.length()),false);
	}

	public <T> List<T> getEntitys(Class<T> clz,String ids,boolean addannodata){
		if(StrUtil.isBlank(ids)){
			return new ArrayList<>();
		}
		String [] idstr = ids.split(",");
		Set<String> idset = new HashSet<String>();
		for(String id : idstr) {
			idset.add(id);
		}
		List<T> os = new ArrayList<>();
		if(StrUtil.isBlank(ids)) return os;
		try {
			Class<?> daoclz = CrudCache.dao_map.get(clz.getSimpleName());
			Class<?> idtype = getIdType(clz);
			for(String id : idset) {
				Object model = null;
				if(null!=daoclz) {
					Object dao = applicationContext.getBean(daoclz);
					Method insertMethod = dao.getClass().getMethod(crudProperties.getSelectOneName(), idtype);
					if(Integer.class==idtype) {
						model =	insertMethod.invoke(dao, Integer.parseInt(String.valueOf(id)));
					}else {
						model = insertMethod.invoke(dao, String.valueOf(id));
					}
				}
				if(null!=model && addannodata) addAnnoData(model);
				os.add(clz.cast(model));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return os;
	}
	
	/**
	 * 执行指定实体类中的dao方法
	 * @param clz model class
	 * @param methodname excute methodname
	 * @param objects parameters
	 * @return
	 */
	public  Object excuteDaoMethod(Class<?> clz,String methodname,Object ...objects) {
		Class<?> dao = CrudCache.dao_map.get(clz.getSimpleName());
		if(null==dao) return null;
		try {
			if(null!=objects && objects.length>0) {
				Class<?> [] classtypes = new Class[objects.length];
				for(int i=0;i<objects.length;i++) {
					if(objects[i].getClass()==HashMap.class) {
						classtypes[i] = Map.class;
					}else {
						classtypes[i]=objects[i].getClass();
					}
				}
				Method excuteMethod = dao.getMethod(methodname, classtypes);
				return excuteMethod.invoke(applicationContext.getBean(dao), objects);
			}else {
				Method excuteMethod = dao.getMethod(methodname);
				return excuteMethod.invoke(applicationContext.getBean(dao));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 执行指定实体类中的dao方法
	 * @param clz model class
	 * @param methodname excute methodname
	 * @return
	 */
	public  Object excuteDaoMethod(Class<?> clz,String methodname) {
		return excuteDaoMethod(clz,methodname,new Object[] {}) ;
	}


	public  void  addAnnoData(Object o){
		if(null==o)  return;
		try {
			if(o instanceof Collection) {
				Collection<?> os = (Collection<?>) o;
				for(Object temp : os) {
					commAddAnnoData(temp,temp.getClass().getDeclaredFields(),true);
				}
			}else {
				commAddAnnoData(o,o.getClass().getDeclaredFields(),true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public   void  addAnnoData(Object o,String ...fieldnames) {
		if(null==o)  return;
		try {
			if(o instanceof Collection) {
				Collection<?> os = (Collection<?>) o;
				for(Object temp : os) {
					Class<?> model = temp.getClass();
					Field[] fields = new Field[fieldnames.length];
					for(int i=0;i<fieldnames.length;i++) {
						fields[i] = model.getDeclaredField(fieldnames[i]);
					}
					commAddAnnoData(temp,fields,false);
				}
			}else {
				Class<?> model = o.getClass();
				Field[] fields = new Field[fieldnames.length];
				for(int i=0;i<fieldnames.length;i++) {
					fields[i] = model.getDeclaredField(fieldnames[i]);
				}
				commAddAnnoData(o,fields,false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param o
	 * @param deepin 若递归下一级model rlst 多过两个 。不建议使用 deepin = true; 会造成很大额外开销
	 * @param fieldnames
	 */
	public   void  addAnnoData(Object o,boolean deepin,String ...fieldnames) {
		if(null==o)  return;
		try {
			if(o instanceof Collection) {
				Collection<?> os = (Collection<?>) o;
				for(Object temp : os) {
					Class<?> model = temp.getClass();
					Field[] fields = new Field[fieldnames.length];
					for(int i=0;i<fieldnames.length;i++) {
						fields[i] = model.getDeclaredField(fieldnames[i]);
					}
					commAddAnnoData(temp,fields,deepin);				}
			}else {
				Class<?> model = o.getClass();
				Field[] fields = new Field[fieldnames.length];
				for(int i=0;i<fieldnames.length;i++) {
					fields[i] = model.getDeclaredField(fieldnames[i]);
				}
				commAddAnnoData(o,fields,deepin);			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public   void  addAnnoData(Object o,boolean deepin){
		if(null==o)  return;
		try {
			if(o instanceof Collection) {
				Collection<?> os = (Collection<?>) o;
				for(Object temp : os) {
					commAddAnnoData(temp,temp.getClass().getDeclaredFields(),deepin);
				}
			}else {
				commAddAnnoData(o,o.getClass().getDeclaredFields(),deepin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	private  void  commAddAnnoData(Object o,Field []fields,boolean deepin) throws Exception{
		Class<?> model = o.getClass();
		for(Field field : fields) {
			IsEum eum = field.getAnnotation(IsEum.class);
			if(null!=eum) {
				String listkvname = eum.value();
				List<EumUtil.KeyValue> kys = EumUtil.getByTFname(o.getClass().getAnnotation(Table.class).value(), StrUtil.getKeyByCamel(field.getName()));
				Method kvmethod = model.getMethod("set"+StrUtil.firstCharToUpperCase(listkvname), List.class);
				kvmethod.invoke(o, kys);
			}

			RltsField rltsField = field.getAnnotation(RltsField.class);
			if(null!=rltsField) {
				Method getidmethod = model.getMethod("get"+StrUtil.firstCharToUpperCase(field.getName()));
				Class<?> idtypeclass = getIdType(rltsField.value());
				Class<?> modeldao = CrudCache.dao_map.get(rltsField.value().getSimpleName());
				Object dao = applicationContext.getBean(modeldao);
				boolean isotm = rltsField.otm();
				String ids = String.valueOf(getidmethod.invoke(o));
				if(isotm) {
					List rltsmodels = new ArrayList();
					if(StrUtil.notBlank(ids)) {
						for(String id : ids.split(",")) {
							if(idtypeclass.equals(Integer.class)) {
								Method m = modeldao.getMethod(crudProperties.getSelectOneName(), Integer.class);
								rltsmodels.add(m.invoke(dao, Integer.parseInt(id)));
							}else {
								Method m = modeldao.getMethod(crudProperties.getSelectOneName(), String.class);
								rltsmodels.add(m.invoke(dao, id));
							}
						}
					}
					if(deepin) {
						for(Object temp : rltsmodels) {
							addAnnoData(temp);
						}
					}
					Method savemethod = model.getMethod("set"+StrUtil.firstCharToUpperCase(rltsField.result_container()), List.class);
					savemethod.invoke(o, rltsmodels);
				}else {
					Object modelvalue = new Object();
					Method savemethod = model.getMethod("set"+StrUtil.firstCharToUpperCase(rltsField.result_container()), rltsField.value());
					if(StrUtil.notBlank(ids)) {
						if(idtypeclass.equals(Integer.class)) {
							Method m = modeldao.getMethod(crudProperties.getSelectOneName(), Integer.class);
							modelvalue = m.invoke(dao, Integer.parseInt(ids));
						}else {
							Method m = modeldao.getMethod(crudProperties.getSelectOneName(), String.class);
							modelvalue = m.invoke(dao, ids);
						}
						if(deepin) {
							addAnnoData(modelvalue);
						}
						savemethod.invoke(o, modelvalue);
					}
				}
			}
		}
	}

	public  Class<?> getIdType(Object o){
		Field[] declaredFields = null;
		if(o instanceof Class) {
			Class<?> clz = (Class<?>)o;
			declaredFields = clz.getDeclaredFields();
		}else {
			declaredFields = o.getClass().getDeclaredFields();
		}
		for (Field field : declaredFields) {
			if(null != field.getAnnotation(ID.class)) {
				return  field.getType();
			}
		}
		return null;
	}

    public  Class<?> getFieldType(Object o,String fieldname){
        Field[] declaredFields = null;
        if(o instanceof Class) {
            Class<?> clz = (Class<?>)o;
            declaredFields = clz.getDeclaredFields();
        }else {
            declaredFields = o.getClass().getDeclaredFields();
        }
        for (Field field : declaredFields) {
            if(fieldname.equals(field.getName())) {
                return  field.getType();
            }
        }
        return null;
    }

	public  String getIdStr(Class<?> clz) {
		String idstr = "id";
		Field []fields = clz.getDeclaredFields();
		for(Field f : fields) {
			ID id = f.getAnnotation(ID.class);
			if(null!=id) {
				idstr = f.getName();
				break;
			}
		}
		return  idstr;
	}
}
