package com.swfinal.home;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController{
	@GetMapping("/home")
	@ResponseBody
	public String getHome() {
		log.info("메인페이지 요청");
		return "Hello, SW Framework";
	}
}