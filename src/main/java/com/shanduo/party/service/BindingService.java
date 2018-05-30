package com.shanduo.party.service;


/**
 * 绑定业务层
 * @ClassName: BindingService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月18日 下午4:20:43
 *
 */
public interface BindingService {
	
	/**
	 * 检查是否绑定
	 * @Title: selectUserId
	 * @Description: TODO
	 * @param @param union_id
	 * @param @return
	 * @return int
	 * @throws
	 */
	Integer selectUserId(String union_id,String type);
	
	int insertSelective(int userId,String openId,String unionId,String type);
	
	
	
}
