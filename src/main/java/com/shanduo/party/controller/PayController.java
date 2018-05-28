package com.shanduo.party.controller;

import java.io.BufferedReader;
import java.io.IOException;
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
import com.alipay.api.internal.util.AlipaySignature;
import com.shanduo.party.entity.UserOrder;
import com.shanduo.party.pay.AliPayConfig;
import com.shanduo.party.pay.WxPayConfig;
import com.shanduo.party.service.OrderService;
import com.shanduo.party.util.WxPayUtils;

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
	@RequestMapping(value = "alipay",produces="text/html;charset=UTF-8",method={RequestMethod.POST})
	@ResponseBody
	public String aliPay(HttpServletRequest request) throws AlipayApiException {
		//获取支付宝POST过来反馈信息
		Map requestParams = request.getParameterMap();
		Map<String,String> params = new HashMap<String,String>(requestParams.size());
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
		boolean flag = AlipaySignature.rsaCheckV1(params, AliPayConfig.ALIPAY_PUBLIC_KEY, AliPayConfig.CHARSET,AliPayConfig.SIGNTYPE);
		if(flag) {
			//订单支付状态
			String tradeStatus = params.get("trade_status");
			if(!tradeStatus.equals("TRADE_SUCCESS")) {
				return "SUCCESS";
			}
			//订单号
			String orderId = params.get("out_trade_no");
			UserOrder order = orderService.selectByOrderId(orderId);
			if(order == null) {
				log.error("订单已操作或不存在");
				return "SUCCESS";
			}
			//应用ID
			String appId = params.get("app_id");
			if(!AliPayConfig.APPID.equals(appId)) {
				log.error("应用ID不匹配:"+appId);
				return "SUCCESS";
			}
			//商家ID
			String sellerId = params.get("seller_id");
			if(!AliPayConfig.SELLERID.equals(sellerId)) {
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
	
	/**
	 * 微信支付回调
	 * @Title: appWxPay
	 * @Description: TODO
	 * @param @param request
	 * @param @return
	 * @param @throws IOException
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "appwxpay")
	@ResponseBody
	public String appWxPay(HttpServletRequest request) throws IOException {
		BufferedReader reader = request.getReader();
        String line = "";
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            inputString.append(line);
        }
        String xmlString  = inputString.toString();
        request.getReader().close();
        log.info("微信支付回调接口返回XML数据:" + xmlString);
        Map<String, Object> resultMap = WxPayUtils.Str2Map(xmlString);
        //验证签名是否微信调用
        boolean flag = WxPayUtils.isWechatSigns(resultMap, WxPayConfig.KEY, "utf-8");
        if(flag) {
        	String returnCode = resultMap.get("return_code").toString();
    		if(!returnCode.equals("SUCCESS")) {
    			log.error(resultMap.get("return_msg").toString());
    			return returnXML(WxPayConfig.FAIL);
    		}
    		String resultCode = resultMap.get("result_code").toString();
    		if(!resultCode.equals("SUCCESS")) {
    			log.error(resultMap.get("err_code_des").toString());
    			return returnXML(WxPayConfig.FAIL);
    		}
    		String appid = resultMap.get("appid").toString();
    		if(!appid.equals(WxPayConfig.APPID)) {
    			log.error("APPID不匹配");
    			return returnXML(WxPayConfig.FAIL);
    		}
    		String mchId = resultMap.get("mch_id").toString();
    		if(!mchId.equals(WxPayConfig.MCH_ID)) {
    			log.error("商户号不匹配");
    			return returnXML(WxPayConfig.FAIL);
    		}
    		String orderId = resultMap.get("out_trade_no").toString();
    		UserOrder order = orderService.selectByOrderId(orderId);
			if(order == null) {
				log.error("订单已操作或不存在");
				return returnXML(WxPayConfig.FAIL);
			}
    		String totalFee = resultMap.get("total_fee").toString();
    		//价格，单位为分
    		BigDecimal amount = order.getMoney();
    		amount = amount.multiply(new BigDecimal("100"));
    		if(amount.compareTo(new BigDecimal(totalFee)) != 0) {
    			log.error("订单金额错误:"+totalFee+","+order.getMoney());
    			return returnXML(WxPayConfig.FAIL);
    		}
    		try {
				orderService.wxUpdateOrder(orderId);
			} catch (Exception e) {
				log.error("修改订单错误");
				return returnXML(WxPayConfig.FAIL);
			}
    		return returnXML(WxPayConfig.SUCCESS);
        }
    	return returnXML(WxPayConfig.FAIL);
	}
	
	/**
	 * 微信回调返回XML
	 * @param returnCode
	 * @return
	 */
	private String returnXML(String returnCode) {
        return "<xml><return_code><![CDATA["
                + returnCode
                + "]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
}
