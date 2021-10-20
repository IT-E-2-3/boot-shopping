package com.mycompany.webapp.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.webapp.dao.OrderDao;
import com.mycompany.webapp.dto.CartDto;
import com.mycompany.webapp.dto.MemberInfoDto;
import com.mycompany.webapp.dto.OrderDto;
import com.mycompany.webapp.dto.OrderItemDto;
import com.mycompany.webapp.dto.OrderRowDetailDto;
import com.mycompany.webapp.exception.ProductSoldOutException;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Resource
	OrderDao orderDao;

	public List<OrderRowDetailDto> getMyCart(String mid) {
		 return orderDao.getMyCartByMlogin(mid);
	}

	public List<CartDto> getPrice(List<CartDto> cartList) {
		return orderDao.getPriceByCartPid(cartList);
	}

	public MemberInfoDto getMid(String mlogin_id) {
		return orderDao.getMidBymolgin(mlogin_id);

	}

	public List<OrderDto> getOrderList(String mid) {

		List<OrderDto> orders = orderDao.selectAllbyMid(mid);

		return orders;
	}
	
	public OrderDto getOrder(String oid) {
		OrderDto order = orderDao.selectOrderbyOid(oid);
		
		return order;
	}

	public List<OrderItemDto> getOrderItems(String oid) {
		List<OrderItemDto> orderItems = orderDao.selectOrderItemsbyOid(oid);

		return orderItems;
	}

	public String getPname(String pid) {
		String pname = orderDao.selectPnamebyPid(pid);
		return pname;
	}
	@Transactional
	public void makeOrder(List<OrderRowDetailDto> orderRowList, String oid, OrderDto order) {
		//mid 의 cart에 있는 모든 주문에 대해서 재고를 얻어오고 재고에서 camount 뺀다
		//음수면 바로 재고없음 ->  throw new Ch16NotFoundAccountException("계좌가 존재하지 않습니다.");
		//음수가 아니면 트렌젝션 실행
//		List<Integer> stock = new LinkedList<Integer>();
//		List<Integer> camount = new LinkedList<Integer>();
		//주문생성
		orderDao.insertOrderByForm(order);
		
		for (OrderRowDetailDto orderRowDetailDto : orderRowList) {
			//재고 조회
			int stock = orderDao.selectStockByPidColor(orderRowDetailDto);
			// 장바구니 로 만든 주문에서 몇개 골랐는지 조회
			int amount = orderDao.selectAmountByPidColor(orderRowDetailDto);
			
			int stock_after = stock-amount;
			if(stock_after<0) {
				throw new ProductSoldOutException("상품명 : " +orderRowDetailDto.getPname()+": 색상 : "+orderRowDetailDto.getColor_code()+": 사이즈 : "+orderRowDetailDto.getSize_code() +"상품 재고 소진");
			}
		
			//재고 변경 객체와 변경할 값을 동시에 보내는 코드 적용
			orderDao.updateStock(orderRowDetailDto, stock_after);
				
			
			// 장바구니 변경
			// 장바구니에 담긴 수량보다 많거나 같게사면 삭제
			// 장바구니에 담긴 수량보다 적게사면 장바구니의 값 변경
			
			//장바구니에 담긴 특정 상품의 수량
			/*
			 * int cart_amount = orderDao.SelectCartAmountByPid(orderRowDetailDto);
			 * if(cart_amount-amount>0) { // cart_amount-amount로 장바구니 수량 변경 }else {
			 * orderDao.DeleteProductFromCart(orderRowDetailDto); }
			 */
			
			
			orderDao.DeleteProductFromCart(orderRowDetailDto);
			//장바구니로 만든 (orderRowList)의 모든 상품을 order_items 테이블로 이동
			orderDao.intertOrderItems(orderRowDetailDto, oid);
			
			
		}
		
	}



	public String getImageUrl(String pid, String pcolor) {
		logger.info("실행");

		String imgurl = orderDao.selectImageUrl(pid, pcolor);
		return imgurl;
	}
	
	public List<OrderRowDetailDto> getProductInfo(String pid){
		List<OrderRowDetailDto> infos = orderDao.selectProductbyOid(pid);
		return infos;
	}

	public void DeleteProductFromCart(OrderRowDetailDto orderRowDetailDto) {
		orderDao.DeleteProductFromCart(orderRowDetailDto);
	}
}