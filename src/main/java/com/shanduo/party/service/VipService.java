package com.shanduo.party.service;

import java.util.List;

import com.shanduo.party.entity.ShanduoVip;

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
	int insertSelective(Integer userId, String vipType, String vipStartTime);
	
	/**
	 * 根据userid查询vip信息
	 * @Title: selectByUserIdAndTime
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @return    设定文件
	 * @return List<ShanduoVip>    返回类型
	 * @throws
	 */
	List<ShanduoVip> selectByUserIdAndTime(Integer userId);
	 
	/**
	 * 
	 * @Title: updateByUserId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param month
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateByUserId(Integer userId, Integer month);
}
