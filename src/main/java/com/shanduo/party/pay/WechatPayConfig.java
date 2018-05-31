package com.shanduo.party.pay;

public class WechatPayConfig {
	
	/**
	 * 小程序ID
	 */
    public static final String APPID = "wxf0a4b4f4e27b7aa6";
    
    /**
     * 商户ID
     */
    public static final String MCH_ID = "1504783251";
    
    /**
     * 签名方式,固定值
     */
    public static final String SIGNTYPE = "MD5";
    
    /**
     * 交易类型
     */
    public static final String TRADETYPE = "JSAPI";
    
    /**
     * 微信统一下单接口地址
     */
    public static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    
    
    /**
     * 支付回调url
     */
    public static final String NOTIFY_URL = "https://yapinkeji.com/shanduoparty/jpay/jsapiwxpay";
    
    /**
     * 调用微信接口的返回值的false
     */
    public static final String FAIL = "FAIL";
    
    /**
     * 调用微信接口的返回值的ture
     */
    public static final String SUCCESS = "SUCCESS";
    
}	
