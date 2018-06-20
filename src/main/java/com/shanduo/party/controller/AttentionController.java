package com.shanduo.party.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.AttentionService;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.UserService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 好友控制层
 * @ClassName: AttentionController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月27日 上午9:40:00
 * 
 */
@Controller
@RequestMapping(value = "jattention")
public class AttentionController {

	private static final Logger log = LoggerFactory.getLogger(AttentionController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private AttentionService attentionService;
	@Autowired
	private UserService userService;
	
	/**
	 * 搜索用户
	 * @Title: seekUser
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param query 手机号或昵称或闪多号
	 * @param @param typeId 类型:1.搜索用户;2.搜索好友
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "seekuser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean seekUser(HttpServletRequest request,String token,String query,String typeId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002, "类型错误");
		}
		List<Map<String, Object>> resultList = new ArrayList<>();
		if("1".equals(typeId)) {
			resultList = userService.seekUser(isUserId,query);
		}else {
			resultList = userService.seekAttention(isUserId,query);
		}
		return new SuccessBean(resultList);
	}
	
	/**
	 * 查询用户信息
	 * @Title: userDetails
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param userId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "userdetails",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean userDetails(HttpServletRequest request,String token,String userId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(userId) || PatternUtils.patternUser(userId)) {
			log.error("账号格式错误");
			return new ErrorBean(10002,"账号格式错误");
		}
		Map<String, Object> resultMap = userService.selectById(isUserId, Integer.valueOf(userId));
		if(resultMap == null) {
			log.error("找不到该用户");
			return new ErrorBean(10002,"找不到该用户");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 申请添加好友
	 * @Title: saveApply
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param attention 被添加人Id
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "saveapply",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveApply(HttpServletRequest request,String token,String attention) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(attention) || PatternUtils.patternUser(attention)) {
			log.error("被添加人格式错误");
			return new ErrorBean(10002,ErrorCodeConstants.PARAMETER);
		}
		Integer attentions = Integer.valueOf(attention);
		int i = attentionService.checkAttention(isUserId, attentions);
		if(i == 1) {
			log.error("已经是好友");
			return new ErrorBean(10002,"已经是好友");
		}
		if(i == 2) {
			log.error("你已被对方拉黑");
			return new ErrorBean(10002,"你已被对方拉黑");
		}
		ShanduoUser user = userService.selectByUserId(attentions);
		String addition = user.getAddition();
		if(user.getAddition().equals("0")) {
			try {
				attentionService.saveAttention(isUserId, attentions);
			} catch (Exception e) {
				log.error("添加好友失败");
				return new ErrorBean(10003,"添加好友失败");
			}
		}else if(addition.equals("1")){
			if(attentionService.checkAttentionApply(isUserId, attentions)) {
				log.error("已经申请");
				return new ErrorBean(10002,"已经申请");
			}
			try {
				attentionService.saveAttentionApply(isUserId, attentions);
			} catch (Exception e) {
				log.error("申请失败");
				return new ErrorBean(10003,"申请失败");
			}
		}
		return new SuccessBean(addition);
	}
	
	/**
	 * 查询所有申请添加我为好友的记录
	 * @Title: applyList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param page 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "applyList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean applyList(HttpServletRequest request,String token,String page,String pageSize) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(10002,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(10002,"记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = attentionService.attentionApplyList(isUserId, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 同意或拒绝添加好友
	 * @Title: isapply
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param applyId 申请ID
	 * @param @param typeId 类型:1,同意;2,拒绝
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "isapply",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean isapply(HttpServletRequest request,String token,String applyId,String typeId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(applyId)) {
			log.error("申请ID为空");
			return new ErrorBean(10002,"申请ID为空");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		try {
			attentionService.isAttentionApply(applyId, isUserId, typeId);
		} catch (Exception e) {
			log.error("操作失败");
			return new ErrorBean(10003,"操作失败");
		}
		return new SuccessBean("操作成功");
	}
	
	/**
	 * 批量软删除申请添加我为好友的记录
	 * @Title: hideApply
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param applyIds 申请记录ID集合,多个用逗号分隔
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "hideapply",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean hideApply(HttpServletRequest request,String token,String applyIds) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(applyIds)) {
			log.error("申请ID为空");
			return new ErrorBean(10002,"申请ID为空");
		}
		try {
			attentionService.hideAttentionApply(applyIds, isUserId);
		} catch (Exception e) {
			log.error("删除失败");
			return new ErrorBean(10003,"删除失败");
		}
		return new SuccessBean("删除成功");
	}
	
	/**
	 * 查询我的好友或黑名单
	 * @Title: attentionList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型:1,好友;2,拉黑
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "attentionList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean attentionList(HttpServletRequest request,String token,String typeId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		List<Map<String, Object>> resultList = attentionService.attentionList(isUserId, typeId);
		return new SuccessBean(resultList);
	}
	
	
	/**
	 * 双向删除好友或删除黑名单
	 * @Title: delAttention
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param attention 好友ID或拉黑ID
	 * @param @param typeId 类型:1,删除好友;2,删除拉黑
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "delattention",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean delAttention(HttpServletRequest request,String token,String attention,String typeId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(attention) || PatternUtils.patternUser(attention)) {
			log.error("被删除人格式错误");
			return new ErrorBean(10002,ErrorCodeConstants.PARAMETER);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		Integer attentions = Integer.valueOf(attention);
		try {
			attentionService.delAttention(isUserId, attentions, typeId);
		} catch (Exception e) {
			log.error("操作失败");
			return new ErrorBean(10003,"操作失败");
		}
		return new SuccessBean("操作成功");
	}
	
	/**
	 * 拉入黑名单
	 * @Title: blacklistAttention
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param attention 被拉黑人名单
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "blacklistattention",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean blacklistAttention(HttpServletRequest request,String token,String attention) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(attention) || PatternUtils.patternUser(attention)) {
			log.error("被拉黑人格式错误");
			return new ErrorBean(10002,ErrorCodeConstants.PARAMETER);
		}
		Integer attentions = Integer.valueOf(attention);
		try {
			attentionService.blacklistAttention(isUserId, attentions);
		} catch (Exception e) {
			log.error("拉黑失败");
			return new ErrorBean(10003,"拉黑失败");
		}
		return new SuccessBean("拉黑成功");
	}
	
}
