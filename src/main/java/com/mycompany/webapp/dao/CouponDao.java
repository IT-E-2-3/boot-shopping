package com.mycompany.webapp.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.webapp.dto.CouponDto;



@Mapper
public interface CouponDao {
  
	public int selectRemainigCoupon(String eid);
	public int selectCheckifCouponissued(@Param("mid") String mid, @Param("eid") String eid);
	public void insertCoupon(CouponDto couponDto);
	public int updateCouponAmount(String eid);
	public Date selectEventStartTIme(String eid);
	public List<CouponDto> getCouponList(String mid);
	public int midGetCoupon(String mid);
  
}
