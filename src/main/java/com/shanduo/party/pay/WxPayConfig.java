package com.shanduo.party.pay;

/**
 * 微信支付基础配置类
 * @ClassName: WxPayConfig
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月22日 下午2:39:37
 *
 */
public class WxPayConfig {
	
	/**
	 * APPID
	 */
    public static final String APPID = "wx3dd985759741b34e";
    
    /**
     * 商户id
     */
    public static final String MCH_ID = "1504783251";
    
    /**
     * 商户密钥
     */
    public static final String KEY = "shanduo123456TCL987654888fa00888";
    
    /**
     * 签名方式,固定值
     */
    public static final String SIGNTYPE = "MD5";
    
    /**
     * 交易类型
     */
    public static final String TRADETYPE_APP = "APP";
    public static final String TRADETYPE_JSAPI = "JSAPI";
    
    /**
     * 微信统一下单接口地址
     */
    public static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    
    /**
     * 支付回调url
     */
    public static final String NOTIFY_URL_APP = "https://yapinkeji.com/shanduoparty/jpay/appwxpay";
    public static final String NOTIFY_URL_JSAPI = "https://yapinkeji.com/shanduoparty/jpay/jsapiwxpay";
    
    /**
     * 调用微信接口的返回值的false
     */
    public static final String FAIL = "FAIL";
    
    /**
     * 调用微信接口的返回值的ture
     */
    public static final String SUCCESS = "SUCCESS";
    
}
