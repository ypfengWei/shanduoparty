package com.shanduo.party.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.shanduo.party.entity.common.ResultBean;
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
	
	@RequestMapping(value = "zfbpay",produces = "text/html;charset=UTF-8",method={RequestMethod.POST})
	@ResponseBody
	public String pay(HttpServletRequest request) throws AlipayApiException {
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map<String, Object> requestParams = request.getParameterMap();
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
		//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
		//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
		boolean flag = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET,"RSA2");
		return null;
		
//		Map requestParams = request.getParameterMap();

//        JSONObject json = JSONObject.fromObject(requestParams);
//
//        String trade_status = requestParams.get("trade_status").toString().substring(2,requestParams.get("trade_status").toString().length()-2);
//        String out_trade_no = requestParams.get("out_trade_no").toString().substring(2,requestParams.get("out_trade_no").toString().length()-2);
//        String notify_id = requestParams.get("notify_id").toString().substring(2,requestParams.get("notify_id").toString().length()-2);
//
//        System.out.println("====================================================");
//        System.out.println(requestParams.toString());
//        System.out.println("支付宝回调地址！");
//        System.out.println("商户的订单编号：" + out_trade_no);
//        System.out.println("支付的状态：" + trade_status);    
//
//        if(trade_status.equals("TRADE_SUCCESS")) {
//
//                /**
//                 *支付成功之后的业务处理
//                 */
//
//                return "SUCCESS";
//        }else {
//
//            /**
//             *支付失败后的业务处理
//             */
//
//            return "SUCCESS";
//        }
//        return "SUCCESS";
	}
	
}
