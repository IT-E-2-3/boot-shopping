package com.mycompany.webapp.dto;

import lombok.Data;

@Data
public class Member {
	
	private String mid;
	private String mlogin_id;
	private String mpw;
	private String mtel;
	private String memail;
	private String maddress;
	private String mname;
	private String mrole;
	private boolean menabled;
	
}
