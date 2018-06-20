package com.shanduo.party.controller;

import java.math.BigDecimal;
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

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.shanduo.party.common.AliPayConfig;
import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.common.WxPayConfig;
import com.shanduo.party.entity.UserOrder;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.service.OrderService;
import com.shanduo.party.util.IpUtils;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;
import com.shanduo.party.util.WxPayUtils;

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
	@Autowired
	private MoneyService moneyService;

	/**
	 * 支付订单
	 * @Title: payorder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param payId 支付类型:1.余额;2.支付宝;3.微信;4.微信小程序;5.微信公众号支付;6.赏金支付
	 * @param @param password 余额支付时传的支付密码
	 * @param @param typeId 订单类型:,1.充值,2.vip,3.svip,4.活动刷新,5.活动置顶
	 * @param @param money 金额,充值才传
	 * @param @param month 月份,开通vip才传
	 * @param @param activityId 活动ID,刷新置顶才传
	 * @param @param location 位置
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "payorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean payorder(HttpServletRequest request, String token, String payId, String password,
			String typeId, String money, String month,String activityId, String location) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(payId) || !payId.matches("^[1234]$")) {
			log.error("支付类型错误");
			return new ErrorBean(10002,"支付类型错误");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[1-5]$")) {
			log.error("消费类型错误");
			return new ErrorBean(10002,"消费类型错误");
		}
		if("1".equals(payId) && "1".equals(typeId)) {
			log.error("充值方式错误");
			return new ErrorBean(10002,"充值方式错误");
		}
		ResultBean resultBean = saveOrder(isUserId, typeId, money, month, activityId,location);
		if(!resultBean.isSuccess()) {
			return resultBean;
		}
		UserOrder order = (UserOrder) resultBean.getResult();
		if("1".equals(payId)) {
			//余额
			return payment(isUserId, order, password,"1");
		}else if("2".equals(payId)) {
			//支付宝
			return aliPayOrder(order);
		}else if("3".equals(payId)) {
			//微信
			return wxPayOrder(order,request);
		}else if("4".equals(payId)) {
			//微信小程序
			return new ErrorBean(10002,"暂不支持该支付");
		}else if("5".equals(payId)) {
			//微信公众号
			return new ErrorBean(10002,"暂不支持该支付");
		}else{
			//赏金
			return payment(isUserId, order, password,"2");
		}
	}
	
	/**
	 * 生成订单
	 * @Title: saveOrder
	 * @Description: TODO
	 * @param @param userId
	 * @param @param typeId 订单类型:,1.充值,2.vip,3.svip,4.活动刷新,5.活动置顶
	 * @param @param money 金额,充值才传
	 * @param @param month 月份,开通vip才传
	 * @param @param activityId 活动ID,刷新置顶才传
	 * @param @param location 位置
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	public ResultBean saveOrder(Integer userId,String typeId,String money,String month,String activityId,String location) {
		if(StringUtils.isNull(location)) {
			log.error("位置为空");
			return new ErrorBean(10002,"位置为空");
		}
		if("1".equals(typeId)) {
			if(StringUtils.isNull(money) || !money.matches("^\\d+(\\.\\d{0,2})$")) {
				log.error("充值金额错误");
				return new ErrorBean(10002,"充值金额错误");
			}
		}else if("2".equals(typeId) || "3".equals(typeId)) {
			if(StringUtils.isNull(month) || !month.matches("^[1-9]\\d*$")) {
				log.error("开通vip月份错误");
				return new ErrorBean(10002,"开通vip月份错误");
			}
		}else {
			if(StringUtils.isNull(activityId)) {
				log.error("活动ID为空");
				return new ErrorBean(10002,"活动ID为空");
			}
		}
		UserOrder order = new UserOrder();
		try {
			order = orderService.saveOrder(userId, typeId, money, month, activityId,location);
		} catch (Exception e) {
			log.error("生成订单出错");
			return new ErrorBean(10003,"生成订单出错");
		}
		return new SuccessBean(order);
	}
	
	/**
	 * 余额或赏金支付
	 * @Title: payment
	 * @Description: TODO
	 * @param @param userId
	 * @param @param order
	 * @param @param password
	 * @param @param typeId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	public ResultBean payment(Integer userId,UserOrder order,String password,String typeId) {
		if(StringUtils.isNull(password) || PatternUtils.patternCode(password)) {
			log.error("密码格式错误");
			return new ErrorBean(10002,"密码格式错误");
		}
		if(moneyService.checkPassword(userId, password)) {
			log.error("支付密码错误");
			return new ErrorBean(10002,"支付密码错误");
		}
		if(moneyService.checkMoney(userId,order.getMoney(),typeId)) {
			log.error("余额不足");
			return new ErrorBean(10002,"余额不足");
		}
		try {
			orderService.updateOrder(order.getId(),typeId);
		} catch (Exception e) {
			log.error("支付失败");
			return new ErrorBean(10003,"支付失败");
		}
		return new SuccessBean("支付成功");
	}
	
	/**
	 * 支付宝支付
	 * @Title: aliPayOrder
	 * @Description: TODO
	 * @param @param order
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	public ResultBean aliPayOrder(UserOrder order) {
		String body = "";
		String subject = "";
		if("1".equals(order.getOrderType())) {
			body = "充值余额";
		}else if("2".equals(order.getOrderType())){
			body = "开通VIP"+order.getMonth()+"个月";
		}else if("3".equals(order.getOrderType())){
			body = "开通SVIP"+order.getMonth()+"个月";
		}else if("4".equals(order.getOrderType())){
			body = "刷新活动";
		}else {
			body = "置顶活动";
		}
		subject = body + order.getMoney();
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", AliPayConfig.APPID, AliPayConfig.APP_PRIVATE_KEY, AliPayConfig.FORMAT, AliPayConfig.CHARSET, AliPayConfig.ALIPAY_PUBLIC_KEY,AliPayConfig.SIGNTYPE);
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest requests = new AlipayTradeAppPayRequest();
		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		//一笔交易的具体描述信息
		model.setBody(body);
		//交易标题
		model.setSubject(subject);
		//订单号
		model.setOutTradeNo(order.getId());
		model.setTimeoutExpress("30m");
		//订单金额
		model.setTotalAmount(order.getMoney().toString());
		model.setProductCode("QUICK_MSECURITY_PAY");
		requests.setBizModel(model);
		requests.setNotifyUrl(AliPayConfig.NOTIFY_URL);
		AlipayTradeAppPayResponse response = null;
		try {
	        //这里和普通的接口调用不同，使用的是sdkExecute
	        response = alipayClient.sdkExecute(requests);
//	        System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
		} catch (AlipayApiException e) {
		       log.error("生成支付宝订单信息错误");
		       return new ErrorBean(10003,"订单错误");
		}
		return new SuccessBean(response.getBody());
	}
	
	/**
	 * 微信支付
	 * @Title: wxPayOrder
	 * @Description: TODO
	 * @param @param order
	 * @param @param request
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	public ResultBean wxPayOrder(UserOrder order,HttpServletRequest request) {
		String body = "";
		if("1".equals(order.getOrderType())) {
			body = "充值余额";
		}else if("2".equals(order.getOrderType())){
			body = "开通VIP"+order.getMonth()+"个月";
		}else if("3".equals(order.getOrderType())){
			body = "开通SVIP"+order.getMonth()+"个月";
		}else if("4".equals(order.getOrderType())){
			body = "刷新活动";
		}else {
			body = "置顶活动";
		}
		body = body + order.getMoney();
		//价格，单位为分
		BigDecimal amount = order.getMoney();
		amount = amount.multiply(new BigDecimal("100"));
		//订单总金额
		Integer moneys = amount.intValue();
		Map<String, String> paramsMap = new HashMap<>(10);
		paramsMap.put("appid", WxPayConfig.APPID);
		paramsMap.put("mch_id", WxPayConfig.MCH_ID);
		paramsMap.put("nonce_str", UUIDGenerator.getUUID());
		paramsMap.put("body", body);
		paramsMap.put("out_trade_no", order.getId());
		paramsMap.put("total_fee", moneys.toString());
		paramsMap.put("spbill_create_ip", IpUtils.getIpAddress(request));
		paramsMap.put("notify_url", WxPayConfig.NOTIFY_URL);
		paramsMap.put("trade_type", WxPayConfig.TRADETYPE);
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String paramsString = WxPayUtils.createLinkString(paramsMap);
		//MD5运算生成签名
		String sign = WxPayUtils.sign(paramsString, WxPayConfig.KEY, "utf-8").toUpperCase();
		//签名
		paramsMap.put("sign", sign);
		String paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
		String result = WxPayUtils.httpRequest(WxPayConfig.PAY_URL, "POST", paramsXml);
		Map<String, Object> resultMap = WxPayUtils.Str2Map(result);
		String returnCode = resultMap.get("return_code").toString();
		if(!returnCode.equals("SUCCESS")) {
			log.error(resultMap.get("return_msg").toString());
			return new ErrorBean(10002,"连接超时");
		}
		String resultCode = resultMap.get("result_code").toString();
		if(!resultCode.equals("SUCCESS")) {
			log.error(resultMap.get("err_code_des").toString());
			return new ErrorBean(10002,"连接超时");
		}
		String prepayId = resultMap.get("prepay_id").toString();
		Map<String, String> responseMap = new HashMap<String, String>(7);
		responseMap.put("appid", WxPayConfig.APPID);
		responseMap.put("partnerid", WxPayConfig.MCH_ID);
		responseMap.put("prepayid", prepayId);
		responseMap.put("package", "Sign=WXPay");
		responseMap.put("noncestr", UUIDGenerator.getUUID());
		Long timeStamp = System.currentTimeMillis() / 1000;
		responseMap.put("timestamp", timeStamp + "");
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String responseString = WxPayUtils.createLinkString(responseMap);
		//MD5运算生成签名
		String responseSign = WxPayUtils.sign(responseString, WxPayConfig.KEY, "utf-8").toUpperCase();
		responseMap.put("sign", responseSign);
		return new SuccessBean(responseMap);
	}
	
}
