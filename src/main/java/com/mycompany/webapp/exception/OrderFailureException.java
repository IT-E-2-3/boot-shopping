package com.mycompany.webapp.exception;

public class OrderFailureException extends RuntimeException {
	
	public OrderFailureException() {
		super("결제 실패");
	}

	public OrderFailureException(String msg) {
		super(msg);
	}
}
