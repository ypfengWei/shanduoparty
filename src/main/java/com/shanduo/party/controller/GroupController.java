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
import com.shanduo.party.im.TXCloudUtil;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.GroupService;
import com.shanduo.party.util.JsonStringUtils;
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
		int i = groupService.checkGroupType(isUserId, groupType);
		int error = 0;
		if("1".equals(groupType)) {
			error = 200;
		}else if("2".equals(groupType)) {
			error = 500;
		}else {
			error = 1000;
		}
		if(i==0) {
			log.error("不可以创建"+error+"群");
			return new ErrorBean(10002,"不可以创建"+error+"群");
		}
		if(i==1) {
			log.error(error+"群已达创建上限");
			return new ErrorBean(10002,error+"群已达创建上限");
		}
		return new SuccessBean("可以创建");
	}
	
	/**
	 * 
	 * @Title: isGroup
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型:1.创建;2.删除;
	 * @param @param name 群聊名称
	 * @param @param groupId 群组ID
	 * @param @param groupType 群类型:1.200;2.500;3.1000
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "isgroup",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean isGroup(HttpServletRequest request,String token,String typeId,String name,String groupId,String groupType) {
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
			if(StringUtils.isNull(name)) {
				log.error("群名称为空");
				return new ErrorBean(10002,"群名称为空");
			}
			if(groupService.checkGroupType(isUserId, groupType) != 2) {
				log.error("创建群组已达上限");
				return new ErrorBean(10002,"创建群组已达上限");
			}
			try {
				groupService.saveGroup(isUserId, name, groupId, groupType);
			} catch (Exception e) {
				log.error("创建群组失败");
				return new ErrorBean(10003,"创建失败");
			}
			return new SuccessBean("创建成功");
		}
		try {
			groupService.delGroup(isUserId, groupId);
		} catch (Exception e) {
			log.error("删除失败");
			return new ErrorBean(10003,"删除失败");
		}
		return new SuccessBean("删除成功");
	}
	
	@RequestMapping(value = "updategroup",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updateGroup(HttpServletRequest request,String token,String name,String groupId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(groupId)) {
			log.error("群组ID为空");
			return new ErrorBean(10002,"群组ID为空");
		}
		if(StringUtils.isNull(name)) {
			log.error("群名称为空");
			return new ErrorBean(10002,"群名称为空");
		}
		try {
			groupService.update(groupId, name);
		} catch (Exception e) {
			log.error("修改群组失败");
			return new ErrorBean(10003,"修改失败");
		}
		return new SuccessBean("修改成功");
	}
	 
	/**
	 * 获取用户所加入的群组
	 * @Title: groupList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "groupList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean groupList(HttpServletRequest request,String token) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		String result = TXCloudUtil.getGroupList(isUserId+"");
		Map<String, Object> resultMap = JsonStringUtils.getMap(result);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 模糊群名称查找
	 * @Title: queryName
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param name
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "queryname",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean queryName(HttpServletRequest request,String token,String name) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(name)) {
			log.error("群名称为空");
			return new ErrorBean(10002,"群名称为空");
		}
		String result = groupService.queryNameList(name);
		if(result == null) {
			return new ErrorBean(10002,"没有符合条件的群");
		}
		Map<String, Object> resultMap = JsonStringUtils.getMap(result);
		return new SuccessBean(resultMap);
	}
}
