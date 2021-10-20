package com.mycompany.webapp.dto;

import java.sql.Date;
//import java.util.Date;
import lombok.Data;

@Data
public class CouponDto {
	private String mid;
	private String eid;
	private Date coupon_startdate;
	private Date coupon_expiredate;
	private String coupon_type;
	private String coupon_state;	
	//private char coupon_state;
	
	//event테이블의 내용을 가져오기 위 
	private String ename;
}
