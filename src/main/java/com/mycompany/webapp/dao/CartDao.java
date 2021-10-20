package com.mycompany.webapp.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.webapp.dto.CartDto;
import com.mycompany.webapp.dto.OrderRowDetailDto;



@Mapper
public interface CartDao {
	public int insertProduct(CartDto cart);
	public int checkCart(CartDto cart);	
	public int updateCount(CartDto cart);
	public void deleteProductByUrlSize(OrderRowDetailDto orderRowDetailDto);
	public void updateAmount(OrderRowDetailDto orderRowDetailDto);
}
