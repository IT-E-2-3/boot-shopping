package com.team3.shopping.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.mycompany.webapp.dto.OrderDto;



public class OrderFormValidator implements Validator {
	private static final Logger logger = LoggerFactory.getLogger(OrderFormValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		logger.info("실행");
		boolean check = OrderDto.class.isAssignableFrom(clazz);
		logger.info("supports " + check);

		return check;
	}

	@Override
	public void validate(Object target, Errors errors) {
		logger.info("실행");
		OrderDto order = (OrderDto) target;

		// 필수 배송지 입력
		if (order.getOaddress() == null || order.getOzip_code() == null) {
			logger.info("배송지");
			errors.rejectValue("ozip_code", "errors.oaddress.required");
		}

		// 수령인 이름 검사
		if (order.getOrecipient() == null || order.getOrecipient().trim().equals("")) {
			logger.info("수령인 이름");
			errors.rejectValue("orecipient", "errors.oname.required");
		}
		
		//  주문자 휴대폰 번호 검사
		if (order.getOrder_tel() == null || order.getOrder_tel().trim().equals("")) {
			logger.info("주문자 번호");
			errors.rejectValue("order_tel", "errors.otel.invalid");
		} else {
			String regex = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
			// 패턴 객체 만들기
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(order.getOrecipient_tel());

			if (!matcher.matches()) {
				logger.info("주문자");
				errors.rejectValue("order_tel", "errors.otel.invalid");
			}
		}

		// 수령인 휴대폰 번호 검사
		if (order.getOrecipient_tel() == null || order.getOrecipient_tel().trim().equals("")) {
			logger.info("수령인 번호");
			errors.rejectValue("orecipient_tel", "errors.orecipient_tel.invalid");
		} else {
			String regex = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
			// 패턴 객체 만들기
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(order.getOrecipient_tel());

			if (!matcher.matches()) {
				logger.info("수령인");
				errors.rejectValue("orecipient_tel", "errors.orecipient_tel.invalid");
			}
		}

		// 수령인 이메일 검사
		if (order.getOrecipient_email() == null || order.getOrecipient_email().trim().equals("")) {
			logger.info("이메일 없음");
			errors.reject("orecipient_email", "errors.orecipient_email.required");
		} else {
			String regex = "^(.+)@(.+)$";
					
			// 패턴 객체 만들기 
			Pattern pattern = Pattern.compile(regex); 
			Matcher matcher = pattern.matcher(order.getOrecipient_email());
			if (!matcher.matches()) {
				logger.info("이메일 잘못됨");
				errors.rejectValue("orecipient_email", "errors.orecipient_email.invalid");
			}
		}

		// 배송 요청 사항 0~20자
		if (order.getOrequest() != null && order.getOrequest().length() > 20) {
			logger.info("배송 요청");
			errors.rejectValue("orequest", "errors.orequest.invalid");
		}
		// 특수 문자 추가 예정

	}

}
