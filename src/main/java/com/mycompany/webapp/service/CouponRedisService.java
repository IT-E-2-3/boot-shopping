package com.mycompany.webapp.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.mycompany.webapp.dao.CouponDao;
import com.mycompany.webapp.dao.EventDao;

@Service
@EnableCaching
public class CouponRedisService {
	private static final Logger logger = LoggerFactory.getLogger(CouponRedisService.class);

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Resource(name = "redisTemplate")
	private ValueOperations<String, Object> valueOps;

	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOps;

	@Resource
	EventDao eventdao;

	@Resource
	CouponDao coupondao;

	
	// 쿠폰 수량 지정
	@CacheEvict(value = "eamount")
	public void setCouponAmount(Integer amount) {
		logger.info("select Coupon");
		valueOps.set("eamount", amount);
	}

	@CacheEvict(value = "eamount")
	public void clearCouponAmount() {
		logger.info("clear");
	}

	// 남아있는 쿠폰 수량 확인
	@Cacheable(cacheNames = "eamount")
	public int getCouponCounts() {
		logger.info("not cached : get coupons");
		return Integer.parseInt((String) valueOps.get("eamount"));
	}

	// 이미 받아간 사람인지 확인하는 메서드
	@Cacheable(cacheNames = "epeople", unless = "#result == false")
	public boolean checkCouponMid(String mid, String eid) {
		logger.info("checkCouponMid " + valueOps.get(mid));
		if (valueOps.get(mid) != null) {
			return true;
		}
		return false;
	}

	public void insertCoupon(String mid, String eid) {
		logger.info("insertCoupon");
		valueOps.set(mid, eid);
	}

}
