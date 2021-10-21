package com.mycompany.webapp.controller;

import java.security.Principal;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycompany.webapp.dto.CouponDto;
import com.mycompany.webapp.dto.EventDto;
import com.mycompany.webapp.dto.EventPeople;
import com.mycompany.webapp.dto.MemberInfoDto;
import com.mycompany.webapp.service.CouponRedisService;
import com.mycompany.webapp.service.CouponService;
import com.mycompany.webapp.service.CouponService.EventTransferResult;
import com.mycompany.webapp.service.EventService;
import com.mycompany.webapp.service.OrderService;

@Controller
@RequestMapping("/event")
@EnableCaching
public class EventController {
	
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);
	private static final Logger fileLogger = LoggerFactory.getLogger("filelogger");
	/**
	 * 스레드 처리를 위함 newFixedThreadPool(threadnum)
	 */
	

	private ExecutorService multiexecutorService = Executors.newFixedThreadPool(4);

	@Resource
	EventService eservice;
	
	@Resource
	CouponService couponservice;
	
	@Resource
	CouponRedisService redisservice;
	
	@Resource
	OrderService orderservice;
	
	//이벤트 페이지 홈
	@RequestMapping("/")
	public String home(Model model, Principal principal) {
		logger.info("이벤트 페이지");
		fileLogger.info("file logger");
		
		EventDto event = eservice.getEventContent();
		MemberInfoDto member = orderservice.getMid(principal.getName());
		String mid = member.getMid();
		
		model.addAttribute("eid", "11");
		model.addAttribute("mid", mid);
		model.addAttribute("event", event);
	
		return "event/event";
	}

	//레디스에서 테스트하는 메서드
	@GetMapping(value = "rediscoupon/{mid}", produces = "application/json'; charset=UTF-8")
	@ResponseBody
	public String redisCoupon(@PathVariable("mid") String mid, Model model) throws InterruptedException, ExecutionException {
		
		JSONObject jsonObject = new JSONObject();
		
		//콜백 - 버튼이 눌리면 동작
		Callable<String> task = new Callable<String>() {
			
			//멀티 스레드 사이의 동기화 
			@Override
			public synchronized String call() throws Exception {
				
				logger.info("mid " + mid + " " + Thread.currentThread().getName() + ": 이벤트 처리");
				String eid = "11";
				
				// DB 상의 이벤트 시작 시간 날짜와 이벤트 참여 날짜 대조
				Date curDate = new Date();
				Date estartDate = couponservice.getEventStartTime(eid);
				if (curDate.before(estartDate)) {
					logger.info("beforedate");
					return "fail";
				}
				
				//쿠폰 남은 수량이 0인지 확인한다 
				int cahcedCouponNum = redisservice.getCouponCounts();
				if(cahcedCouponNum < 1) {
					logger.info(cahcedCouponNum + " soldout");
					return "fail";
				}
					
				// 이미 발급된 회원인지 확인한다
				if(redisservice.checkCouponMid(mid, eid)){ 
					logger.info("issued");
					return "fail";
				}
				
				
				/* ----------------여기까지 실패의 경우의 수 -----------------*/
				
				
				// 쿠폰 생성
				CouponDto newCoupon = new CouponDto();
				newCoupon.setEid(eid);
				newCoupon.setMid(mid);
				newCoupon.setCoupon_type("쿠폰");
				newCoupon.setCoupon_state("1");

				// 쿠폰 유효기간 설정 (임의)
				Date date = new Date();
				long timeInMilliSeconds = date.getTime();
				java.sql.Date date1 = new java.sql.Date(timeInMilliSeconds);

				newCoupon.setCoupon_startdate(date1);
				newCoupon.setCoupon_expiredate(date1);

				// 쿠폰 발급 트랜잭션
				EventTransferResult result = couponservice.issueCoupon(newCoupon);
				logger.info("transaciton info " + result + ":" + mid);

				if (result.toString().contains("FAIL")) {
					return "fail";
				}
				
				return "success";
			}
		};
		
		
		Future<String> future = multiexecutorService.submit(task);
		//callback 에서 return 된 값을 받음
		String futureResult = future.get();

		jsonObject.put("result", futureResult);
		
		//json 으로 보내서 결과 표시
		String json = jsonObject.toString();
		return json;
		
	}
}