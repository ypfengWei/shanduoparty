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
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.ExperienceService;

/**
 * 经验控制层
 * @ClassName: ExperienceController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月25日 下午4:12:46
 *
 */
@Controller
@RequestMapping(value = "experience")
public class ExperienceController {

	private static final Logger log = LoggerFactory.getLogger(ExperienceController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private ExperienceService experienceService;
	
	/**
	 * 签到
	 * @Title: signIn
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "signin",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean signIn(HttpServletRequest request,String token) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		if(experienceService.checkCount(userId, "3")) {
			log.error("已签到");
			return new ErrorBean("已签到");
		}
		try {
			experienceService.signin(userId);
		} catch (Exception e) {
			log.error("签到失败");
			return new ErrorBean("签到失败");
		}
		return new SuccessBean("签到成功");
	}
	
//	if(!moneyService.checkCount(userId, "4")) {
//	try {
//		moneyService.addExperience(userId, "4");
//	} catch (Exception e) {
//		log.error("添加经验失败");
//	}
//
}
