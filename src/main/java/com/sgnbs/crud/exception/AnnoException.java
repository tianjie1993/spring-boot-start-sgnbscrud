package com.sgnbs.crud.exception;

/**
 * 错误的定义注解异常
 * @author tianj
 *
 */
public class AnnoException extends Exception{
	
	private static final long serialVersionUID = 674401378285901451L;

	@Override
	public String getMessage() {
		return "ListDo、SaveDo或ToDetail注解位置错误！";

	}
	
}
