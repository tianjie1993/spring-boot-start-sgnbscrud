package com.sgnbs.crud.util;

public class CrudLocal {

    private static final ThreadLocal<String> ERROR_MSG = new ThreadLocal<String>();

    private static final ThreadLocal<Boolean> ISNEW = new ThreadLocal<Boolean>();

    private static final ThreadLocal<String> VIEWNAME = new ThreadLocal<String>();


    public static void addErrorMsg(String msg) {
        ERROR_MSG.set(msg);
    }

    public static String getErrorMsg() {
        return ERROR_MSG.get();
    }

    public static void removeErrorMsg() {
        ERROR_MSG.remove();
    }

    public static Boolean isNew() {
        if(null==ISNEW.get()) {
            return false;
        }else {
            return  ISNEW.get();
        }
    }
    public static void removeIsNew() {
        ISNEW.remove();
    }

    public static void setSaveNew() {
        ISNEW.set(true);
    }

    public static void setViewName(String viewname) {
        VIEWNAME.set(viewname);
    }

    public static String getViewName() {
        return VIEWNAME.get();
    }

    public static void removeViewName() {
        VIEWNAME.remove();
    }

}
