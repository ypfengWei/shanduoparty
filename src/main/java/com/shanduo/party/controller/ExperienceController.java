package com.shanduo.party.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.common.ErrorCodeConsts;
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
@RequestMapping(value = "jexperience")
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
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		if(experienceService.checkCount(isUserId, "3")) {
			log.error("已签到");
			return new ErrorBean(10002,"已签到");
		}
		try {
			experienceService.signin(isUserId);
		} catch (Exception e) {
			log.error("签到失败");
			return new ErrorBean(10003,"签到失败");
		}
		return new SuccessBean("签到成功");
	}
	
	/**
	 * 检查签到
	 * @Title: checkSignIn
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "checksignin",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean checkSignIn(HttpServletRequest request,String token) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		Map<String, Object> resultMap = experienceService.checkSignin(isUserId);
		return new SuccessBean(resultMap);
	}
}
