package com.shanduo.party.controller;

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
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.AttentionService;
import com.shanduo.party.service.BaseService;
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(attention) || attention.matches("^[1-9]\\d{4,}$")) {
			log.error("被添加人格式错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER);
		}
		Integer userId = userToken.getUserId();
		Integer attentions = Integer.valueOf(attention);
		if(attentionService.checkAttention(userId, attentions, "2")) {
			log.error("已被对方拉入黑名单");
			return new ErrorBean("对方拒绝添加好友");
		}
		if(attentionService.checkAttention(userId, attentions, "1")) {
			log.error("已经是好友");
			return new ErrorBean("已经是好友");
		}
		if(attentionService.checkAttentionApply(userId, attentions)) {
			log.error("已经申请");
			return new ErrorBean("已经申请");
		}
		try {
			attentionService.saveAttentionApply(userId, attentions);
		} catch (Exception e) {
			log.error("申请失败");
			return new ErrorBean("申请失败");
		}
		return new SuccessBean("申请成功");
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean("页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean("记录错误");
		}
		Integer userId = userToken.getUserId();
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = attentionService.attentionApplyList(userId, pages, pageSizes);
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(applyId)) {
			log.error("申请ID为空");
			return new ErrorBean("申请ID为空");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		Integer userId = userToken.getUserId();
		try {
			attentionService.isAttentionApply(applyId, userId, typeId);
		} catch (Exception e) {
			log.error("操作失败");
			return new ErrorBean("操作失败");
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(applyIds)) {
			log.error("申请ID为空");
			return new ErrorBean("申请ID为空");
		}
		Integer userId = userToken.getUserId();
		try {
			attentionService.hideAttentionApply(applyIds, userId);
		} catch (Exception e) {
			log.error("删除失败");
			return new ErrorBean("删除失败");
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
	 * @param @param page 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "attentionList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean attentionList(HttpServletRequest request,String token,String typeId,String page,String pageSize) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		List<Map<String, Object>> resultList = attentionService.attentionList(userId, typeId);
		return new SuccessBean(resultList);
	}
	
	
	/**
	 * 双向删除好友或删除黑名单
	 * @Title: delAttention
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param attention 好友ID或拉黑ID
	 * @param @param typeId 类型:1,删除好友;2,拉黑
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "delattention",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean delAttention(HttpServletRequest request,String token,String attention,String typeId) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(attention) || attention.matches("^[1-9]\\d{4,}$")) {
			log.error("被删除人格式错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		Integer userId = userToken.getUserId();
		Integer attentions = Integer.valueOf(attention);
		try {
			attentionService.delAttention(userId, attentions, typeId);
		} catch (Exception e) {
			log.error("操作失败");
			return new ErrorBean("操作失败");
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(attention) || attention.matches("^[1-9]\\d{4,}$")) {
			log.error("被拉黑人格式错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER);
		}
		Integer userId = userToken.getUserId();
		Integer attentions = Integer.valueOf(attention);
		try {
			attentionService.blacklistAttention(userId, attentions);
		} catch (Exception e) {
			log.error("拉黑失败");
			return new ErrorBean("拉黑失败");
		}
		return new SuccessBean("拉黑成功");
	}
	
}
