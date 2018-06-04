package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

/**
 * 评价业务层
 * @ClassName: ScoreService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author A18ccms a18ccms_gmail_com
 * @date 2018年5月18日 下午4:20:28
 *
 */
public interface ScoreService {
	
	/**
	 * 添加评价
	 * @Title: saveActivityScore
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param activityid
	 * @param @param score
	 * @param @param evaluationcontent
	 * @param @param remarks
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateActivityScore(Integer userId, String activityid, Integer score, String evaluation);
	
	/**
	 * 活动发起人对活动参与人进行评价
	 * @Title: updateByUserId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param activityId
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateByUserId(String activityId, List<Map<String, Object>> list);
	
	/**
	 * 根据用户发起的所有活动查询此用户活动下的评论信息
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param userId
	 * @return List 
	 * @throws
	 */
	Map<String, Object> selectByIdScore(Integer userId, Integer page, Integer pageSize);
	
	/**
	 * 参与者对活动默认好评
	 * @Title: updateById
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param time
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateById(String time);
	
	/**
	 * 发起者对参与者默认好评
	 * @Title: updateByIdTime
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param time
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateByIdTime(String time);
	
	/**
	 * 修改会员成长值
	 * @Title: updateByReputation
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateByReputation();
	
	Map<String, Object> selectReputation(Integer userToken,Integer userId, Integer pageNum, Integer pageSize);
	
//	Map<String, Object> selectReleaseActivity(Integer userId, Integer pageNum, Integer pageSize);
	
	Map<String, Object> selectJoinActivity(Integer userId, Integer pageNum, Integer pageSize);
	
}