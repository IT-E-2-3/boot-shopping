package com.mycompany.webapp.controller;

import java.security.Principal;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
//import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.mycompany.webapp.dto.MemberInfoDto;
import com.mycompany.webapp.dto.OrderDto;
import com.mycompany.webapp.dto.OrderItemDto;
import com.mycompany.webapp.dto.OrderRowDetailDto;
import com.mycompany.webapp.exception.ProductSoldOutException;
import com.mycompany.webapp.service.OrderService;
import com.team3.shopping.validator.OrderFormValidator;



@Controller
@RequestMapping("/order")
public class OrderController {
   private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

   /*
    * principal 로그인아이디 알기위해 사용 
    */

   @Resource
   OrderService orderService;
////수정
//   int total_amount = 0;
//   List<OrderRowDetailDto> OrderRowList;

   @GetMapping("/")
   public String content(Model model, Principal principal, HttpSession session) {
	   

	 
	   List<OrderRowDetailDto> OrderRowList;
	   
      logger.info("실행");
      MemberInfoDto member = orderService.getMid(principal.getName());
      
      
      OrderRowList = (List<OrderRowDetailDto>) session.getAttribute("OrderRowList");
      
      int total_amount = 0;
      
      for (OrderRowDetailDto orderRowDetailDto : OrderRowList) {
         int price = (orderRowDetailDto.getPprice());
         total_amount += price * orderRowDetailDto.getOamount();

      }
    


      model.addAttribute("OrderRowList", OrderRowList);
      model.addAttribute("total_amount", total_amount);
      String mname = member.getMname();
      StringBuilder sb = new StringBuilder();
      sb.append(mname.charAt(0));
      for (int i = 0; i < mname.length() - 2; i++) {
         sb.append("*");
      }
      sb.append(mname.charAt(mname.length() - 1));
      member.setMname(sb.toString());
      model.addAttribute("member", member);

      return "order/order";
   }

   @InitBinder("orderForm")
   public void orderFormSetValidator(WebDataBinder binder) {
      logger.info("실행");

      binder.addValidators(new OrderFormValidator());
   }
   /*
    * public String orderComplete( @ModelAttribute("orderForm") @Valid OrderDto
    * order, BindingResult bindingResult) {
    * 
    * if(bindingResult.hasErrors()) { logger.info("다시 입력폼 제공 + 에러 메시지"); //forward
    * return "redirect:/order/"; } else { logger.info("정상 요청 처러후 응답 제공");
    * //redirect return "redirect:/"; } }
    */

   @GetMapping(value = "order", produces = "application/json'; charset=UTF-8")
   @ResponseBody
   public String orderForm(@ModelAttribute("orderForm") @Valid OrderDto order, Errors errors, Principal principal,
         HttpSession session) {
      logger.info("실행");

      
      int total_amount = 0;
      List<OrderRowDetailDto> OrderRowList;
      OrderRowList = (List<OrderRowDetailDto>) session.getAttribute("OrderRowList");
      
      
      
      if (errors.hasErrors()) {
         logger.info(errors.toString());
         logger.info("다시 입력폼 제공 + 에러 메시지");

         JSONObject jsonObject = new JSONObject();
         jsonObject.put("result", "fail");
         String json = jsonObject.toString(); // result : successs

         return json;
      }

      UUID temp = UUID.randomUUID();
      String oid = Long.toHexString(temp.getMostSignificantBits());
      order.setOid(oid);
      
      session.removeAttribute(oid);
      session.setAttribute("oid", oid);
      
    logger.info(order.toString());
    logger.info("---------CARD--------");
    logger.info("getOpayment : "+ order.getOpayment());
    logger.info("getOcard_installmentrate : " + order.getOcard_installmentrate());
    logger.info("getOcard_installment_period : "+order.getOcard_installment_period());
    logger.info("getOcard_name :"+order.getOcard_name());
    logger.info("---------CARD--------");
    MemberInfoDto member = orderService.getMid(principal.getName());
     //      logger.info(mid);
    
    String mid = member.getMid();
    order.setMid(mid);
//    LocalTime currentTime = LocalTime.now();
    // LocalTime targetTime = LocalTime.of(int hour, int minute, int second, int
    // nanoOfSecond);
    

    for (OrderRowDetailDto orderRowDetailDto : OrderRowList) {
       int price = (orderRowDetailDto.getPprice());
       total_amount += price * orderRowDetailDto.getOamount();

    }
    order.setOtotal_price(total_amount);
    
    session.removeAttribute(oid);
    

      OrderRowList = (List<OrderRowDetailDto>) session.getAttribute("OrderRowList");
      for (OrderRowDetailDto orderRowDetailDto : OrderRowList) {
        logger.info("orderRowDetailDto.toString() : "+orderRowDetailDto.toString());
        orderRowDetailDto.setMid(mid);
        logger.info("orderRowDetailDto.toString() : "+orderRowDetailDto.toString());
      }
      //      session.removeAttribute("OrderRowList");
    
      try {
        orderService.makeOrder(OrderRowList, oid, order);
      } catch (ProductSoldOutException e) {
        logger.error(e.getMessage());
        

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", e.getMessage());
        String json = jsonObject.toString(); // result : successs

        return json;
      }
      logger.info(order.toString());
      // 성공시
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("result", "success");
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
      order.setOdate(sdf.format(timestamp).toString());
      System.out.println("#######"+order.getOdate());
      session.setAttribute("order", order);
      String json = jsonObject.toString(); // result : successs
      
      return json;
   }

