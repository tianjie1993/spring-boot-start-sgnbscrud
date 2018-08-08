package com.sgnbs.crud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sgnbs.crud.annotation.ID;
import com.sgnbs.crud.autoconfig.CrudProperties;
import com.sgnbs.crud.cache.CrudCache;
import com.sgnbs.crud.modelpagelist.PageDataManagement;
import com.sgnbs.crud.resp.AjaxResult;
import com.sgnbs.crud.util.CrudLocal;
import com.sgnbs.crud.util.CrudUtil;
import com.sgnbs.crud.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通用增删改查
 * @author tianj
 *
 */
@Slf4j
@RestController("/crud")
public class CrudController{
	
	
	@Autowired
	private CrudUtil crudUtil;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private CrudProperties crudProperties;

	@Autowired
	private PageDataManagement pageDataManagement;


	/**
	 * 通用详情
	 * @param classname
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/details/{classname}")
	public AjaxResult getDetails(@PathVariable("classname") String classname,HttpServletRequest request) {
		String modelname = StrUtil.getCamelClassname(classname);
		Class<?> model = CrudCache.model_map.get(modelname);
		String id = request.getParameter("id");
		List os = null;
		if(StrUtil.notBlank(id)) {
			os = crudUtil.getEntitys(model, id);
			crudUtil.addAnnoData(os);
		}
		return AjaxResult.success(os);
	}

	/**
	 * @param classname
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/detail/{classname}")
	public AjaxResult getDetail(@PathVariable("classname") String classname,HttpServletRequest request) throws Exception{
		String modelname = StrUtil.getCamelClassname(classname);
		Class<?> model = CrudCache.model_map.get(modelname);
		Object model_ins = model.newInstance();
		String id = request.getParameter("id");
		if(StrUtil.notBlank(id)) {
			model_ins = crudUtil.getEntity(model, id);
		}
		crudUtil.addAnnoData(model_ins);
		CrudCache.ClassMethod clzm_detail = CrudCache.todetail_map.get(modelname);
		if(null!=clzm_detail) {
			clzm_detail.getMethod().invoke(applicationContext.getBean(clzm_detail.getClz()),request, model_ins);
		}
		return AjaxResult.success(model_ins);
	}
	

	/**
	 * 通用物理删除。接口地址/crud/physicald/#表名#
	 * 支持批量
	 * @param classname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/physicald/{classname}")
	@Transactional(rollbackFor=Exception.class)
	public AjaxResult physicalDelete(@PathVariable("classname") String classname, HttpServletRequest request) throws Exception {
		String ids = request.getParameter("id");
		Class<?> model = CrudCache.model_map.get(StrUtil.getCamelClassname(classname));;
  		CrudCache.ClassMethod clzm = CrudCache.deldo_map.get(StrUtil.getCamelClassname(classname));
  		if(null!=clzm) {
  			clzm.getMethod().invoke(applicationContext.getBean(clzm.getClz()), request);
  		}
  		String msg = CrudLocal.getErrorMsg();
  		if(StrUtil.notBlank(msg)) {
			CrudLocal.removeErrorMsg();
  		    return AjaxResult.error(msg);
  		}
  		crudUtil.delEntitys(model, ids);
	    return AjaxResult.success();
	}
	
	/**
	 * 通用逻辑删除。接口地址/crud/logicald/#表名#
	 * 支持批量
	 * 会设置表status=99.没有属性跑出异常
	 * @param classname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/logicald/{classname}")
	@Transactional(rollbackFor=Exception.class)
	public AjaxResult logicalDelete(@PathVariable("classname") String classname,HttpServletRequest request) throws Exception {
		String ids = request.getParameter("id");
		String [] idsz = ids.split(",");
		classname = StrUtil.getCamelClassname(classname);
		Class<?> model = CrudCache.model_map.get(classname);
		Object o = model.newInstance();
		Field[] declaredFields = model.getDeclaredFields();
		String methodname = "set"+ StrUtil.firstCharToUpperCase(crudProperties.getDelfield());
	    Method setStatusMethod = model.getMethod(methodname, crudUtil.getFieldType(o,crudProperties.getDelfield()));
	    Class<?> idtype = Integer.class;
	    String idste = "id";
        for (Field field : declaredFields) {
        	if(null != field.getAnnotation(ID.class)) {
        		idtype = field.getType();
        		idste = field.getName();
        		break;
        	}
        }
        CrudCache.ClassMethod clzm = CrudCache.deldo_map.get(classname);
  		if(null!=clzm) {
  			clzm.getMethod().invoke(applicationContext.getBean(clzm.getClz()), request);
  		}
  		String msg = CrudLocal.getErrorMsg();
  		if(StrUtil.notBlank(msg)) {
			CrudLocal.removeErrorMsg();
  		    return AjaxResult.error(CrudLocal.getErrorMsg());
  		}
        Method setIdMethod = model.getMethod("set"+StrUtil.firstUppercase(idste), idtype);
		Class<?> modeldao = CrudCache.dao_map.get(classname);
		Object dao = applicationContext.getBean(modeldao);
		Method updatestatus = modeldao.getMethod(crudProperties.getUpdateName(), model);
		for(String id : idsz) {
			if(idtype.equals(Integer.class)) {
				setIdMethod.invoke(o, Integer.parseInt(id));
			}else {
				setIdMethod.invoke(o, id);
			}
			setStatusMethod.invoke(o,crudProperties.getDelStatus());
			updatestatus.invoke(dao, o);
		}
	    return AjaxResult.success();
	}

	/**
	 * 数据保存通过入口
	 * @param classname
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/save/{classname}")
	@Transactional(rollbackFor=Exception.class)
	public AjaxResult saveEntity(@PathVariable("classname") String classname,@RequestParam Map<String,String>  map) throws Exception {
		String modelname = StrUtil.getCamelClassname(classname);
		Class<?> model = CrudCache.model_map.get(modelname);
		Object model_ins = model.newInstance();
		save(model_ins,map);
		crudUtil.addAnnoData(model_ins);
		String errormsg = CrudLocal.getErrorMsg();
		if(StrUtil.notBlank(errormsg)) {
			crudUtil.delEntity(model_ins);
			CrudLocal.removeErrorMsg();
			return  AjaxResult.error(errormsg);
		}else {
			crudUtil.updateEntity(model_ins);
		}
		CrudLocal.removeIsNew();
		return  AjaxResult.success(model_ins);
	}

	/**
	 * 通用获取listdata接口
	 * @param classname
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/list/{classname}/{ispage}/{num}")
	public Object getListData(@PathVariable("classname") String classname,
									 @PathVariable(value = "num") Integer num,
									 @PathVariable(value = "ispage") boolean ispage,
									 @RequestParam Map<String,String>  map) throws Exception {
		String pageNoName = crudProperties.getPageNoName();
		String pageSizeName = crudProperties.getPageSizeName();
		if(StrUtil.notBlank(map.get(pageNoName)) && StrUtil.notBlank(map.get(pageSizeName)) && ispage) {
			PageHelper.startPage(Integer.parseInt(map.get(pageNoName)), Integer.parseInt(map.get(pageSizeName)));
		}
		map.remove(pageNoName);
		map.remove(pageSizeName);
		String modelname = StrUtil.getCamelClassname(classname);
		CrudCache.ClassMethod clzm = CrudCache.listdo_map.get(modelname+num);
		if(null!=clzm) {
			clzm.getMethod().invoke(applicationContext.getBean(clzm.getClz()), map);
		}
		CrudCache.ClassMethod clzm1 = CrudCache.isList_map.get(modelname+num);
		Object data = null;
		if(null!=clzm1) {
			if(ispage){
				Page page = (Page)clzm1.getMethod().invoke(applicationContext.getBean(clzm1.getClz()), map);
				pageDataManagement.transformData(page.getResult());
				data = page;
			}else{
				Object o = clzm1.getMethod().invoke(applicationContext.getBean(clzm1.getClz()), map);
				pageDataManagement.transformData(o);
				data = o;
			}
		}
		return pageDataManagement.success(data);
	}


	private void save(Object model_ins,Map<String,String> map)  throws Exception{
		String modelname = model_ins.getClass().getSimpleName();
		Class<?> model = model_ins.getClass();
		Set<String> keys = map.keySet();
		String idstr = "id";
		Class<?> idtype = Integer.class;
		boolean isnew = false;
		Field []fields = model.getDeclaredFields();
		for(Field f : fields) {
			ID id = f.getAnnotation(ID.class);
			if(null!=id) {
				idstr = f.getName();
				idtype = f.getType();
				break;
			}
		}
		for(String key : keys) {
			Field field = null;
			try {
				field = model.getDeclaredField(key);
			} catch (NoSuchFieldException e) {
				log.warn("no such field {}",key);
			}
			if(null!=field) {
				Method  m = model.getMethod("set"+StrUtil.firstUppercase(key), field.getType());
				if(field.getType().equals(Integer.class) && StrUtil.notBlank(map.get(key))) {
					m.invoke(model_ins, Integer.parseInt(map.get(key)));
				}
				if(field.getType().equals(String.class)) {
					m.invoke(model_ins, map.get(key));
				}
				if(field.getType().equals(Date.class) && StrUtil.notBlank(map.get(key))) {
					String dateStr = map.get(key);
					Date date = null;
					if(dateStr.length()== 8) {
						date = getDate(map.get(key), "yyyyMMdd");
					}
					if(dateStr.length()==14) {
						date = getDate(map.get(key),"yyyyMMddHHmmss");
					}
					m.invoke(model_ins, date);
				}
				if(field.getType().equals(Long.class) && StrUtil.notBlank(map.get(key))) {
					m.invoke(model_ins, Long.parseLong(map.get(key)));
				}
			}
			if(key.equals(idstr) && StrUtil.isBlank(map.get(key))) {
				isnew = true;
				CrudLocal.setSaveNew();
				if(idtype.equals(String.class)){
					Method m_setid = model.getMethod("set"+StrUtil.firstCharToUpperCase(idstr), String.class);
					m_setid.invoke(model_ins, StrUtil.getUUID());
				}
			}
		}
		if(idtype.equals(Integer.class)) {
			Method m_getid=  model.getMethod("get"+StrUtil.firstCharToUpperCase(idstr));
			if(null==m_getid.invoke(model_ins)) {
				isnew = true;
				CrudLocal.setSaveNew();
			}
		}
		if(isnew) {
			crudUtil.insertEntity(model_ins);
		}
		//1.保存表单时处理个性化需求
		CrudCache.ClassMethod clzm = CrudCache.savedo_map.get(modelname);
		if(null!=clzm) {
			clzm.getMethod().invoke(applicationContext.getBean(clzm.getClz()), model_ins,map);
		}
		
	}


	private  Date getDate(String date, String format){
		Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			if (date != null) {
				d = sdf.parse(date);
			}
			else {
				d = new Date();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
}
