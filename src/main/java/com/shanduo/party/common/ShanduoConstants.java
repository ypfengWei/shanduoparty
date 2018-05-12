package com.shanduo.party.common;


public class ShanduoConstants {


	public static final String ROLE_SELLER = "11";// 店家

	public static final String ROLE_SELLER_SERVICE = "22";//用户

	public static final String USER_SUPNO = "33";//VIP
	
	public static final String USER_SUPNO_SERVICE  = "44";//超级VIP

	/**
	 * 商家
	 */
	public static final int IS_SELLER = 1;
	/**
	 * 不是商家
	 */
	public static final int NOIS_SELLER = 0;

	public static final int pageNumber = 20;

	public static final String POWERED_INSERT = "添加成功";

	public static final String POWERED_DELETE = "删除成功";

	public static final String POWERED_UPDATE = "修改成功";

	public static final String CATEGORY_PRAENTSS = "1";// 一级分类
	public static final String CATEGORY_PRAENTS = "2";// 二级分类
	public static final String CATEGORY_PRAENT = "3";// 三级分类

	public static final String MASTER_KEY = "xx01";// 登陆验证码万能钥匙
	public static final int SYSTEM = 17;
	public static final int ROSTER = 18;
	public static final int OFFLINE = 19;
	public static final int MUC_ROOM = 23;
	public static final int SECURITY_AUDIT = 25;
	public static final int MUC_SERVICE = 26;
	public static final int USER_ID = 101;
	public static final int UNIT_NO = 102;
	public static final int CLASS_NO = 103;

	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long WEEK = 7 * DAY;
	 
}