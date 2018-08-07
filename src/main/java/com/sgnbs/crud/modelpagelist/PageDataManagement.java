package com.sgnbs.crud.modelpagelist;

import com.github.pagehelper.Page;
import com.sgnbs.crud.cache.CrudCache;
import com.sgnbs.crud.modelpagelist.container.PageDataContainerintf;
import com.sgnbs.crud.resp.AjaxResult;
import com.sgnbs.crud.util.EumUtil;
import com.sgnbs.crud.util.StrUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PageDataManagement {


    @Resource
    private ApplicationContext applicationContext;

    private PageDataContainerintf pageDataContainerintf;

    public void transformData(Object o) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if( o instanceof List){
            List<Map<String,String>> list = (List<Map<String, String>>) o;
            List<Map<String,String>> newlist = new ArrayList<>();
            for(Map<String,String> model : list) {
                Map<String,String> newmodel = new HashMap<>();
                for(String key : model.keySet()) {
                    String newkey = key;
                    String newvalue = String.valueOf(model.get(key));
                    //1.处理枚举项
                    if(key.startsWith("E") && key.split("-").length==3) {
                        String []eumstr = key.split("-");
                        newvalue = EumUtil.getEumValuename(eumstr[1], eumstr[2],newvalue);
                        newkey =  eumstr[2];
                    }
                    //2.处理器处理
                    if(key.startsWith("T") && key.split("-").length==3) {
                        String []eumstr = key.split("-");
                        CrudCache.ClassMethod clm = CrudCache.tran_map.get(eumstr[1].toLowerCase());
                        if(null!=clm){
                            newvalue = (String) clm.getMethod().invoke(clm.getClz().newInstance(), newvalue);
                        }
                        newkey =  eumstr[2];
                    }
                    newkey = StrUtil.getCamelCasekey(newkey);
                    newmodel.put(newkey, newvalue);
                }
                newlist.add(newmodel);
            }
            list.clear();
            list.addAll(newlist);
        }
    }

    public Object success(Object data) {
        if(null!=pageDataContainerintf){
            if(data instanceof Page) {
                return pageDataContainerintf.pack((Page) data);
            }else{
                return pageDataContainerintf.pack((List) data);
            }
        }else{
            return AjaxResult.success(data);
        }
    }

    public void setPageDataContainer(Class<? extends PageDataContainerintf> container){
        this.pageDataContainerintf = applicationContext.getBean(container);

    }
}
