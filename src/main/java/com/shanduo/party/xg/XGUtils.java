package com.shanduo.party.xg;


import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.xinge.XingeApp;

/**
 * 腾讯信鸽普通推送工具类
 * @ClassName: XgUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月7日 下午2:32:23
 * 
 */
public class XGUtils {

	private static final long ACCESS_ID = 2100283798;
	private static final String SECRET_KEY = "55cb37c288b6e1abea3de1efcc166a1a";
	
	/**
	 * 2.1 Android平台推送消息给单个设备
	 * @Title: pushTokenAndroid
	 * @Description: TODO
	 * @param @param title
	 * @param @param content
	 * @param @param token
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushTokenAndroid(String title,String content,String token) throws JSONException {
		JSONObject resultJson = XingeApp.pushTokenAndroid(ACCESS_ID, SECRET_KEY, title, content, token);
		return isError(resultJson);
	}
	
	/**
	 * 2.2 Android平台推送消息给单个账号
	 * @Title: pushAccountAndroid
	 * @Description: TODO
	 * @param @param title
	 * @param @param content
	 * @param @param account
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushAccountAndroid(String title,String content,String account) throws JSONException {
		JSONObject resultJson =  XingeApp.pushAccountAndroid(ACCESS_ID, SECRET_KEY, title, content, account);
		return isError(resultJson);
	}
	
	/**
	 * 2.3 Android平台推送消息给所有设备
	 * @Title: pushAllAndroid
	 * @Description: TODO
	 * @param @param title
	 * @param @param content
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushAllAndroid(String  title,String content) throws JSONException {
		JSONObject resultJson = XingeApp.pushAllAndroid(ACCESS_ID, SECRET_KEY, title, content);
		return isError(resultJson);
	}
	
	/**
	 * 2.4 Android平台推送消息给标签选中设备
	 * @Title: pushTagAndroid
	 * @Description: TODO
	 * @param @param title
	 * @param @param content
	 * @param @param tag
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushTagAndroid(String  title,String content,String tag) throws JSONException {
		JSONObject resultJson = XingeApp.pushTagAndroid(ACCESS_ID, SECRET_KEY, title, content, tag);
		return isError(resultJson);
	}
	
	/**
	 * 2.5 IOS平台推送消息给单个设备
	 * @Title: pushTokenIos
	 * @Description: TODO
	 * @param @param content
	 * @param @param token
	 * @param @param environment
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushTokenIos(String content,String token,int environment) throws JSONException {
		JSONObject resultJson = XingeApp.pushAccountIos(ACCESS_ID, SECRET_KEY, content, token, environment);
		return isError(resultJson);
	}
	
	/**
	 * 2.6 IOS平台推送消息给单个账号
	 * @Title: pushAccountIos
	 * @Description: TODO
	 * @param @param content
	 * @param @param account
	 * @param @param environment
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushAccountIos(String content,String account,int environment) throws JSONException {
		JSONObject resultJson = XingeApp.pushAccountIos(ACCESS_ID, SECRET_KEY, content, account, environment);
		return isError(resultJson);
	}
	
	/**
	 * 2.7 IOS平台推送消息给所有设备
	 * @Title: pushAllIos
	 * @Description: TODO
	 * @param @param content
	 * @param @param environment
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushAllIos(String  content,int environment) throws JSONException {
		JSONObject resultJson = XingeApp.pushAllIos(ACCESS_ID, SECRET_KEY, content, environment);
		return isError(resultJson);
	}
	
	/**
	 * 2.8 IOS平台推送消息给标签选中设备
	 * @Title: pushTagIos
	 * @Description: TODO
	 * @param @param content
	 * @param @param tag
	 * @param @param environment
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String pushTagIos(String content,String tag,int environment) throws JSONException {
		JSONObject resultJson = XingeApp.pushTagIos(ACCESS_ID, SECRET_KEY, content, tag, environment);
		return isError(resultJson);
	}
	
	/**
	 * 判断返回
	 * @Title: getError
	 * @Description: TODO
	 * @param @param resultJson
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public static String isError(JSONObject resultJson) throws JSONException {
  	  	if(resultJson.getInt("ret_code") != 0) {
  	  		return "ok";
  	  	}
		return resultJson.getString("err_msg").toString();
	}
}
