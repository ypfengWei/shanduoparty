package com.shanduo.party.controller;

import java.util.HashMap;
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
import com.shanduo.party.service.DynamicService;
import com.shanduo.party.service.ExperienceService;
import com.shanduo.party.service.PraiseService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 动态控制层
 * @ClassName: DynamicController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月16日 下午3:35:53
 *
 */
@Controller
@RequestMapping(value = "jdynamic")
public class DynamicController {

	private static final Logger log = LoggerFactory.getLogger(DynamicController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private DynamicService dynamicService;
	@Autowired
	private PraiseService praiseService;
	@Autowired
	private ExperienceService experienceService;
	
	/**
	 * 发表动态
	 * @Title: saveDynamic
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param content 动态内容
	 * @param @param picture 图片
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @param location 位置
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "savedynamic",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveDynamic(HttpServletRequest request,String token,String content,String picture,String lat, String lon,String location) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(content) && StringUtils.isNull(picture)) {
			log.error("内容为空");
			return new ErrorBean(10002,"内容为空");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度错误");
			return new ErrorBean(10002,"纬度错误");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度错误");
			return new ErrorBean(10002,"经度错误");
		}
		try {
			dynamicService.saveDynamic(isUserId, content, picture, lat, lon, location);
		} catch (Exception e) {
			log.error("发表动态失败");
			return new ErrorBean(10003,"发表动态失败");
		}
		//添加每日发表动态经验值，日限制2次/5点经验
		if(!experienceService.checkCount(isUserId, "4")) {
			try {
				experienceService.addExperience(isUserId, "4");
			} catch (Exception e) {
				log.error("发表动态获得经验失败");
			}
		}
		return new SuccessBean("发表动态成功");
	}
	
	/***
	 * 查看动态
	 * @Title: dynamicList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 1.附近;2.好友;3.我的动态;4.别人的动态
	 * @param @param userId
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "dynamicList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean dynamicList(HttpServletRequest request,String token,String typeId,String userId,String lat,String lon,
			String page,String pageSize) {
		Integer isUserId = baseService.checkUserToken(token);
		if(StringUtils.isNull(typeId) || !page.matches("^[1234]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(10002,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(10002,"记录错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度错误");
			return new ErrorBean(10002,"纬度错误");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度错误");
			return new ErrorBean(10002,"经度错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<>(3);
		if("1".equals(typeId)) {
			resultMap = dynamicService.nearbyList(lat, lon, pages, pageSizes);
			return new SuccessBean(resultMap);
		}
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if("2".equals(typeId)) {
			resultMap = dynamicService.attentionList(isUserId, lat, lon, pages, pageSizes);
		}else if("3".equals(typeId)) {
			resultMap = dynamicService.dynamicList(isUserId,lat, lon, pages, pageSizes);
		}else {
			if(StringUtils.isNull(userId) || PatternUtils.patternUser(userId)) {
				log.error("闪多号格式错误");
				return new ErrorBean(10002,"闪多号格式错误");
			}
			resultMap = dynamicService.dynamicList(Integer.valueOf(userId),lat, lon, pages, pageSizes);
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 动态点赞或取消
	 * @Title: isPraise
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param dynamicId 动态ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "ispraise",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean isPraise(HttpServletRequest request,String token,String dynamicId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(dynamicId)) {
			log.error("动态ID为空");
			return new ErrorBean(10002,"动态ID为空");
		}
		if(dynamicService.checkDynamic(dynamicId)) {
			log.error("动态不存在");
			return new ErrorBean(10002,"动态不存在");
		}
		if(praiseService.checkPraise(isUserId, dynamicId)) {
			try {
				praiseService.deletePraise(isUserId, dynamicId);
			} catch (Exception e) {
				log.error("取消失败");
				return new ErrorBean(10003,"取消失败");
			}
			return new SuccessBean("-1");
		}
		try {
			praiseService.savePraise(isUserId, dynamicId);
		} catch (Exception e) {
			log.error("点赞失败");
			return new ErrorBean(10003,"点赞失败");
		}
		//添加每日点赞经验值，日限制10次/1点经验
		if(!experienceService.checkCount(isUserId, "6")) {
			try {
				experienceService.addExperience(isUserId, "6");
			} catch (Exception e) {
				log.error("点赞获得经验失败");
			}
		}
		return new SuccessBean("1");
	}
	
	/**
	 * 查看单个动态详情
	 * @Title: byDynamic
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param dynamicId 动态ID
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "bydynamic",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean byDynamic(HttpServletRequest request,String token,String dynamicId,String lat,String lon) {
		if(StringUtils.isNull(dynamicId)) {
			log.error("动态ID为空");
			return new ErrorBean(10002,"动态ID为空");
		}
		if(!StringUtils.isNull(lat) && !PatternUtils.patternLatitude(lat)) {
			log.error("纬度错误");
			return new ErrorBean(10002,"纬度错误");
		}
		if(!StringUtils.isNull(lon) && !PatternUtils.patternLatitude(lon)) {
			log.error("经度错误");
			return new ErrorBean(10002,"经度错误");
		}
		Map<String, Object> resultMap = dynamicService.selectById(dynamicId,lat, lon);
		if(resultMap == null) {
			log.error("动态不存在");
			return new ErrorBean(10002,"动态不存在");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 查看单个一级评论
	 * @Title: byComment
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param commentId 评论ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "bycomment",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean byComment(HttpServletRequest request,String token,String commentId) {
		if(StringUtils.isNull(commentId)) {
			log.error("评论ID为空");
			return new ErrorBean(10002,"评论ID为空");
		}
		Map<String, Object> resultMap = dynamicService.selectByCommentId(commentId);
		if(resultMap == null) {
			log.error("评论不存在");
			return new ErrorBean(10002,"评论不存在");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 查看单个动态的1级评论或者2级回复
	 * @Title: commentList
	 * @Description: TODO
	 * @param @param requrst
	 * @param @param token
	 * @param @param dynamicId 动态ID
	 * @param @param commentId 1级评论ID
	 * @param @param page 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "commentList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean commentList(HttpServletRequest requrst,String token,String dynamicId,
			String commentId,String page,String pageSize) {
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
		Map<String, Object> resultMap = new HashMap<>(3);
		if(StringUtils.isNull(dynamicId) && StringUtils.isNull(commentId)) {
			log.error(ErrorCodeConstants.PARAMETER);
			return new ErrorBean(10002,ErrorCodeConstants.PARAMETER);
		}
		if(!StringUtils.isNull(dynamicId)) {
			resultMap = dynamicService.commentList(dynamicId, pages, pageSizes);
		}
		if(!StringUtils.isNull(commentId)) {
			resultMap = dynamicService.commentsList(commentId, pages, pageSizes);
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 评论和回复
	 * @Title: saveComment
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param dynamicId 动态ID
	 * @param @param comment 评论内容
	 * @param @param typeId 评论类型:1.1级,2.2级
	 * @param @param commentId 1级评论ID
	 * @param @param respondent 1级评论用户ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "savecomment",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveComment(HttpServletRequest request,String token,String dynamicId,
			String comment,String typeId,String commentId,String respondent) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(dynamicId)) {
			log.error("动态ID为空");
			return new ErrorBean(10002,"动态ID为空");
		}
		if(StringUtils.isNull(comment)) {
			log.error("评论内容为空");
			return new ErrorBean(10002,"评论内容为空");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("评论类型错误");
			return new ErrorBean(10002,"评论类型错误");
		}
		if("2".equals(typeId)) {
			if(StringUtils.isNull(commentId)) {
				log.error("评论ID为空");
				return new ErrorBean(10002,"评论ID为空");
			}
			if(StringUtils.isNull(respondent) || PatternUtils.patternUser(respondent)) {
				log.error("被回复用户ID错误");
				return new ErrorBean(10002,"被回复用户ID错误");
			}
		}
		try {
			dynamicService.saveDynamicComment(isUserId, dynamicId, comment, typeId, commentId, respondent);
		} catch (Exception e) {
			log.error("评论失败");
			return new ErrorBean(10003,"评论失败");
		}
		//添加每日评论动态经验值，日限制5次/2点经验
		if(!experienceService.checkCount(isUserId, "7")) {
			try {
				experienceService.addExperience(isUserId, "7");
			} catch (Exception e) {
				log.error("评论获得经验失败");
			}
		}
		return new SuccessBean("评论成功");
	}
	
	/**
	 * 批量删除动态
	 * @Title: hideDynamics
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param dynamicIds 动态ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "hidedynamics",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean hideDynamics(HttpServletRequest request,String token,String dynamicIds) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(dynamicIds)) {
			log.error("动态ID为空");
			return new ErrorBean(10002,"动态ID为空");
		}
		try {
			dynamicService.hideDynamic(dynamicIds, isUserId);
		} catch (Exception e) {
			log.error("删除失败");
			return new ErrorBean(10003,"删除失败");
		}
		return new SuccessBean("删除成功");
	}
	
	/**
	 * 删除评论
	 * @Title: hideComment
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param commentId 评论ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "hidecomment",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean hideComment(HttpServletRequest request,String token,String commentId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(commentId)) {
			log.error("评论ID为空");
			return new ErrorBean(10002,"评论ID为空");
		}
		try {
			dynamicService.hideComment(commentId, isUserId);
		} catch (Exception e) {
			log.error("删除失败");
			return new ErrorBean(10003,"删除失败");
		}
		return new SuccessBean("删除成功");
	}
	
}