   @RequestMapping("/ordersuccess")
   public String ordersuccess(Model model, HttpSession session) {
      logger.info("실행");
      OrderDto order= (OrderDto) session.getAttribute("order");
      session.removeAttribute("order");
      String oid = order.getOid();
//      OrderDto order = (OrderDto) orderService.getOrder(oid);
      List<OrderRowDetailDto> orderItems = orderService.getProductInfo(oid);
      
//      order.setOdeposit_deadline("2021");
      model.addAttribute("order", order);
      model.addAttribute("orderItems", orderItems);

      logger.info(orderItems.toString());
      
      long cardCost = (order.getOtotal_price())/(Integer.parseInt(order.getOcard_installment_period()) )*(100-Integer.parseInt(order.getOcard_installmentrate()) );
      model.addAttribute("cardCost", cardCost);
      
     
      //odate를 기반으로 2일 후까지 입금시켜야 하는 입금기한을 주려고 했으나 실패
//      Timestamp ts= Timestamp.valueOf(order.getOdate());
//      Calendar cal = Calendar.getInstance();
//      cal.setTime(ts);
//      cal.add(Calendar.DATE, 2);
//      ts.setTime(cal.getTime().getTime());
//      
//       SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
//         order.setOdate(sdf.toString());
         
      
//      Date date = Date.valueOf(order.getOdate());
//      model.addAttribute("deadline", date.toString());
      return "order/orderSuccess";
   }

   @GetMapping("/searchOrder")
   public String getSelectedOrders(@RequestParam String startDate, @RequestParam String endDate) {
      logger.info("실행 searchOrder");
      logger.info(startDate + " " + endDate);

      return "redirect:/order/orderList?startDate=" + startDate + "&endDate=" + endDate;
   }
   
   /*
    * @InitBinder public void initBinder(WebDataBinder binder) throws Exception {
    * final DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); final
    * CustomDateEditor dateEditor = new CustomDateEditor(df, true) {
    * 
    * @Override public void setAsText(String text) throws IllegalArgumentException
    * { if ("today".equals(text)) { SimpleDateFormat dateFormat = new
    * SimpleDateFormat("yyyy-MM-dd"); String currentDate = dateFormat.format(new
    * Date()); setValue(currentDate.toString()); } else { super.setAsText(text); }
    * } }; binder.registerCustomEditor(Date.class, dateEditor); }
    */

