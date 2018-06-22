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

import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.VipService;

/**
 * vip控制层
 * @ClassName: VipController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月15日 下午3:30:49
 *
 */
@Controller
@RequestMapping(value = "vip")
public class VipController {
	
	private static final Logger log = LoggerFactory.getLogger(VipController.class);
	
	@Autowired
	private BaseService baseService;
	
	@Autowired
	private VipService vipService;
	
	/**
	 * 获取vip可升级的最高月份
	 * @Title: getMonth
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "getMonth", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean getMonth(HttpServletRequest request, String token) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.TOKEN_INVALID,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		int i =	vipService.getMonth(userToken);
		if(i < 1) {
			log.error("不能升级vip");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"vip不能升级");
		}
		return new SuccessBean(i);
	}
	
	/**
	 * 获取vip经验等级
	 * @Title: getVipLevel
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "getVipLevel", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean getVipLevel(HttpServletRequest request, String token) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.TOKEN_INVALID,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Map<String, Object> resultMap = vipService.vipLecel(userToken);
		return new SuccessBean(resultMap);
	}
}
