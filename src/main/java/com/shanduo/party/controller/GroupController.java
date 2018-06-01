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
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.GroupService;
import com.shanduo.party.util.StringUtils;

@Controller
@RequestMapping(value = "jgroup")
public class GroupController {

	private static final Logger log = LoggerFactory.getLogger(GroupController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private GroupService groupService;
	
	/**
	 * 检查是否可以创建群聊
	 * @Title: checkGroup
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param groupType
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "checkgroup",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean checkGroup(HttpServletRequest request,String token,String groupType) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(groupType) || !groupType.matches("^[123]$")) {
			log.error("群聊类型错误");
			return new ErrorBean(10002,"群聊类型错误");
		}
		if(groupService.checkGroupType(isUserId, groupType)) {
			log.error("创建群聊已达上限");
			return new ErrorBean(10002,"创建群聊已达上限");
		}
		return new SuccessBean("可以创建");
	}
	
	/**
	 * 创建群聊
	 * @Title: saveGroup
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param groupId
	 * @param @param groupType
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "savegroup",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveGroup(HttpServletRequest request,String token,String groupId,String groupType) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(groupId)) {
			log.error("群聊ID为空");
			return new ErrorBean(10002,"群聊ID为空");
		}
		if(StringUtils.isNull(groupType) || !groupType.matches("^[123]$")) {
			log.error("群聊类型错误");
			return new ErrorBean(10002,"群聊类型错误");
		}
		if(groupService.checkGroupType(isUserId, groupType)) {
			log.error("创建群聊已达上限");
			return new ErrorBean(10002,"创建群聊已达上限");
		}
		try {
			groupService.saveGroup(isUserId, groupId, groupType);
		} catch (Exception e) {
			log.error("创建群聊失败");
			return new ErrorBean(10003,"创建群聊失败");
		}
		return new SuccessBean("创建群聊成功");
	}
	
}