   @GetMapping("/orderList")
   public String orderList(Model model, Principal principal, HttpSession session, 
         @RequestParam(required = false, defaultValue = "2000-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
         @RequestParam(required = false, defaultValue = "today") @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate)
         throws ParseException {
      logger.info("실행");
      logger.info("startDate" + startDate);
      logger.info("endDate" + endDate);

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      Date beforeDate = null;
      Date afterDate = null;   
      
      if (startDate != null && endDate != null) {
         logger.info("date로 변환");
         if(endDate.equals("today")) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //dateFormat
            String date = formatter.format(new Date());
            beforeDate = dateFormat.parse(date);
         }else {
            beforeDate = dateFormat.parse(endDate);
         }
         afterDate = dateFormat.parse(startDate);
         session.removeAttribute("orderList");
      }

      MemberInfoDto member = orderService.getMid(principal.getName());
      String mid = member.getMid();
      logger.info("mid " + mid);

      List<OrderDto> orders = new ArrayList<OrderDto>();
      
      //이미 세션에 모든 리스트를 저장해두고 있음
      if(session.getAttribute("orders") != null) {
         orders = (List<OrderDto>) session.getAttribute("orders");
         logger.info("orders " + orders.toString());
      }
      
      // 리스트 새로 받기
      if (orders.size() == 0) {
         logger.info("새로운 orders");
         orders = orderService.getOrderList(mid); //paging 처리
         // session.removeAttribute("orders");

         for (OrderDto order : orders) {
            String OID = order.getOid();
            logger.info("oid " + OID);

            List<OrderItemDto> orderitems = orderService.getOrderItems(OID);
            order.setOrderItems(orderitems);
            order.setProductKindNum(orderitems.size());

            logger.info(orderitems.toString());
            // 1개의 주문 이름 -- 최소 1개의 product name
            if (orderitems.size() > 0) {
               String PID = orderitems.get(0).getPid();
               String pname = orderService.getPname(PID);
               logger.info("pname " + pname);
               order.setMainItem(pname);
            }
         }
         
         model.addAttribute("orderList", orders);
         session.setAttribute("orders", orders);
         return "order/orderList";
      }
      
      //이미 orders가 있는 경우
      logger.info("이미 orders가 있는 경우 " + "beforeDate " + beforeDate + "afterDate " + afterDate);
      List<OrderDto> selectedOrders = new ArrayList<OrderDto>();
      
      // 조회 기준 -- 날짜
      if (beforeDate != null && afterDate != null) {
         logger.info("조회 기준 -- 날짜");
         for (OrderDto order : orders) {
            Date orderDate = dateFormat.parse(order.getOdate());
            
            if ((orderDate.after(afterDate)||orderDate.equals(afterDate)) && (orderDate.before(beforeDate) || orderDate.equals(beforeDate))) {
               logger.info("조회 " + order.getOdate());
               selectedOrders.add(order);
            }
         }
      }else {
         selectedOrders = orders;
      }
      
      
      if(model.containsAttribute("orderList")) {
         logger.info("contains orderList");
      }
      model.addAttribute("neworderList", selectedOrders);
      model.addAttribute("hello", "hello");
      model.addAttribute("startDate", startDate);
      model.addAttribute("endDate", endDate);
      logger.info("선택된 주문 " + selectedOrders.size());
      return "order/orderList";
      
   }

   @GetMapping("/orderDetail")
   public String orderDetail(@RequestParam String oid, Model model, HttpSession session) {
      logger.info("실행");
      OrderDto order = (OrderDto) orderService.getOrder(oid);
      List<OrderRowDetailDto> orderItems = orderService.getProductInfo(oid);
      
      System.out.println("######" + order.getOcard_installment_period());
      
      logger.info(order.toString());
      model.addAttribute("orderItems", orderItems);
      model.addAttribute("order", order);
//      long cardCost = (order.getOtotal_price())/(Integer.parseInt(order.getOcard_installment_period()) )*(100-Integer.parseInt(order.getOcard_installmentrate()) );
//      model.addAttribute("cardCost", cardCost);
//  
//      logger.info(orderItems.toString());
      return "order/orderDetail";
   }

/*
    * @InitBinder public void initBinder(WebDataBinder binder) throws Exception {
    * final DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); final
    * CustomDateEditor dateEditor = new CustomDateEditor(df, true) {
    * 
    * @Override public void setAsText(String text) throws IllegalArgumentException
    * { if ("today".equals(text)) { SimpleDateFormat dateFormat = new
    * SimpleDateFormat("yyyy-MM-dd"); String currentDate = dateFormat.format(new
    * Date()); setValue(currentDate.toString()); } else { super.setAsText(text); }
    * } }; binder.registerCustomEditor(Date.class, dateEditor); }
    */
}