package com.shanduo.party.controller;

import java.math.BigDecimal;
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
import com.shanduo.party.service.RegionService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 区域提成控制层
 * @ClassName: RegionController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月4日 下午2:39:13
 *
 */
@Controller
@RequestMapping(value = "jregion")
public class RegionController {

	private static final Logger log = LoggerFactory.getLogger(RegionController.class);
	
	@Autowired
	private RegionService regionService;
	
	/**
	 * 区域提成代理登录
	 * @Title: login
	 * @Description: TODO
	 * @param @param request
	 * @param @param account 账号
	 * @param @param password 密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "login",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean login(HttpServletRequest request,String account,String password) {
		if(StringUtils.isNull(account) || PatternUtils.patternPassword(account)) {
			log.error("账号格式错误");
			return new ErrorBean(10002, "账号格式错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.error("密码格式错误");
			return new ErrorBean(10002, "密码格式错误");
		}
		Map<String, Object> resultMap = regionService.loginRegion(account, password);
		if(resultMap == null) {
			log.error("账号或密码错误");
			return new ErrorBean(10002, "账号或密码错误");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 区域代理修改密码
	 * @Title: updatePassword
	 * @Description: TODO
	 * @param @param request
	 * @param @param userId
	 * @param @param password 旧密码
	 * @param @param newPassword 新密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updatepassword",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updatePassword(HttpServletRequest request,String userId,String password,String newPassword) {
		if(StringUtils.isNull(userId)) {
			log.error("用户ID为空");
			return new ErrorBean(10002, "用户ID为空");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.error("密码格式错误");
			return new ErrorBean(10002, "密码格式错误");
		}
		if(StringUtils.isNull(newPassword) || PatternUtils.patternPassword(newPassword)) {
			log.error("新密码格式错误");
			return new ErrorBean(10002, "新密码格式错误");
		}
		Integer userIds = Integer.parseInt(userId);
		try {
			regionService.updatePassword(userIds, password, newPassword);
		} catch (Exception e) {
			log.error("旧密码错误");
			return new ErrorBean(10002, "旧密码错误");
		}
		return new SuccessBean("修改成功");
	}
	
	/**
	 * 查询区域代理当月提成
	 * @Title: monthCurrent
	 * @Description: TODO
	 * @param @param request
	 * @param @param userId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "monthcurrent",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean monthCurrent(HttpServletRequest request,String userId) {
		if(StringUtils.isNull(userId)) {
			log.error("用户ID为空");
			return new ErrorBean(10002, "用户ID为空");
		}
		Integer userIds = Integer.parseInt(userId);
		BigDecimal count = regionService.selectCurrentMonth(userIds);
		if(count == null) {
			log.error("没有此用户");
			return new ErrorBean(10002, "没有此用户");
		}
		return new SuccessBean(count);
	}
	
	/**
	 * 查看历史月度提成
	 * @Title: countList
	 * @Description: TODO
	 * @param @param request
	 * @param @param userId
	 * @param @param page
	 * @param @param pageSize
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "countList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean countList(HttpServletRequest request,String userId,String page,String pageSize) {
		if(StringUtils.isNull(userId)) {
			log.error("用户ID为空");
			return new ErrorBean(10002, "用户ID为空");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(10002,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(10002,"记录错误");
		}
		Integer userIds = Integer.parseInt(userId);
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = regionService.countList(userIds, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
}
