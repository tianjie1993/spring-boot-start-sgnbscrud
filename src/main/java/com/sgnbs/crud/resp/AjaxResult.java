package com.sgnbs.crud.resp;

public class AjaxResult {
	
	private static final String SUCCESS_CODE ="200";
	
	private static final String FALURE_CODE ="201";

	
	private  String code;
	
	private String desc;
	
	private Object result;
	
	public AjaxResult(){
		
	}
	
	public AjaxResult(String code, String desc, Object result) {
		super();
		this.code = code;
		this.desc = desc;
		this.result = result;
	}

	public static AjaxResult success(String desc,Object o){
		AjaxResult res = new AjaxResult();
		res.setCode(SUCCESS_CODE);
		res.setDesc(desc);
		res.setResult(o);
		return res;
	}
	
	public static AjaxResult success(){
		AjaxResult res = new AjaxResult();
		res.setCode(SUCCESS_CODE);
		res.setDesc("success");
		return res;
	}
	
	public static AjaxResult success(Object o){
		AjaxResult res = new AjaxResult();
		res.setCode(SUCCESS_CODE);
		res.setDesc("success");
		res.setResult(o);
		return res;
	}
	
	public static AjaxResult error(String desc,Object o){
		AjaxResult res = new AjaxResult();
		res.setCode(FALURE_CODE);
		res.setDesc(desc);
		res.setResult(o);
		return res;
	}
	public static AjaxResult error(String desc){
		AjaxResult res = new AjaxResult();
		res.setCode(FALURE_CODE);
		res.setDesc(desc);
		return res;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
