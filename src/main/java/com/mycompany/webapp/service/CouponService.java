package com.mycompany.webapp.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.mycompany.webapp.dao.CouponDao;
import com.mycompany.webapp.dto.CouponDto;

import org.springframework.stereotype.Service;

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

	public Date getEventStartTime(String eid) {
		return couponDao.selectEventStartTIme(eid);
	}

	@Resource
	private TransactionTemplate transactionTemplate;

	public EventTransferResult issueCoupon(CouponDto coupon) {
		logger.info("실행");

		return transactionTemplate.execute(new TransactionCallback<EventTransferResult>() {

			@Override
			public EventTransferResult doInTransaction(TransactionStatus status) {
				logger.info("실행");
				try {
					logger.info("coupon.getEid() " + coupon.getEid());

					int remains = couponDao.selectRemainigCoupon(coupon.getEid());
					logger.info("remains " + remains);
					if (remains < 1) {
						redisservice.setCouponAmount(0);
						
						return EventTransferResult.FAIL_COUPON_SOLDOUT;
					}

					int isIssued = couponDao.selectCheckifCouponissued(coupon.getMid(), coupon.getEid());
					if (isIssued > 0) {
						return EventTransferResult.FAIL_COUPON_ISSUED;
					}
					redisservice.insertCoupon(coupon.getMid(), coupon.getEid());
					logger.info("isIssued " + isIssued);

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
}
