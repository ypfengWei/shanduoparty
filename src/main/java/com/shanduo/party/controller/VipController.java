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
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.entity.service.VipInfo;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.VipService;

/**
 * vip控制层
 * @ClassName: VipController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月22日 上午11:52:56
 */
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
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		VipInfo vipInfo = vipService.selectByUserIds(isUserId);
		if(vipInfo == null) {
			log.error("您不是会员");
			return new ErrorBean("您不是会员");
		}
		return new SuccessBean(vipInfo);
	}
}
