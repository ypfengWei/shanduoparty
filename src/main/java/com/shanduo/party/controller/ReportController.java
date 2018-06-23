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
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.ReportService;
import com.shanduo.party.util.StringUtils;

@Controller
@RequestMapping(value = "report")
public class ReportController {
	
	private static final Logger log = LoggerFactory.getLogger(ReportController.class);
	
	@Autowired
	private ReportService reportService;
	
	/**
	 * 举报
	 * @Title: saveReport
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param report 举报者
	 * @param @param beReported 被举报者
	 * @param @param activityId 活动Id
	 * @param @param dynamicId 动态Id
	 * @param @param typeId 1:活动 2:动态
	 * @param @param remarks 举报内容
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "saveReport", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean saveReport(HttpServletRequest request, String userId, String activityId, String dynamicId, String typeId, 
			String remarks) {
		Integer userIds = Integer.parseInt(userId);
		if (userId == null) {
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
		id = reportService.selectId(activityId, dynamicId, typeId, userIds);
		if(!StringUtils.isNull(id)) {
			log.error("已举报");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"已举报");
		}
		try {
			reportService.report(userIds, activityId, dynamicId, typeId, remarks);
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
	 * @param @param type 1:活动记录 2:动态记录
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "reportRecord", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean reportRecord(HttpServletRequest request, String type, String page, String pageSize) {
		if(StringUtils.isNull(type) || !type.matches("^[12]$")) {
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
		List<Map<String, Object>> resultMap = reportService.reportRecord(type, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 根据活动或者动态Id查询举报用户
	 * @Title: selectInfo
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param activityId 活动id
	 * @param @param dynamicId 动态id
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "selectInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean selectInfo(HttpServletRequest request, String activityId, String dynamicId) {
		if(StringUtils.isNull(activityId) && StringUtils.isNull(dynamicId)) {
			log.error("活动或者动态id为空");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"活动或者动态为空");
		}
		List<Map<String, Object>> resultMap = reportService.selectInfo(activityId, dynamicId);
		if(resultMap == null) {
			return new ErrorBean(ErrorCodeConstants.BACKSTAGE_ERROR,"查询失败");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 举报处理
	 * @Title: ReportHandling
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param activityId 活动id
	 * @param @param dynamicId 动态Id
	 * @param @param type 1:通过  2:未通过
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "reportHandling", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean reportHandling(HttpServletRequest request, String activityId, String dynamicId, String type) {
		if(StringUtils.isNull(activityId)&&StringUtils.isNull(dynamicId)) {
			log.error("id为空");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"id为空");
		}
		if(StringUtils.isNull(type) || !type.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"类型错误");
		}
		try {
			reportService.updateReputation(activityId,type,dynamicId);
		} catch (Exception e) {
			log.error("信誉修改失败");
			return new ErrorBean(ErrorCodeConstants.BACKSTAGE_ERROR,"举报处理失败");
		}
		return new SuccessBean("举报处理成功");
	}
}
