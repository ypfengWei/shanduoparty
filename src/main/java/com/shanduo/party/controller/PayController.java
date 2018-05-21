package com.shanduo.party.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
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
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.shanduo.party.entity.UserOrder;
import com.shanduo.party.pay.AlipayConfig;
import com.shanduo.party.service.OrderService;

/**
 * 第三方支付控制层
 * @ClassName: PayController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月18日 下午3:43:35
 *
 */
@Controller
@RequestMapping(value = "jpay")
public class PayController {

	private static final Logger log = LoggerFactory.getLogger(PayController.class);
	
	@Autowired
	private OrderService orderService;
	
	public Object set(String orderId) {
		UserOrder order = orderService.selectByOrderId(orderId);
		String body = "";
		String subject = "";
		if("1".equals(order.getOrderType())) {
			body = "充值闪多余额";
			subject = body + order.getMoney();
		}else if("2".equals(order.getOrderType())){
			body = "开通闪多VIP"+order.getMonth()+"个月";
		}else if("3".equals(order.getOrderType())){
			body = "开通闪多SVIP"+order.getMonth()+"个月";
		}else if("4".equals(order.getOrderType())){
			body = "刷新闪多活动"+order.getActivityId();
		}else {
			body = "置顶闪多活动"+order.getActivityId();
		}
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", AlipayConfig.APPID, AlipayConfig.APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
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
		request.setBizModel(model);
		request.setNotifyUrl(AlipayConfig.NOTIFY_URL);
		try {
		        //这里和普通的接口调用不同，使用的是sdkExecute
		        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
		        System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
		    } catch (AlipayApiException e) {
		        e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * 支付宝支付回调
	 * @Title: pay
	 * @Description: TODO
	 * @param @param request
	 * @param @return
	 * @param @throws AlipayApiException
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "zfbpay",produces = "text/html;charset=UTF-8",method={RequestMethod.POST})
	@ResponseBody
	public String pay(HttpServletRequest request) throws AlipayApiException {
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
		    String name = iter.next();
		    String[] values = (String[]) requestParams.get(name);
		    String valueStr = "";
		    for (int i = 0; i < values.length; i++) {
		        valueStr = (i == values.length - 1) ? valueStr + values[i]
		                    : valueStr + values[i] + ",";
		  	}
		    //乱码解决，这段代码在出现乱码时使用。
//			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		log.info(params.toString());
		//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
		//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
		boolean flag = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET,AlipayConfig.SIGNTYPE);
		if(flag) {
			//订单支付状态
			String trade_status = params.get("trade_status");
			//订单号
			String orderId = params.get("out_trade_no");
			if(trade_status.equals("TRADE_SUCCESS")) {
				//
				UserOrder order = orderService.selectByOrderId(orderId);
				if(order == null) {
					log.error("订单已操作或不存在");
					return "SUCCESS";
				}
				//应用ID
				String appId = params.get("app_id");
				if(!AlipayConfig.APPID.equals(appId)) {
					log.error("应用ID不匹配:"+appId);
					return "SUCCESS";
				}
				//商家ID
				String sellerId = params.get("seller_id");
				if(!AlipayConfig.SELLERID.equals(sellerId)) {
					log.error("商家ID不匹配:"+sellerId);
					return "SUCCESS";
				}
				//订单金额
				String money = params.get("total_amount");
				if(order.getMoney().compareTo(new BigDecimal(money)) != 0){
					log.error("订单金额错误:"+money+","+order.getMoney());
					return "SUCCESS";
				}
				try {
					orderService.zfbUpdateOrder(orderId);
				} catch (Exception e) {
					log.error("修改订单错误");
					return "SUCCESS";
				}
			}
			return "SUCCESS";
		}
		return "SUCCESS";
	}
	
}
