package com.mycompany.webapp.dto;

import lombok.Data;

@Data
public class OrderItemDto {
	private String oid;
	   private String pid;
	   private String color_code;
	   private String size_code;
	   private String oamount;
	   private String mainimageurl;
}
