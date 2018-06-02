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
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.util.JsonStringUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 评分控制层
 * @ClassName: ScoreController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月9日 下午4:29:13
 */
@Controller
@RequestMapping(value = "score")
public class ScoreController {
	
	private static final Logger log = LoggerFactory.getLogger(ScoreController.class);
	
	@Autowired
	private BaseService baseService;
	
	@Autowired
	private ScoreService scoreService;
	
	/**
	 * 对参加的活动进行评价
	 * @Title: saveActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId
	 * @param @param score
	 * @param @param evaluationcontent
	 * @param @param remarks
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "updateScore", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean updateScore(HttpServletRequest request, String token, String activityId, String score,
			String evaluationcontent) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean(10002,"活动ID为空");
		}
		if (StringUtils.isNull(score) || !score.matches("^[1-5]$")) {
			log.error("评分错误");
			return new ErrorBean(10002,"评分错误");
		}
		try {
			scoreService.updateActivityScore(userToken, activityId, Integer.parseInt(score), evaluationcontent);
		} catch (Exception e) {
			return new ErrorBean(10003,"添加失败");
		}
		return new SuccessBean("添加成功");
	}
	
	/**
	 * 发起人对参与人进行评价
	 * @Title: updateOthersScore
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId
	 * @param @param othersscore
	 * @param @param evaluationcontent
	 * @param @param remarks
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "updateOthersScore", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean updateOthersScore(HttpServletRequest request, String token, String activityId,String data) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(activityId)){
			log.error("活动ID为空");
			return new ErrorBean(10002,"活动ID为空");
		}
		if(StringUtils.isNull(data) || data.isEmpty()) {
			log.error("没有进行评价");
			return new ErrorBean(10002,"没有进行评价");
		}
		List<Object> list = JsonStringUtils.getList(data);
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			int score = Integer.parseInt(map.get("score").toString());
			if (StringUtils.isNull(score+"") || !(score+"").matches("^[1-5]$")) {
				log.error("评分错误");
				return new ErrorBean(10002,"评分错误");
			}
			int userId = Integer.parseInt(map.get("userId").toString());
			String evaluated = map.get("evaluated").toString();
			try {
				scoreService.updateByUserId(userId, activityId, score, evaluated);
			} catch (Exception e) {
				return new ErrorBean(10003,"评价失败");
			}
		}
		return new SuccessBean("评价成功");
	}
	
	/**
	 * 查询历史评价
	 * @Title: selActivityScore
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @return    设定文件
	 * @return List<ActivityScore>    返回类型
	 * @throws
	 */
	@RequestMapping(value = "selHistoryScore", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean selActivityScore(HttpServletRequest request, String token, String page, String pageSize) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
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
		Map<String, Object> activityScores = scoreService.selectByIdScore(userToken, pages, pageSizes);
		return new SuccessBean(activityScores);
	}
	
}