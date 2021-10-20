package com.mycompany.webapp.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.mycompany.webapp.dao.CouponDao;
import com.mycompany.webapp.dao.EventDao;


@Service
public class CouponRedisService {
	private static final Logger logger = LoggerFactory.getLogger(CouponRedisService.class);
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Resource(name="redisTemplate")
	private ValueOperations<String, Object> valueOps;

	@Resource(name="redisTemplate")
	private SetOperations<String, String> setOps;
	
	
	@Resource
	EventDao eventdao;
	
	@Resource
	CouponDao coupondao;
	
	//시작전 쿠폰 수량 지정
	public void setCouponAmount(Integer amount) {
		valueOps.set("eamount", amount);
	}
	
	//남아있는 쿠폰 수량 확인
	public int getCouponCounts() {
		return (int) valueOps.get("eamount");
	}
	
	//이미 받아간 사람인지 확인하는 메서드
	/*
	 * { eid: [mid1 , mid2, mid3, ...] }
	 * 
	 * */
	public boolean checkCouponMid(String mid, String eid){
		
		if(setOps.isMember(eid, mid)){
			return true;
		}
		return false;
	}
	
	//회원에게 쿠폰을 나눠준 이력
	public void insertCoupon(String mid, String eid) {
		logger.info("give coupon");
		setOps.add(eid, mid);
	}
	
}
