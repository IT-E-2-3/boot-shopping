package com.mycompany.webapp.controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycompany.webapp.dto.CartDto;
import com.mycompany.webapp.dto.MemberInfoDto;
import com.mycompany.webapp.dto.OrderRowDetailDto;
import com.mycompany.webapp.service.CartService;
import com.mycompany.webapp.service.OrderService;



@Controller
@RequestMapping("/cart")
public class CartController {

   private static final Logger logger = LoggerFactory.getLogger(CartController.class);

   @Resource
   OrderService orderService;

   @Resource
   CartService cartService;

   int total_amount = 0;
   List<OrderRowDetailDto> OrderRowList;	

   @GetMapping(value = "/insert", produces = "application/json;charset=UTF-8")
   @ResponseBody
   public String insertProduct(@ModelAttribute CartDto cart, Principal principal) {
      MemberInfoDto member = orderService.getMid(principal.getName());
      String mid = member.getMid();
      cart.setMid(mid);
//      logger.info(cart.toString());

      int chkcart = cartService.checkCart(cart);
      if (chkcart == 0) {
         cartService.insertProduct(cart);
         logger.info("같은 제품이 없습니다.");	
      } else {
         cartService.updateProductCamount(cart);
         logger.info("같은 제품이 있어서 수량을 증가시켰습니다.");
      }

      JSONObject jsonObj = new JSONObject();
      jsonObj.put("result", "success");
      String json = jsonObj.toString();
      return json;
   }

   @RequestMapping("/")
   public String cart2(Model model, Principal principal) {

      total_amount = 0;
      logger.info("cart");
      MemberInfoDto member = orderService.getMid(principal.getName());
    

      // cart의 내용 받아오기

      String mid = member.getMid();
//      logger.info(mid);

      // cart의 내용 받아오기
      // 지금은 cart 전체를 받아오지만
      OrderRowList = orderService.getMyCart(mid);

      DecimalFormat decFormat = new DecimalFormat("###,###");
      for (OrderRowDetailDto orderRowDetailDto : OrderRowList) {
         int price = (orderRowDetailDto.getPprice());
         total_amount += price * orderRowDetailDto.getOamount();

      }
//      logger.info(total_amount+" ");

      String decimal_total_amount = decFormat.format(total_amount);

      model.addAttribute("OrderRowList", OrderRowList);
      model.addAttribute("total_amount", decimal_total_amount);

      return "cart/cart";
   }

   //// json으로 장바구니에서 주문한 내용을 받아서 세션에 주문리스트를 저장한다.
   @PostMapping("jsonCartToOrder")
   public String orderForm2(@RequestBody List<Map<String, String>> attributeMap, HttpSession session) {
      logger.info("실행");

      System.out.println(attributeMap);
      logger.info("json 결과 " + attributeMap.toString());

      List<OrderRowDetailDto> OrderRowList = new LinkedList<OrderRowDetailDto>();

      for (Map<String, String> map : attributeMap) {
         OrderRowDetailDto orderRowDetailDto = new OrderRowDetailDto();
         orderRowDetailDto.setColor_code(map.get("color_code"));
         orderRowDetailDto.setOamount(Integer.parseInt(map.get("oamount")));
         orderRowDetailDto.setPbrand(map.get("pbrand"));
         orderRowDetailDto.setPname(map.get("pname"));
         orderRowDetailDto.setPprice(Integer.parseInt(map.get("pprice")));
         orderRowDetailDto.setProduct_detail_url1(map.get("product_detail_url1"));
         orderRowDetailDto.setSize_code(map.get("size_code"));

         OrderRowList.add(orderRowDetailDto);

      }
      session.removeAttribute("OrderRowList");
      session.setAttribute("OrderRowList", OrderRowList);

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("result", "success");
      String json = jsonObject.toString(); // result : successs

      return json;

   }
   @PostMapping("jsonSave")
   public String jsonSave(@RequestBody List<Map<String, String>> attributeMap, Principal principal) {
      logger.info("실행");
      MemberInfoDto member = orderService.getMid(principal.getName());
      String mid = member.getMid();

      System.out.println(attributeMap);
      logger.info("json 결과 " + attributeMap.toString());
      
      List<OrderRowDetailDto> OrderRowList = new LinkedList<OrderRowDetailDto>();
      
      for (Map<String, String> map : attributeMap) {
         OrderRowDetailDto orderRowDetailDto = new OrderRowDetailDto();
         orderRowDetailDto.setMid(mid);
         orderRowDetailDto.setColor_code(map.get("color_code"));
         orderRowDetailDto.setOamount(Integer.parseInt(map.get("oamount")));
         orderRowDetailDto.setPbrand(map.get("pbrand"));
         orderRowDetailDto.setPname(map.get("pname"));
         orderRowDetailDto.setPprice(Integer.parseInt(map.get("pprice")));
         orderRowDetailDto.setProduct_detail_url1(map.get("product_detail_url1"));
         orderRowDetailDto.setSize_code(map.get("size_code"));
         
         cartService.updateAmount(orderRowDetailDto);
         
      }
      
      logger.info("실행");
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("result", "success");
      String json = jsonObject.toString(); // result : successs
      
      return json;
      
   }

   @PostMapping("jsonArrToDel")
   public String jsonArrToDel(@RequestBody List<Map<String, String>> attributeMap, Principal principal) {
      logger.info("실행");

      MemberInfoDto member = orderService.getMid(principal.getName());
      String mid = member.getMid();

      System.out.println(attributeMap);
      logger.info("json 결과 " + attributeMap.toString());

      List<OrderRowDetailDto> OrderRowList = new LinkedList<OrderRowDetailDto>();

      for (Map<String, String> map : attributeMap) {
         OrderRowDetailDto orderRowDetailDto = new OrderRowDetailDto();
         orderRowDetailDto.setMid(mid);
         orderRowDetailDto.setColor_code(map.get("color_code"));
         orderRowDetailDto.setProduct_detail_url1(map.get("product_detail_url1"));
         orderRowDetailDto.setSize_code(map.get("size_code"));

         cartService.DeleteProductFromCart(orderRowDetailDto);

      }
   

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("result", "success");
      String json = jsonObject.toString(); // result : successs

      return json;

   }

   //// json으로 장바구니에서 주문한 내용을 받아서 세션에 주문리스트를 저장한다.
   @PostMapping("jsondelone")
   public String jsondelone(@RequestBody OrderRowDetailDto orderRowDetailDto, Principal principal) {
      logger.info("실행");
      MemberInfoDto member = orderService.getMid(principal.getName());
      String mid = member.getMid();
      orderRowDetailDto.setMid(mid);
      logger.info("json 결과 " + orderRowDetailDto.toString());

//         OrderRowDetailDto orderRowDetailDto = new OrderRowDetailDto();

      cartService.DeleteProductFromCart(orderRowDetailDto);

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("result", "success");
      String json = jsonObject.toString(); // result : successs

      return json;

   }

}