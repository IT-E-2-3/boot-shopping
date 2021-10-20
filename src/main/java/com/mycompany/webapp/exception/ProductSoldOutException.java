package com.mycompany.webapp.exception;

public class ProductSoldOutException extends RuntimeException {
	
	public ProductSoldOutException() {
		super("상품 소진");
	}

	public ProductSoldOutException(String msg) {
		super(msg);
	}
}
