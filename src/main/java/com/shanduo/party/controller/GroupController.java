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
import com.shanduo.party.entity.UserGroup;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.GroupService;
import com.shanduo.party.util.StringUtils;

/**
 * 群组控制层
 * @ClassName: GroupController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月1日 下午2:54:04
 *
 */
@Controller
@RequestMapping(value = "jgroup")
public class GroupController {

	private static final Logger log = LoggerFactory.getLogger(GroupController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private GroupService groupService;
	
	/**
	 * 检查是否可以创建群组
	 * @Title: checkGroup
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param groupType 群类型:1.200;2.500;3.1000
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
			log.error("群组类型错误");
			return new ErrorBean(10002,"群组类型错误");
		}
		if(groupService.checkGroupType(isUserId, groupType)) {
			log.error("创建群组已达上限");
			return new ErrorBean(10002,"创建群组已达上限");
		}
		return new SuccessBean("可以创建");
	}
	
	/**
	 * 创建或删除群组
	 * @Title: isGroup
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型:1.创建;2.删除;
	 * @param @param groupId 群组ID
	 * @param @param groupType 群类型:1.200;2.500;3.1000
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "isgroup",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean isGroup(HttpServletRequest request,String token,String typeId,String groupId,String groupType) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		if(StringUtils.isNull(groupId)) {
			log.error("群组ID为空");
			return new ErrorBean(10002,"群组ID为空");
		}
		if("1".equals(typeId)) {
			if(StringUtils.isNull(groupType) || !groupType.matches("^[123]$")) {
				log.error("群组类型错误");
				return new ErrorBean(10002,"群组类型错误");
			}
			UserGroup group = groupService.selectByGroupId(groupId);
			if(group != null) {
				log.error("群组已存在");
				return new ErrorBean(10002,"群组已存在");
			}
			if(groupService.checkGroupType(isUserId, groupType)) {
				log.error("创建群组已达上限");
				return new ErrorBean(10002,"创建群组已达上限");
			}
			try {
				groupService.saveGroup(isUserId, groupId, groupType);
			} catch (Exception e) {
				log.error("创建群组失败");
				return new ErrorBean(10003,"创建群组失败");
			}
			return new SuccessBean("创建群组成功");
		}
		try {
			groupService.delGroup(isUserId, groupId);
		} catch (Exception e) {
			log.error("删除群组失败");
			return new ErrorBean(10003,"删除群组失败");
		}
		return new SuccessBean("删除群组成功");
	}
	
	/**
	 * 检查群组是否人数已上限
	 * @Title: checkGroupCount
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param groupId 群组ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "checkgroupcount",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean checkGroupCount(HttpServletRequest request,String token,String groupId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(groupId)) {
			log.error("群组ID为空");
			return new ErrorBean(10002,"群组ID为空");
		}
		boolean falg = groupService.checkGroupCount(groupId);
		if(falg) {
			log.error("群成员已满");
			return new ErrorBean(10002,"群成员已满");
		}
		return new SuccessBean("可以加入");
	}
	
	/**
	 * 修改群组人数
	 * @Title: updateGroup
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param groupId 群组ID
	 * @param @param count 人数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updategroup",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updateGroup(HttpServletRequest request,String token,String groupId,String count) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(count) || !count.matches("^[1-9]\\d{0,3}$")) {
			log.error("群组人数错误");
			return new ErrorBean(10002,"群组人数错误");
		}
		if(StringUtils.isNull(groupId)) {
			log.error("群组ID为空");
			return new ErrorBean(10002,"群组ID为空");
		}
		int counts = Integer.valueOf(count);
		UserGroup group = groupService.selectByGroupId(groupId);
		if(group == null) {
			log.error("群组不存在");
			return new ErrorBean(10002,"群组不存在");
		}
		if(group.getGroupType().equals("1")) {
			if(counts > 200) {
				log.error("群组人数超过上限");
				return new ErrorBean(10003,"群组人数超过上限");
			}
		}else if(group.getGroupType().equals("2")) {
			if(counts > 500) {
				log.error("群组人数超过上限");
				return new ErrorBean(10003,"群组人数超过上限");
			}
		}else {
			if(counts > 1000) {
				log.error("群组人数超过上限");
				return new ErrorBean(10003,"群组人数超过上限");
			}
		}
		try {
			groupService.updateGroup(groupId, counts);
		} catch (Exception e) {
			log.error("修改群组失败");
			return new ErrorBean(10003,"修改群组失败");
		}
		return new SuccessBean("修改群组成功");
	}
	
	
}
