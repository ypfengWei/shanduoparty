package com.shanduo.party.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.UserOrder;
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.pay.AlipayConfig;
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
	public ResultBean saveOrder(HttpServletRequest request,String token,String typeId,String money,
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
	
	/**
	 * 支付宝支付生成订单
	 * @Title: aliOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId
	 * @param @param money
	 * @param @param month
	 * @param @param activityId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "aliorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean aliOrder(HttpServletRequest request,String token,String typeId,String money,
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
		UserOrder order = orderService.selectByOrderId(orderId);
		String body = "";
		String subject = "";
		if("1".equals(order.getOrderType())) {
			body = "充值闪多余额";
		}else if("2".equals(order.getOrderType())){
			body = "开通闪多VIP"+order.getMonth()+"个月";
		}else if("3".equals(order.getOrderType())){
			body = "开通闪多SVIP"+order.getMonth()+"个月";
		}else if("4".equals(order.getOrderType())){
			body = "刷新闪多活动"+order.getActivityId();
		}else {
			body = "置顶闪多活动"+order.getActivityId();
		}
		subject = body + order.getMoney();
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", AlipayConfig.APPID, AlipayConfig.APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest requests = new AlipayTradeAppPayRequest();
		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		//一笔交易的具体描述信息
		model.setBody(body);
		//交易标题
		model.setSubject(subject);
		//订单号
		model.setOutTradeNo(orderId);
		model.setTimeoutExpress("30m");
		//订单金额
		model.setTotalAmount(order.getMoney().toString());
		model.setProductCode("QUICK_MSECURITY_PAY");
		requests.setBizModel(model);
		requests.setNotifyUrl(AlipayConfig.NOTIFY_URL);
		AlipayTradeAppPayResponse response = null;
		try {
	        //这里和普通的接口调用不同，使用的是sdkExecute
	        response = alipayClient.sdkExecute(requests);
	        System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
		} catch (AlipayApiException e) {
		       log.error("生成支付宝订单信息错误");
		       return new ErrorBean("订单错误");
		}
		return new SuccessBean(response.getBody());
	}
}
