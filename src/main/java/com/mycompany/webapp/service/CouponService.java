package com.mycompany.webapp.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

//import org.redisson.api.RBucket;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.mycompany.webapp.dao.CouponDao;
import com.mycompany.webapp.dto.CouponDto;



@Service
public class CouponService {

	private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

	@Resource
	CouponDao couponDao;

	@Resource
	CouponRedisService redisservice;



	public enum EventTransferResult {
		SUCCESS, FAIL, FAIL_COUPON_SOLDOUT, FAIL_COUPON_ISSUED
	}

	@Cacheable(value = "estarttime")
	public Date getEventStartTime(String eid) {
		logger.info("estarttime");
		return couponDao.selectEventStartTIme(eid);
	}

	@Resource private TransactionTemplate transactionTemplate;
	
	
	
	public EventTransferResult issueCoupon(CouponDto coupon) {
		logger.info("실행");

		return transactionTemplate.execute(new TransactionCallback<EventTransferResult>() {

			@Override
			public EventTransferResult doInTransaction(TransactionStatus status) {
				logger.info("실행");
				logger.info("<!-----------트랜잭션 구간 입니다 ------------!>");
				try {

					int remains = couponDao.selectRemainigCoupon(coupon.getEid());
					logger.info("remains " + remains);
					if (remains < 1) {
						redisservice.setCouponAmount(0);
						
						logger.info("before clear");
						redisservice.clearCouponAmount();
						logger.info("after clear");
						
						return EventTransferResult.FAIL_COUPON_SOLDOUT;
					}

					int isIssued = couponDao.selectCheckifCouponissued(coupon.getMid(), coupon.getEid());
					if (isIssued > 0) {
						return EventTransferResult.FAIL_COUPON_ISSUED;
					}
					logger.info("isIssued " + isIssued);
					
					redisservice.insertCoupon(coupon.getMid(), coupon.getEid());
					couponDao.updateCouponAmount(coupon.getEid());
					couponDao.insertCoupon(coupon);
					
					logger.info("updated ? ");
				} catch (Exception e) {
					logger.error(e.getMessage());
					status.setRollbackOnly();
					// 캐시 저장
					return EventTransferResult.FAIL;
				}

				return EventTransferResult.SUCCESS;
			}
		});

	}

	public List<CouponDto> getCouponList(String mid) {
		return couponDao.getCouponList(mid);
	}

	public void insertCoupon(CouponDto coupon) {
		couponDao.insertCoupon(coupon);
	}
	
	
	
	/* 분산 락 적용 시도
	 * 
	 * 
	 * public EventTransferResult issueCoupon(CouponDto coupon) { logger.info("실행");
	 * 
	 * return transactionTemplate.execute(new
	 * TransactionCallback<EventTransferResult>() {
	 * 
	 * @Override public EventTransferResult doInTransaction(TransactionStatus
	 * status) { logger.info("실행"); RBucket rBucket =
	 * redissonClient.getBucket("eamount"); System.out.println (rBucket.get());
	 * 
	 * RLock lock = redissonClient.getLock("eamount"); try {
	 * 
	 * logger.info("coupon.getEid() " + coupon.getEid());
	 * 
	 * int remains = couponDao.selectRemainigCoupon(coupon.getEid());
	 * logger.info("remains " + remains); if (remains < 1) {
	 * redisservice.setCouponAmount(0);
	 * 
	 * return EventTransferResult.FAIL_COUPON_SOLDOUT; }
	 * 
	 * int isIssued = couponDao.selectCheckifCouponissued(coupon.getMid(),
	 * coupon.getEid()); if (isIssued > 0) { return
	 * EventTransferResult.FAIL_COUPON_ISSUED; } logger.info("isIssued " +
	 * isIssued);
	 * 
	 * boolean isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS); if (!isLocked) {
	 * return EventTransferResult.FAIL; }
	 * 
	 * redisservice.insertCoupon(coupon.getMid(), coupon.getEid());
	 * redisservice.decrementCouponCount();
	 * 
	 * 
	 * couponDao.updateCouponAmount(coupon.getEid());
	 * couponDao.insertCoupon(coupon);
	 * 
	 * 
	 * } catch (Exception e) { logger.error(e.getMessage());
	 * status.setRollbackOnly();
	 * 
	 * return EventTransferResult.FAIL; }
	 * 
	 * return EventTransferResult.SUCCESS; } });
	 * 
	 * }
	 */
	
	
}
