package com.rabobank.customer.exception;

public class FileHandlerException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public FileHandlerException(String message, Throwable error)
	{
		super(message,error);
	}
	public FileHandlerException(String message)
	{
		super(message);
	}

}
