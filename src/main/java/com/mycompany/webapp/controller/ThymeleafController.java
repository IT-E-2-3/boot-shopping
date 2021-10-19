package com.mycompany.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.webapp.dto.Board;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/thymeleaf")
@Log
public class ThymeleafController {
	
	@RequestMapping("/content")
	public String content() {
		log.info("실행");
		return "thymeleaf/content";
	}
	
	@RequestMapping("/text")
	public String text(Model model) {
		log.info("실행");
		Board board = new Board();
		board.setBno(1);
		board.setBtitle("Spring Boot Template Engine");
		board.setBcontent("<span style='color:red'>Thymeleaf</span> is a modern server-side <b>Java template engine</b>");
		board.setMid("thymeleaf");
		board.setBdate(new Date());
		
		model.addAttribute("board",board);
		return "thymeleaf/text";
	}
	
	@RequestMapping("/javascript")
	public String javascript(Model model) {
		log.info("실행");
		model.addAttribute("name", "홍길동");
	    model.addAttribute("hobby", new String[] {"영화", "여행", "드라이빙"});
	    
	    Board board = new Board();
	    board.setBno(1);
	    board.setBtitle("스프링 부트 Template Engine");
	    board.setBcontent("Thymeleaf is a modern server-side Java template engine");
	    board.setMid("thymeleaf");
	    board.setBdate(new Date());
	    model.addAttribute("board", board);
	    
	    JSONObject jsonObject = new JSONObject(board);
	    model.addAttribute("jsonBoard", jsonObject.toString());
	    
	    return "thymeleaf/javascript";
		
	}
	
	@RequestMapping("/variableExpressions")
	public String variableExpressions(HttpSession session) {
		log.info("실행");
		if(session.getAttribute("sessionMid")==null) {
			session.setAttribute("sessionMid", "thymeleaf");
		}else {
			session.removeAttribute("sessionMid");
		}
		return "thymeleaf/variableExpressions";
	}
	
	@RequestMapping("/selectionVariableExpressions")
	public String selectionVariableExpressions(Model model) {
		log.info("실행");
		Board board = new Board();
		board.setBno(1);
		board.setBtitle("Spring Boot Template Engine");
		board.setBcontent("<span style='color:red'>Thymeleaf</span> is a modern server-side <b>Java template engine</b>");
		board.setMid("thymeleaf");
		board.setBdate(new Date());
		model.addAttribute("board", board);
		return "thymeleaf/selectionVariableExpressions";
	}
	
	@RequestMapping("/messageExpressions")
	public String messageExpressions() {
		log.info("실행");
		return "thymeleaf/messageExpressions";
	}
	
	@RequestMapping({
			"/linkUrlExpressions/{typeId}/detail",
			"/linkUrlExpressions/{typeId}/update"})
	public String linkUrlExpressions(
			@PathVariable String typeId,
			@RequestParam(defaultValue="")String productId,
			@RequestParam(defaultValue="1") int pageNo,
			Model model) {
		log.info("실행");
		log.info("typeId :" + typeId);
		log.info("productId :" + productId);
		log.info("pageNo :"+ pageNo);
		
		model.addAttribute("typeId", "t1");
	    model.addAttribute("productId", "p1");
	    model.addAttribute("pageNo", "1");
	    model.addAttribute("url1", "/thymeleaf/linkUrlExpressions/t1/detail");
	    model.addAttribute("url2", "/t1/detail");
		return "thymeleaf/linkUrlExpressions";
	}
	
	 @RequestMapping("/builtinObject")
	 public String builtinObject(HttpServletRequest request, HttpSession session, Model model) {
	    log.info("실행");
	 
		//request범위에 저장
	    request.setAttribute("title", "spring boot");
	    model.addAttribute("today", new Date());
	    model.addAttribute("array", new String[] {"spring", "boot", "thymeleaf"});

	    //session 범위에 저장(같은 브라우저에서 공유)
	    session.setAttribute("sessionMid", "thymeleaf");
		
	    //application범위에 저장(모든 브라우저에서 공유)
		ServletContext application = session.getServletContext();
		application.setAttribute("visitorCount", 100);
		
	   
	   return "thymeleaf/builtinObject";
	 }
	 
	@RequestMapping("/iteration")
	public String getBoards(Model model) {
	   log.info("실행");
	   List<Board> list = new ArrayList<>();
	   for(int i=10; i<=15; i++) {
	      Board board = new Board();
	      board.setBno(i);
	      board.setBtitle("제목"+i);
	      board.setBcontent("내용"+i);
	      board.setMid("글쓴이"+i);
	      board.setBdate(new Date());
	      list.add(board);
	   }
	   model.addAttribute("list", list);
	   return "thymeleaf/iteration";
	}
	
	@RequestMapping("/conditional")
	public String conditional(@RequestParam(defaultValue="false") boolean option, HttpSession session, Model model) {
	   log.info("실행");
	   if(option) {
	      session.setAttribute("sessionMid", "thymeleaf");
	   } else {
	      session.removeAttribute("sessionMid");
	   }
	   
	   model.addAttribute("type", "c");
	   return "thymeleaf/conditional";
	}
}
