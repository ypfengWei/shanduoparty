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

import com.shanduo.party.common.ErrorCodeConsts;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.ReputationService;
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
	private ReputationService reputationService;
	
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
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"记录错误");
		}
		if(StringUtils.isNull(type) || !type.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"类型错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Integer userToken = baseService.checkUserToken(token);
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		if(StringUtils.isNull(userId)) {
			if (userToken == null) {
				log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
				return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
			}
			if("1".equals(type)) {
				resultMap = reputationService.selectReputation(null, userToken, pages, pageSizes);
			} else {
				resultMap = reputationService.selectJoinActivity(userToken, pages, pageSizes);
			}
		} else {
			if("1".equals(type)) {
				resultMap = reputationService.selectReputation(null, Integer.valueOf(userId), pages, pageSizes);
			} else {
				resultMap = reputationService.selectJoinActivity(Integer.valueOf(userId), pages, pageSizes);
			}
		}
		if(resultMap == null) {
			log.error("用户不存在");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"用户不存在");
		}
		return new SuccessBean(resultMap);
	}
	
}
