package com.mycompany.webapp.dto;

import lombok.Data;

@Data
public class CategoryDto {
	private String category_code;
	private String category_name;
	private String top_category_code;
	private int category_level;
}
