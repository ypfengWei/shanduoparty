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
import com.shanduo.party.entity.UserToken;
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
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "savedynamic",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveDynamic(HttpServletRequest request,String token,String content,String picture,String lat, String lon) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(content) && StringUtils.isNull(picture)) {
			log.error("内容为空");
			return new ErrorBean("内容为空");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度错误");
			return new ErrorBean("纬度错误");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度错误");
			return new ErrorBean("经度错误");
		}
		Integer userId = userToken.getUserId();
		try {
			dynamicService.saveDynamic(userId, content, picture, lat, lon);	
		} catch (Exception e) {
			log.error("发表动态失败");
			return new ErrorBean("发表动态失败");
		}
		//添加每日发表动态经验值，日限制2次/5点经验
		if(!experienceService.checkCount(userId, "4")) {
			try {
				experienceService.addExperience(userId, "4");
			} catch (Exception e) {
				log.error("发表动态获得经验失败");
			}
		}
		return new SuccessBean("发表动态成功");
	}
	
	/***
	 * 查看好友或附近的动态
	 * @Title: attentionList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 1,附近;2,好友
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "homeList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean homeList(HttpServletRequest request,String token,String typeId,String lat,String lon,
			String page,String pageSize) {
		UserToken userToken = baseService.checkUserToken(token);
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean("页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean("记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = null;
		if("1".equals(typeId)) {
			Integer userId = null;
			if(userToken != null) {
				userId = userToken.getUserId();
			}
			if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
				log.error("纬度错误");
				return new ErrorBean("纬度错误");
			}
			if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
				log.error("经度错误");
				return new ErrorBean("经度错误");
			}
			resultMap = dynamicService.nearbyList(userId, lat, lon, pages, pageSizes);
		}else {
			if(userToken == null) {
				log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
				return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			}
			Integer userId = userToken.getUserId();
			resultMap = dynamicService.attentionList(userId, pages, pageSizes);
		}
		if(resultMap == null) {
			log.error("没有更多了");
			return new ErrorBean("没有更多了");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 我的动态或别人的动态
	 * @Title: dynamicList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param page 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "dynamicList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean dynamicList(HttpServletRequest request,String token,String page,String pageSize) {
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
		Map<String, Object> resultMap = dynamicService.dynamicList(userId, pages, pageSizes);
		if(resultMap == null) {
			log.error("没有更多了");
			return new ErrorBean("没有更多了");
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		if(StringUtils.isNull(dynamicId)) {
			log.error("动态ID为空");
			return new ErrorBean("动态ID为空");
		}
		if(praiseService.checkPraise(userId, dynamicId)) {
			try {
				praiseService.deletePraise(userToken.getUserId(), dynamicId);
			} catch (Exception e) {
				log.error("取消失败");
				return new ErrorBean("取消失败");
			}
			return new SuccessBean("取消成功");
		}
		try {
			praiseService.savePraise(userId, dynamicId);
		} catch (Exception e) {
			log.error("点赞失败");
			return new ErrorBean("点赞失败");
		}
		//添加每日点赞经验值，日限制10次/1点经验
		if(!experienceService.checkCount(userId, "6")) {
			try {
				experienceService.addExperience(userId, "6");
			} catch (Exception e) {
				log.error("点赞获得经验失败");
			}
		}
		return new SuccessBean("点赞成功");
	}
	
	/**
	 * 查看单个动态
	 * @Title: queryDynamic
	 * @Description: TODO
	 * @param @param requrst
	 * @param @param token
	 * @param @param dynamicId 动态ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "querydynamic",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean queryDynamic(HttpServletRequest requrst,String token,String dynamicId) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(dynamicId)) {
			log.error("动态ID为空");
			return new ErrorBean("动态ID为空");
		}
		Map<String, Object> resultMap = dynamicService.selectById(dynamicId, userToken.getUserId());
		if(resultMap == null) {
			log.error("动态不存在");
			return new ErrorBean("动态不存在");
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
	 * @param @param typeId 评论类型
	 * @param @param commentId 1级评论ID
	 * @param @param respondent 1级评论用户ID
	 * @param @param picture 图片
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "savecomment",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveComment(HttpServletRequest request,String token,String dynamicId,
			String comment,String typeId,String commentId,String respondent,String picture) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(dynamicId)) {
			log.error("动态ID为空");
			return new ErrorBean("动态ID为空");
		}
		if(StringUtils.isNull(comment) && StringUtils.isNull(picture)) {
				log.error("评论内容为空");
				return new ErrorBean("评论内容为空");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("评论类型错误");
			return new ErrorBean("评论类型错误");
		}
		if("2".equals(typeId)) {
			if(StringUtils.isNull(commentId)) {
				log.error("评论ID为空");
				return new ErrorBean("评论ID为空");
			}
			if(StringUtils.isNull(respondent) || PatternUtils.patternUser(respondent)) {
				log.error("被回复用户ID错误");
				return new ErrorBean("被回复用户ID错误");
			}
		}
		Integer userId = userToken.getUserId();
		try {
			dynamicService.saveDynamicComment(userId, dynamicId, comment, typeId, commentId, respondent, picture);
		} catch (Exception e) {
			log.error("评论失败");
			return new ErrorBean("评论失败");
		}
		//添加每日评论动态经验值，日限制5次/2点经验
		if(!experienceService.checkCount(userId, "7")) {
			try {
				experienceService.addExperience(userId, "7");
			} catch (Exception e) {
				log.error("评论获得经验失败");
			}
		}
		return new SuccessBean("评论成功");
	}
	
	/**
	 * 删除动态
	 * @Title: hideDynamic
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(dynamicIds)) {
			log.error("动态ID为空");
			return new ErrorBean("动态ID为空");
		}
		try {
			dynamicService.hideDynamic(dynamicIds, userToken.getUserId());
		} catch (Exception e) {
			log.error("删除失败");
			return new ErrorBean("删除失败");
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
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(commentId)) {
			log.error("评论ID为空");
			return new ErrorBean("评论ID为空");
		}
		try {
			dynamicService.hideComment(commentId, userToken.getUserId());
		} catch (Exception e) {
			log.error("删除失败");
			return new ErrorBean("删除失败");
		}
		return new SuccessBean("删除成功");
	}
	
}
