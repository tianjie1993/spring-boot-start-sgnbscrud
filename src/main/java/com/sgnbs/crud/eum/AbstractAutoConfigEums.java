package com.sgnbs.crud.eum;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAutoConfigEums {


    private static final Map<String,String> EUMS= new HashMap<>();

    protected  AbstractAutoConfigEums configEums(Map<String,String> eums){
        EUMS.putAll(eums);
        return this;
    }

    public abstract void bindEums();

    public static Map<String,String> getEums(){
        return EUMS;

    }
}
