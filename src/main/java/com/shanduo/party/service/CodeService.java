package com.shanduo.party.service;

/**
 * 验证码业务层
 * @ClassName: CodeService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月14日 上午9:47:57
 *
 */
public interface CodeService {

	/**
	 * 手机验证码录入
	 * @Title: insertSelective
	 * @Description: TODO
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @param typeId 验证码类型
	 * @param @return
	 * @return int
	 * @throws
	 */
	int savePhoneVerifyCode(String phone,String code,String typeId);
	
	/**
	 * 检查验证码是否超时或错误
	 * @Title: selectByQuery
	 * @Description: TODO
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @param typeId 验证码类型
	 * @param @return
	 * @return PhoneVerifyCode
	 * @throws
	 */
	boolean selectByQuery(String phone,String code,String typeId);
	
	/**
	 * 检查是否多次请求发送短信
	 * @Title: selectByPhone
	 * @Description: TODO
	 * @param @param phone 手机号
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean selectByPhone(String phone);
	
	/**
	 * 删除过期的验证码记录
	 * @Title: deleteTimer
	 * @Description: TODO
	 * @param @param createDate 创建时间
	 * @param @return
	 * @return int
	 * @throws
	 */
	int deleteTimer(String createDate);
}
