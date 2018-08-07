package com.sgnbs.crud.modelpagelist.transfer;

import com.sgnbs.crud.annotation.ListTransf;
import com.sgnbs.crud.cache.CrudCache;

import java.lang.reflect.Method;

public abstract class AbstractDataTransfer {

    public AbstractDataTransfer() {
            configTranAction(DateTimeTransfer.class);
    }

    abstract void bind();

    protected AbstractDataTransfer configTranAction(Class<?> clz){
        Method[] methods = clz.getMethods();
        for(Method method : methods) {
            Class<?>[] types = method.getParameterTypes();
            Class returntype = method.getReturnType();
            ListTransf listTransf = method.getAnnotation(ListTransf.class);
            if(null!=listTransf && types.length==1 && types[0]==Object.class && String.class==returntype){
                CrudCache.tran_map.put(listTransf.value().toLowerCase(), new CrudCache.ClassMethod(clz,method));
            }
        }
        return this;
    }
}
