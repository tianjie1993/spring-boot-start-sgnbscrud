package com.sgnbs.crud.util;

import java.util.UUID;

public class StrUtil {
	
	/**
	 * 首字母变小写
	 */
	public static String firstCharToLowerCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'A' && firstChar <= 'Z') {
			char[] arr = str.toCharArray();
			arr[0] += ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	
	/**
	 * 首字母变大写
	 */
	public static String firstCharToUpperCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z') {
			char[] arr = str.toCharArray();
			arr[0] -= ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	
	/**
	 * 字符串为 null 或者为  "" 时返回 true
	 */
	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim()) ? true : false;
	}
	
	/**
	 * 字符串不为 null 而且不为  "" 时返回 true
	 */
	public static boolean notBlank(String str) {
		return str == null || "".equals(str.trim()) || "null".equals(str.trim())? false : true;
	}
	
	public static boolean notBlank(String... strings) {
		if (strings == null)
			return false;
		for (String str : strings)
			if (str == null || "".equals(str.trim()))
				return false;
		return true;
	}
	
	public static boolean notNull(Object... paras) {
		if (paras == null)
			return false;
		for (Object obj : paras)
			if (obj == null)
				return false;
		return true;
	}
	
	public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString(); 
        String uuidStr=str.replace("-", "");
        return uuidStr;
      }
	
	
	/**
	 * 将key转化为的驼峰式
	 * @param key
	 * @return
	 */
	public static String getCamelCasekey(String key) {
		StringBuffer camelcasekey = new StringBuffer();
		String [] keystrs = key.split("_");
		for(int i=0;i<keystrs.length;i++) {
			if(i==0) {
				camelcasekey.append(keystrs[i].toLowerCase());
			}else {
				char[] cs=keystrs[i].toLowerCase().toCharArray();
			    cs[0]-=32;
			    camelcasekey.append(String.valueOf(cs));
			}
		}
		return camelcasekey.toString();
	}
	
	/**
	 * 将驼峰式转为key
	 * @param key
	 * @return
	 */
	public static String getKeyByCamel(String value) {
		StringBuffer keystr = new StringBuffer();
		char [] keychas = value.toCharArray();
		for(char a : keychas) {
			if(Character.isUpperCase(a)) {
				keystr.append("_"+a);
			}else {
				keystr.append(a);
			}
		}
		return keystr.toString();
	}
	
	/**
	 * 表名转为标准驼峰式。跟实体类名一样。
	 * @param key
	 * @return
	 */
	public static String getCamelClassname(String key) {
		StringBuffer camelcasekey = new StringBuffer();
		String [] keystrs = key.split("_");
		for(int i=0;i<keystrs.length;i++) {
			char[] cs=keystrs[i].toLowerCase().toCharArray();
		    cs[0]-=32;
		    camelcasekey.append(String.valueOf(cs));
		}
		return camelcasekey.toString();
	}
	
	/**
	 * 首字母大写
	 * @param key
	 * @return
	 */
	public static String firstUppercase(String str) {
		  char[] methodName = str.toCharArray();  
		  methodName[0] = toUpperCase(methodName[0]);
		  return String.valueOf(methodName);
	}
	
	public static char toUpperCase(char chars) {  
	    if (97 <= chars && chars <= 122) {  
	        chars ^= 32;  
	    }  
	    return chars;  
	} 
}
