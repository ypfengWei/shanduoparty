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
	
	/**
	 * 
	 * @Title: insertSelective
	 * @Description: TODO
	 * @param @param userId
	 * @param @param openId
	 * @param @param unionId
	 * @param @param type
	 * @param @return
	 * @return int
	 * @throws
	 */
	int insertSelective(int userId,String unionId,String type);
	
	/**
	 * 获取绑定的openId
	 * @Title: selectOpenId
	 * @Description: TODO
	 * @param @param userId
	 * @param @param type
	 * @param @return
	 * @return String
	 * @throws
	 */
	String selectOpenId(Integer userId,String type);
	
}
