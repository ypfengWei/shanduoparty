package com.shanduo.party.pay;


/**
 * 支付宝支付基础配置类
 * @ClassName: AlipayConfig
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月19日 上午10:59:35
 *
 */
public class AlipayConfig {

	// 1.商户appid
	public static String APPID = "2018011901973829";    
	
	// 2.私钥 pkcs8格式的
	public static String RSA_PRIVATE_KEY ="";
	
	// 3.支付宝公钥
	public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq8/rpQ0b5mkUymGhC3aawjRBrtutHwWAzfB9Xda+lVHwRHjLEhamKjKkB6MFNfOMD5zkQv2Xzy0IYMJFpzvXJECTqFteaeJzBf4Dk8UpksH0JxgwcTWzAe7i+wxqV5+hE+vQ7oBk0bk2Hqjq61qlzpPX6aiC7nSmOy8lUGmm9RtPgx4wcLxD4Xl2k5rfivN9+WJuEMJlArEG4JOTvr3Zi1Ne1FjxWU2cG3Z8NZ/0h72esQ8TYVgVSPlGWMsmU04uv3SivmWxoXaBkfXH+EelQAbKSksPD70oh/UnOq/8Umvmlx+ZbiJ9CDEgibxxuD/o2jzcydxA5YQnWiSyOOlwZwIDAQAB";
	
	// 4.服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "https://www.xxx.com/alipay/notify_url.do";
	
	// 5.页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
	public static String return_url = "http://www.xxx.com/alipay/return_url.do";
	
	// 6.请求网关地址
	public static String URL = "https://openapi.alipay.com/gateway.do";    
	
	// 7.编码
	public static String CHARSET = "UTF-8";
	
	// 8.返回格式
	public static String FORMAT = "json";
	
	// 9.加密类型
	public static String SIGNTYPE = "RSA2";
	
}
