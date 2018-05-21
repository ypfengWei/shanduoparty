package com.shanduo.party.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.entity.service.ActivityInfo;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.VipService;

@Controller
@RequestMapping(value = "vip")
public class VipController {
	
	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private VipService vipService;
	
	@Autowired
	private BaseService baseService;
	
	@RequestMapping(value = "showVip", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean queryHotActivity(HttpServletRequest request,String token) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		ActivityInfo activityInfo = vipService.selectByUserIds(userToken.getUserId());
		if(activityInfo == null) {
			log.error("您不是会员");
			return new ErrorBean("您不是会员");
		}
		return new SuccessBean(activityInfo);
	}
}
