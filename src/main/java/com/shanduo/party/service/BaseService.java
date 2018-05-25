package com.shanduo.party.service;


/**
 * 检查登录状态和权限业务层
 * @ClassName: BaseService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月19日 上午10:51:38
 *
 */
public interface BaseService {

	/**
	 * 检查token
	 * @Title: checkUserToken
	 * @Description: TODO
	 * @param @param token
	 * @param @return
	 * @return Integer
	 * @throws
	 */
	Integer checkUserToken(String token);
	
	/**
	 * 检查权限(管理员,商家)
	 * @Title: checkUserRole
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param role 权限ID
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkUserRole(Integer userId,String role);
	
	/**
	 * 检查VIP权限
	 * @Title: checkUserVip
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param vip 权限VIP等级
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkUserVip(Integer userId,Integer vip);
}
