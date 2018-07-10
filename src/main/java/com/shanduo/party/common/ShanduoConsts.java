package com.shanduo.party.common;


public class ShanduoConsts {


	public static final String ROLE_MERCHANT = "1";//商家

	public static final String ROLE_USER = "2";//用户

	public static final String ROLE_ADMIN = "3";//管理员

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

	/**
	 * 秒，分钟，小时，一天，一周
	 */
	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long WEEK = 7 * DAY;
	
}