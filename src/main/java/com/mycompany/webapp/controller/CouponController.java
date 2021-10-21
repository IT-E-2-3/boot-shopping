package com.mycompany.webapp.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mycompany.webapp.dto.CouponDto;
import com.mycompany.webapp.dto.MemberInfoDto;
import com.mycompany.webapp.service.CouponService;
import com.mycompany.webapp.service.OrderService;

import lombok.extern.java.Log;

@Controller
@Log
public class CouponController {
	
	@Resource
	OrderService orderService;

	@Resource
	CouponService couponService;

	@ModelAttribute("eventStartDate")
	public Date initEvent() {
		log.info("실행");

		String eid = "2";
		Date startDate = couponService.getEventStartTime(eid);

		return startDate;
	}

	@GetMapping("/mycoupon")
	public String content(Model model, Principal principal) {
		log.info("실행");

		MemberInfoDto member = orderService.getMid(principal.getName());
		String mid = member.getMid();

		List<CouponDto> couponList = couponService.getCouponList(mid);
		model.addAttribute("couponList", couponList);
		return "member/mycoupon";
	}


}