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
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.util.StringUtils;

/**
 * 信誉控制层
 * @ClassName: ReputationController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月30日 下午5:34:13
 */
@Controller
@RequestMapping(value = "reputation")
public class ReputationController {

	private static final Logger log = LoggerFactory.getLogger(ReputationController.class);
	
	@Autowired
	private BaseService baseService;
	
	@Autowired
	private ScoreService scoreService;
	
	/**
	 * 信誉轨迹
	 * @Title: creditDetails
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param userId 用户Id，看别人的信誉轨迹
	 * @param @param page 页码 
	 * @param @param pageSize 记录
	 * @param @param type 1：发布的活动  2：参加的活动
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "creditDetails", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean creditDetails(HttpServletRequest request, String token, String userId, String page, String pageSize, String type) {
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"记录错误");
		}
		if(StringUtils.isNull(type) || !type.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"类型错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Integer userToken = baseService.checkUserToken(token);
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		if(StringUtils.isNull(userId)) {
			if (userToken == null) {
				log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
				return new ErrorBean(ErrorCodeConstants.TOKEN_INVALID,ErrorCodeConstants.USER_TOKEN_PASTDUR);
			}
			if("1".equals(type)) {
				resultMap = scoreService.selectReputation(null, userToken, pages, pageSizes);
			} else {
				resultMap = scoreService.selectJoinActivity(userToken, pages, pageSizes);
			}
		} else {
			if("1".equals(type)) {
				resultMap = scoreService.selectReputation(null, Integer.valueOf(userId), pages, pageSizes);
			} else {
				resultMap = scoreService.selectJoinActivity(Integer.valueOf(userId), pages, pageSizes);
			}
		}
		if(resultMap == null) {
			log.error("用户不存在");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"用户不存在");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 举报
	 * @Title: saveReport
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param report 举报者
	 * @param @param beReported 被举报者
	 * @param @param activityId 活动Id
	 * @param @param dynamicId 动态Id
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "saveReport", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean saveReport(HttpServletRequest request, String token, String activityId, String dynamicId, String typeId, 
			String remarks) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.TOKEN_INVALID,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"类型错误");
		}
		if(StringUtils.isNull(remarks)) {
			log.error("举报内容为空");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"举报内容不能为空");
		}
		String id = null;
		if("1".equals(typeId)) {
			if(StringUtils.isNull(activityId)) {
				log.error("活动为空");
				return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"活动为空");
			}
		} else {
			if(StringUtils.isNull(dynamicId)) {
				log.error("动态为空");
				return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"动态为空");
			}
		}
		id = scoreService.selectId(activityId, dynamicId, typeId, userToken);
		if(!StringUtils.isNull(id)) {
			log.error("已举报");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"已举报");
		}
		try {
			scoreService.report(userToken, activityId, dynamicId, typeId, remarks);
		} catch (Exception e) {
			log.error("举报记录添加失败");
			return new ErrorBean(ErrorCodeConstants.BACKSTAGE_ERROR,"举报失败");
		}
		return new SuccessBean("举报成功");
	}
	
	/**
	 * 查看举报的活动或动态记录
	 * @Title: reportRecord
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param typeId 1:活动记录 2:动态记录
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "reportRecord", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean reportRecord(HttpServletRequest request, String typeId, String page, String pageSize) {
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"类型错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = scoreService.reportRecord(typeId, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 举报处理
	 * @Title: ReportHandling
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param activityId 活动id
	 * @param @param type 1:通过  2:未通过
	 * @param @param dynamicId 动态Id
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "reportHandling", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean reportHandling(HttpServletRequest request, String activityId, String type, String dynamicId) {
		if(StringUtils.isNull(activityId)&&StringUtils.isNull(dynamicId)) {
			log.error("id为空");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"id为空");
		}
		if(StringUtils.isNull(type) || !type.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"类型错误");
		}
		try {
			scoreService.updateReputation(activityId,type,dynamicId);
		} catch (Exception e) {
			log.error("信誉修改失败");
			return new ErrorBean(ErrorCodeConstants.BACKSTAGE_ERROR,"举报处理失败");
		}
		return new SuccessBean("举报处理成功");
	}
	
}
