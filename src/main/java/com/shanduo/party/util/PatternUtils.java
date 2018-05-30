package com.shanduo.party.util;

/**
 * 正则验证手机邮箱号工具类
 * @ClassName: PatternUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fanshixin
 * @date 2018年4月14日 上午11:37:59
 *
 */
public class PatternUtils {

	/**
	 * 正则效验手机号
	 * @Title: patternPhone
	 * @Description: TODO
	 * @param @param phone
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean patternPhone(String phone) {
		String regEx = "^(13[0-9]|14[0-9]|15([0-9])|166|17[0-9]|18[0-9]|19[89])\\d{8}$";
		return !phone.matches(regEx);
	}
	
	/**
	 * 正则效验验证码
	 * @Title: patternCode
	 * @Description: TODO
	 * @param @param code
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean patternCode(String code) {
		String regEx = "^\\d{6}$";
		return !code.matches(regEx);
	}
	
	/**
	 * 正则效验密码
	 * @Title: patternPassword
	 * @Description: TODO
	 * @param @param password
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean patternPassword(String password) {
		String regEx = "^[0-9A-Za-z!@#$%^&*()-_+=`~:;'|\\\\\\\\/?\\\\.,<>]{8,16}$";
		return !password.matches(regEx);
	}
	
	/**
	 * 正则效验账号
	 * @Title: patternUser
	 * @Description: TODO
	 * @param @param user
	 * @param @return
	 * @return Boolean
	 * @throws
	 */
	public static Boolean patternUser(String user) {
		String regEx = "^[1-9]\\d{4,10}$";
		return !user.matches(regEx);
	}
	
	/**
	 * 正则效验生日日期(yyyy-MM-dd)
	 * @Title: patternBirthday
	 * @Description: TODO
	 * @param @param birthday
	 * @param @return
	 * @return Boolean
	 * @throws
	 */
	public static Boolean patternBirthday(String birthday) {
		String regEx = "^(19|20)\\d{2}-([1-9]|0[1-9]|1[012])-(\\d|[0-2]\\d|3[01])$";
		return !birthday.matches(regEx);
	}
	
	/**
	 * 正则效验经纬度
	 * @Title: patternLatitude
	 * @Description: TODO
	 * @param @param latitude
	 * @param @return
	 * @return Boolean
	 * @throws
	 */
	public static Boolean patternLatitude(String latitude) {
		String regEx = "^\\d{1,10}(\\.\\d+)?$";
		return !latitude.matches(regEx);
	}
	
	/**
	 * 正则效验文件格式
	 * @Title: patternFile
	 * @Description: TODO
	 * @param @param fileName
	 * @param @return
	 * @return Boolean
	 * @throws
	 */
	public static Boolean patternFile(String fileName) {
		String regEx = "^.+\\.(jpg|JPG|gif|GIF|png|PNG|jpeg|JPEG)$";
		return !fileName.matches(regEx);
	}
	
}
