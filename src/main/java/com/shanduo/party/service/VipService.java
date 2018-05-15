package com.shanduo.party.service;


public interface VipService {
	
	/**
	 * 添加会员
	 * @Title: insertSelective
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param vipType
	 * @param @param vipStartTime
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int insertSelective(Integer userId, String vipType, Integer month);
	
	/**
	 * 续费会员
	 * @Title: updateByUserId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param month
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateByUserId(Integer userId, Integer month, String vipType);
	
}
