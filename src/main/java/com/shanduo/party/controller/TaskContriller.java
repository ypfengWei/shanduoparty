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

import com.shanduo.party.common.ErrorCodeConsts;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.TaskService;

/**
 * 任务控制层
 * @ClassName: TaskContriller
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月20日 下午6:05:04
 *
 */
@Controller
@RequestMapping(value = "task")
public class TaskContriller {
	
	private static final Logger log = LoggerFactory.getLogger(TaskContriller.class);
	
	@Autowired
	private TaskService taskService;

	@Autowired
	private BaseService baseService;
	
	/**
	 * 完成任务数
	 * @Title: releaseRecord
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "releaseRecord", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean releaseRecord(HttpServletRequest request, String token) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		Map<String, Object> resultMap =	taskService.releaseRecord(userToken);
		return new SuccessBean(resultMap);
	}
}
