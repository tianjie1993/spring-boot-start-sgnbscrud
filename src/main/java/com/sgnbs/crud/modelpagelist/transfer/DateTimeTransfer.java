package com.sgnbs.crud.modelpagelist.transfer;

import com.sgnbs.crud.annotation.ListTransf;
import com.sgnbs.crud.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeTransfer {

    @ListTransf("Date")
    public String Date(Object date) throws ParseException {
        if(StrUtil.isBlank(String.valueOf(date))){
            return "";
        }
        Date d = null;
        SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            d = ssdf.parse(date.toString());
        } else {
            d = new Date();
        }
        return ssdf.format(d);

    }

    @ListTransf("time")
    public String TimeStamp(Object date) throws ParseException {
        if(StrUtil.isBlank(String.valueOf(date))){
            return "";
        }
        Date d = null;
        SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date != null) {
            d = ssdf.parse(date.toString());
        } else {
            d = new Date();
        }
        return ssdf.format(d);
    }
}
