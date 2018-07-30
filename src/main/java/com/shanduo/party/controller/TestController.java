package com.shanduo.party.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.service.UserService;

@Controller
@RequestMapping
public class TestController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "test")
	@ResponseBody
	public Object test() {
		return userService.addService();
	}
}
