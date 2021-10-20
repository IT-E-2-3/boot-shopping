package com.mycompany.webapp.dto;

import lombok.Data;

@Data
public class CartDto {
	private String mid;
	private String pid;
	private String color_code;
	private String size_code;
	private int camount;
//	private String camount;
	private String price;
}
