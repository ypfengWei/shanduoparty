package com.shanduo.party.service;

import com.shanduo.party.entity.UserToken;

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
	 * @return UserToken
	 * @throws
	 */
	UserToken checkUserToken(String token);
	
}
