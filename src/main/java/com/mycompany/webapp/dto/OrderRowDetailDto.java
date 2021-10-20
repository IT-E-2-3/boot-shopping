package com.mycompany.webapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRowDetailDto {
	private String mid;
	private String color_code;
	private String size_code;
	private String product_detail_url1;
	private String pbrand;
	private String pname;
	private Integer pprice;
	private Integer oamount;
	

}
/*	mid 로 cart 접근하고 pid, color_code, size_code camount 얻어옴
  		
  		pid 로 pname pbrand pprice; 얻어옴
  		
  		pid + color_code 로 
	   private String product_detail_url1
	   얻어옴
	   Cart, Product, Color_url
 
 */
