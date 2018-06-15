package com.shanduo.party.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.CarouselService;

/**
 * 轮播图接口层
 * @ClassName: CarouselController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月12日 下午4:26:42
 *
 */
@Controller
@RequestMapping(value = "jcarousel")
public class CarouselController {

	private static final Logger log = LoggerFactory.getLogger(CarouselController.class);
	
	@Autowired
	private CarouselService carouselService;
	
	/**
	 * 获得所有轮播图
	 * @Title: carouselList
	 * @Description: TODO
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "carouselList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean carouselList(HttpServletRequest request) {
		List<Map<String, Object>> resultList = carouselService.carouselList();
		if(resultList == null) {
			log.error("没有轮播图");
			return new ErrorBean(10002,"没有轮播图");
		}
		return new SuccessBean(resultList);
	}
	
	
	
}
