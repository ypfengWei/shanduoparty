package com.shanduo.party.common;

/**
 * Android页面路径
 * @ClassName: ActivityConfig
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月21日 上午10:33:07
 *
 */
public class ActivityConsts {

	public static final String ACTIVITY = "com.yapin.shanduo.ui.activity.";
	//vip
	public static final String VIP = ACTIVITY+"MembercenterActivity";
	//钱包
	public static final String MONEY = ACTIVITY+"MywalletActivity";
	//我的消息
	public static final String MESSAGE = ACTIVITY+"MyMessageActivity";
	//跳转到安卓
	public static final String INTENT_URI = "xgscheme://com.xg.push";
	//安卓活动详情页
	public static final String ACT_DETAIL = INTENT_URI+"/notify_act_detail";
	//安卓我报名的活动页面
	public static final String USER_ACT = INTENT_URI+"/notify_user_act";
}
