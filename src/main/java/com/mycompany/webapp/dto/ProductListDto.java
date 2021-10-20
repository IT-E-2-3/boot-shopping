package com.mycompany.webapp.dto;

import lombok.Data;

@Data
public class ProductListDto {
	private String pid;
	private String pname;
	private String pprice;
	private String pbrand;
	private String pdetail;
	private String color_code;
	private String product_detail_url1;
	private String product_detail_url2;
	private String product_detail_url3;
	private String color_chip_url;
	private String size_code;
	private int remaining_stock;
}
