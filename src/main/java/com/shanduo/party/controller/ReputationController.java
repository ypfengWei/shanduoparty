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
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.util.StringUtils;

/**
 * 信誉接口
 * @ClassName: ReputationController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月30日 下午5:34:13
 *
 */
@Controller
@RequestMapping(value = "reputation")
public class ReputationController {

	private static final Logger log = LoggerFactory.getLogger(ReputationController.class);
	
	@Autowired
	private BaseService baseService;
	
	@Autowired
	private ScoreService scoreService;
	
	@RequestMapping(value = "creditDetails", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean participant(HttpServletRequest request, String token) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Map<String, Object> resultMap = scoreService.selectReputation(userToken);
		return new SuccessBean(resultMap);
	}
}
