package com.mycompany.webapp.dto;


import java.util.List;

import lombok.Data;

@Data
public class OrderDto {
	
	private String oid;
	private String mid;
	private String odate;
	private String ozip_code;
	private String oaddress;
	private String odetail_address;
	private String orecipient;
	private String orecipient_tel;
	private int ototal_price;	
	private String ochannel="the handsome";
	private String ocard_name;
	private String ocard_installmentrate;
	private String ocard_installment_period;
	private String oaccountholder;
	private String odeposit_deadline;
	private String order_tel;
	private String oaddtional_tel;
	private String orequest;
	private String orecipient_email;
	private String opayment;
	
	private List<OrderItemDto> orderItems;
	private String mainItem;
	private Integer productKindNum;
}
/*
 * 	
		
	로그인 정보로 얻어오는 항목
	private String mid;
	생성해야할 목록
	private String oid;
	private String odate;	
	private String ochannel="the handsome";
	
	form 으로 얻어오는 항목
	private String order_tel;
	private String ozip_code;
	private String oaddress;
	private String odetail_address;
	private String orecipient;
	private String orecipient_tel;
	private String oaddtional_tel;
	private String orequest;
	private String orecipient_email;
	
	// 이걸 어쩐다?
	private String ototal_price;
	
	결제수단으로 선택하는 항목
	private String opayment;
	//어떤 결제수단을 고르냐에 따라 html 의 hide 속성값이 변하여
	 * 추가로 선택하는 화면이 나오고
	그로부터 선택하는 값
	private String ocard_name;
	private String ocard_installmentrate;
	private String ;
	
	어떤 결제수단을 고르냐에 따라 html 의 hide 속성값이 변하여
	 * 추가로 선택하는 화면이 나오고
	그로부터 자동생성되는 값.
	
	private String oaccountholder;
	private String odeposit_deadline;
	
	


 */

/*
		private String OID;
	private String MID;
	private String ODATE;
	private Integer OZIP_CODE;
	private String OADDRESS;
	private String ORECIPIENT;
	private String ORECIPIENT_TEL;
	private Integer OTOTAL_PRICE;
	private String OCHANNEL;
	private String OCARD_NAME;
	private Integer OCARD_INSTALLMENTRATE;
	private Integer OCARD_INSTALLMENT_PERIOD;
	private String OACCOUNTHOLDER;
	private String ODEPOSIT_DEADLINE;
	
*/