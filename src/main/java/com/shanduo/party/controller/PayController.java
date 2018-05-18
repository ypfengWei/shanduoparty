package com.shanduo.party.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shanduo.party.service.OrderService;

/**
 * 第三方支付控制层
 * @ClassName: PayController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月18日 下午3:43:35
 *
 */
@Controller
@RequestMapping(value = "jpay")
public class PayController {

	private static final Logger log = LoggerFactory.getLogger(PayController.class);
	
	@Autowired
	private OrderService orderService;
	
	
}
