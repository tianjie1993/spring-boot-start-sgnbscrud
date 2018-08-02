package com.sgnbs.crud.exception;


public class ModelException {
	
	public class MetohdNullException extends Exception{
	
		private static final long serialVersionUID = -5358086016027912013L;
	
		@Override
		public String toString() {
			return "不存在方法.请定义。";
		}
	
	}
	
}
