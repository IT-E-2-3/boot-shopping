package com.mycompany.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Controller
@Log
public class HomeController {
	
	@RequestMapping("/")
	public String home() {
		log.info("실행");
		//src/main/resources/templates에서 home.html을 찾는다.
		return "home";
	}
	
	@RequestMapping("/order")
	public String order() {
		log.info("실행");
		//src/main/resources/templates에서 home.html을 찾는다.
		return "order";
	}
}
