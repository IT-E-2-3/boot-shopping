package com.mycompany.webapp.dto;

import lombok.Data;

@Data
public class Member {
	private String mid;
	private String mname;
	private String mpassword;
	private boolean menabled;
	private String mrole;
	private String memail;
}
