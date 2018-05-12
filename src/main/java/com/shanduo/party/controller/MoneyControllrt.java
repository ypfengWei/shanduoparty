package com.shanduo.party.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.UserMoney;
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.util.StringUtils;

/**
 * 钱包控制层
 * @ClassName: MoneyControllrt
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月3日 下午2:46:21
 *
 */
@Controller
@RequestMapping(value = "jmoney")
public class MoneyControllrt {
	
	private static final Logger log = LoggerFactory.getLogger(MoneyControllrt.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private MoneyService moneyService;
	
	/**
	 * 查询用户余额
	 * @Title: getMoney
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "getmoney")
	@ResponseBody
	public ResultBean getMoney(HttpServletRequest request,String token) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		UserMoney money = moneyService.selectByUserId(userId);
		if(money == null) {
			log.error("查询钱包出错");
			return new ErrorBean("查询钱包出错");
		}
		return new SuccessBean(money);
	}
	
	/**
	 * 查询余额历史操作记录
	 * @Title: moneyList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param page 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "moneyList")
	@ResponseBody
	public ResultBean moneyList(HttpServletRequest request,String token,String page,String pageSize) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
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
		Map<String, Object> resultMap = moneyService.moneyList(userId, pages, pageSizes);
		if(resultMap == null) {
			log.error("没有更多了");
			return new ErrorBean("没有更多了");
		}
		return new SuccessBean(resultMap);
	}

}
