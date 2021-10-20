package com.mycompany.webapp.dto;

import lombok.Data;

@Data
public class EventDto {
	private String eid;
	private String ename;
	private int etotal_amount;
	private int eamount;
	private String eimg_url;
	private String estart_time; //timestamp
	private String eexpiredate; //timestamp
	private String econtent;
}
