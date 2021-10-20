package com.mycompany.webapp.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.webapp.dto.CartDto;
import com.mycompany.webapp.dto.MemberInfoDto;
import com.mycompany.webapp.dto.OrderDto;
import com.mycompany.webapp.dto.OrderItemDto;
import com.mycompany.webapp.dto.OrderRowDetailDto;



@Mapper
public interface OrderDao {

	// mid로 cart를 조회하여 구매할 상품의 OrderRowDetailDto를 얻는다.
	public List<OrderRowDetailDto> getMyCartByMlogin(String mid);

	// 로그인 정보로 mid를 얻어온다.
	public MemberInfoDto getMidBymolgin(String mlogin_id);
	

	// cart의 모든 아이템을 얻어온다.
	public List<CartDto> getPriceByCartPid(List<CartDto> cartList);

	public List<OrderDto> selectAllbyMid(String mid);

	public Integer selectOrderAmount();

	public List<OrderItemDto> selectOrderItemsbyOid(String oid);

	public String selectPnamebyPid(String pid);

	// 구매할 물건의 재고를 얻어온다.
	public int selectStockByPidColor(OrderRowDetailDto orderRowDetailDto);

	public int selectAmountByPidColor(OrderRowDetailDto orderRowDetailDto);

	public void updateStock(@Param("obj") OrderRowDetailDto orderRowDetailDto, @Param("stock_after") int stock_after);

	public void DeleteProductFromCart(OrderRowDetailDto orderRowDetailDto);

	// cart 아이템을 이동시키기
	public void intertOrderItems(@Param("obj") OrderRowDetailDto orderRowDetailDto, @Param("oid") String oid);

	// 주문생성
	public void insertOrderByForm(OrderDto order);

	public String selectImageUrl(@Param("pid") String pid, @Param("pcolor") String pcolor);

	public List<OrderRowDetailDto> selectProductbyOid(String oid);
	
	public OrderDto selectOrderbyOid(String oid);

	public int SelectCartAmountByPid(OrderRowDetailDto orderRowDetailDto);

}
