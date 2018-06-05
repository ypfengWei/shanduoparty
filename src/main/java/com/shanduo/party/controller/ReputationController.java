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
			return new ErrorBean(10002,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(10002,"记录错误");
		}
		if(StringUtils.isNull(type) || !type.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Integer userToken = baseService.checkUserToken(token);
		Map<String, Object> resultMap = new HashMap<>();
		if(StringUtils.isNull(userId)) {
			if (userToken == null) {
				log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
				return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
			}
			if("1".equals(type)) {
				resultMap = scoreService.selectReputation(null, userToken, pages, pageSizes);
			} else {
				resultMap = scoreService.selectJoinActivity(userToken, pages, pageSizes);
			}
		} else {
			if("1".equals(type)) {
				resultMap = scoreService.selectReputation(null, Integer.parseInt(userId), pages, pageSizes);
			} else {
				resultMap = scoreService.selectJoinActivity(Integer.parseInt(userId), pages, pageSizes);
			}
		}
		if(resultMap == null) {
			log.error("用户不存在");
			return new ErrorBean(10002,"用户不存在");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 举报
	 * @Title: title
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param report 举报者
	 * @param @param beReported 被举报者
	 * @param @param activityId 活动Id
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "saveReport", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean saveReport(HttpServletRequest request, String report, String beReported, String activityId) {
		if(StringUtils.isNull(report)) {
			log.error("举报者id为空");
			return new ErrorBean(10002,"举报者id为空");
		}
		if(StringUtils.isNull(beReported)) {
			log.error("被举报者id为空");
			return new ErrorBean(10002,"被举报者id为空");
		}
		try {
			scoreService.report(activityId, Integer.parseInt(report), Integer.parseInt(beReported));
		} catch (Exception e) {
			log.error("举报记录添加失败");
			return new ErrorBean(10003,"举报失败");
		}
		return new SuccessBean();
	}
	
	@RequestMapping(value = "report", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean report(HttpServletRequest request, String activityId, String type) {
		if(StringUtils.isNull(activityId)) {
			log.error("活动id为空");
			return new ErrorBean(10002,"活动为空");
		}
		if(StringUtils.isNull(type) || !type.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		try {
			scoreService.updateReputation(activityId,type);
		} catch (Exception e) {
			log.error("举报记录添加失败");
			return new ErrorBean(10003,"举报失败");
		}
		return new SuccessBean();
	}
}
