package com.shanduo.party.service;

import java.util.Map;

/**
 * 信誉业务层
 * @ClassName: ReputationService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月23日 下午4:45:54
 */
public interface ReputationService {
	
	/**
	 * 修改信誉
	 * @Title: updateByReputation
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateByReputation();
	
	/**
	 * 添加信誉记录
	 * @Title: getRecord
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param activityId
	 * @param @param score
	 * @param @param evaluation
	 * @param @param type
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 * @throws
	 */
	boolean getRecord(Integer userId, String activityId, Integer score, String evaluation, Integer type);
	
	/**
	 * 查询信誉分和用户发布的活动及评价
	 * @Title: selectReputation
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userToken
	 * @param @param userId
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return Map<String,Object>    返回类型
	 * @throws
	 */
	Map<String, Object> selectReputation(Integer userToken,Integer userId, Integer pageNum, Integer pageSize);
	
	/**
	 * 查询信誉分和用户参与的活动及评价
	 * @Title: selectJoinActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return Map<String,Object>    返回类型
	 * @throws
	 */
	Map<String, Object> selectJoinActivity(Integer userId, Integer pageNum, Integer pageSize);
	
	/**
	 * 在一次活动中差评所占百分比大于50%的扣一分信誉分
	 * @Title: updateReport
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateReport(Integer userId);
}
