package com.sgnbs.crud.util;

import com.sgnbs.crud.eum.AbstractAutoConfigEums;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EumUtil {

	private static final String KEY_SPLIT_STR = "-";

	public static List<KeyValue> getByTFname(String tablename,String fieldname){
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		Map<String,String> eums = AbstractAutoConfigEums.getEums();
		for(String key : eums.keySet()) {
			String []tfname = key.split(KEY_SPLIT_STR);
			if(tfname[0].equalsIgnoreCase(tablename) && tfname[1].equalsIgnoreCase(fieldname)) {
				String keyvaluestr = eums.get(key);
				if(StrUtil.isBlank(keyvaluestr)) {
					continue;
				}
				String  []keyvaluesz = keyvaluestr.split(",");
				for(String kv : keyvaluesz) {
					if(StrUtil.isBlank(kv)) {
						continue;
					}
					String []kvsz = kv.split("=");
					if(kvsz.length!=2) {
						continue;
					}
					kvs.add(new EumUtil().new KeyValue(Integer.parseInt(kvsz[0]),kvsz[1]));
				}
				break;
			}
		}
		return kvs;
	}

	public static String getEumValuename(String tablename,String fieldname,String keystr){
		List<KeyValue> kvs = getByTFname(tablename, fieldname);
		for(KeyValue kv : kvs) {
			if(String.valueOf(kv.getKey()).equals(keystr)) {
				return kv.getValue();
			}
		}
		return null;
	}

	public static String getEumNamevalue(String tablename,String fieldname,String valuestr){
		List<KeyValue> kvs = getByTFname(tablename, fieldname);
		for(KeyValue kv : kvs) {
			if(String.valueOf(kv.getValue()).equals(valuestr)) {
				return kv.getKey()+"";
			}
		}
		return null;
	}

	
	
	public class KeyValue {
		
		private Integer key;
		
		private String value;
		
		KeyValue(Integer key,String value){
			this.key=key;
			this.value=value;
		}

		public Integer getKey() {
			return key;
		}

		public void setKey(Integer key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
}
