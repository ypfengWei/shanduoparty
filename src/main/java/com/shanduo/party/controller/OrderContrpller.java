package com.shanduo.party.controller;

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
import com.shanduo.party.service.OrderService;
import com.shanduo.party.util.StringUtils;

/**
 * 订单控制层
 * @ClassName: OrderContrpller
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月18日 上午9:56:59
 *
 */
@Controller
@RequestMapping(value = "jorder")
public class OrderContrpller {
	
	private static final Logger log = LoggerFactory.getLogger(OrderContrpller.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private OrderService orderService;

	/**
	 * 余额支付生成订单
	 * @Title: saveorder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 订单类型:,1.充值,2.vip,3.svip,4.活动刷新,5.活动置顶
	 * @param @param money 金额,充值才传
	 * @param @param month 月份,开通vip才传
	 * @param @param activityId 活动ID,刷新置顶才传
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "saveorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveorder(HttpServletRequest request,String token,String typeId,String money,
			String month,String activityId) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		if(StringUtils.isNull(typeId) || !typeId.matches("^[1-5]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		if("1".equals(typeId) || "2".equals(typeId)) {
			if(StringUtils.isNull(month) || !month.matches("^[1-9]\\d*$")) {
				log.error("开通vip月份错误");
				return new ErrorBean("开通vip月份错误");
			}
		}else if("3".equals(typeId)) {
			if(StringUtils.isNull(money) || !money.matches("^\\d+(\\.\\d{0,2})$")) {
				log.error("充值金额错误");
				return new ErrorBean("充值金额错误");
			}
		}else {
			if(StringUtils.isNull(activityId)) {
				log.error("活动ID为空");
				return new ErrorBean("活动ID为空");
			}
		}
		String orderId = "";
		try {
			orderId = orderService.saveOrder(userId, typeId, money, month, activityId);
		} catch (Exception e) {
			log.error("生成订单出错");
			return new ErrorBean("生成订单出错");
		}
		return new SuccessBean(orderId);
	}
}
