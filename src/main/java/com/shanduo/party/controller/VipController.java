package com.shanduo.party.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.ShanduoVip;
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.StringUtils;

@Controller
@RequestMapping(value = "vip")
public class VipController {
	
	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	@Autowired
	private VipService vipService;
	
	@Autowired
	private BaseService baseService;
	
	@RequestMapping(value = "saveVip", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean saveActivity(HttpServletRequest request, String token, String vipType, String vipStartTime, String month) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if (StringUtils.isNull(vipType)) {
			log.error("会员类别为空");
			return new ErrorBean("会员类别为空");
		}
		if (StringUtils.isNull(vipStartTime)) {
			log.error("会员开始时间为空");
			return new ErrorBean("会员开始时间为空");
		}
		if (StringUtils.isNull(month)) {
			log.error("会员时长为空");
			return new ErrorBean("会员时长为空");
		}
		List<ShanduoVip> resultList = vipService.selectByUserIdAndTime(Integer.parseInt(token));
		if (resultList != null) {
			try {
				vipService.updateByUserId(Integer.parseInt(token), Integer.parseInt(month));
			} catch (Exception e) {
				log.error("会员添加失败");
				return new ErrorBean("添加失败");
			}
			return new SuccessBean("添加成功");
		}
		try {
			vipService.insertSelective(Integer.parseInt(token), vipType, vipStartTime);
		} catch (Exception e) {
			log.error("成长值添加失败");
			return new ErrorBean("添加失败");
		}
		return new SuccessBean("添加成功");
	}
	
}	
