package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

/**
 * 举报业务层
 * @ClassName: ReportService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月23日 下午4:47:20
 */
public interface ReportService {
	
	/**
	 * 举报成功后修改信誉分以及扣分记录
	 * @Title: updateReputation
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param activityId
	 * @param @param type
	 * @param @param dynamicId
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int updateReputation(String activityId, String type, String dynamicId);
	
	/**
	 * 举报
	 * @Title: report
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param activityId
	 * @param @param dynamicId
	 * @param @param typeId
	 * @param @param remarks
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int report(Integer userId, String activityId, String dynamicId, String typeId, String remarks);
	
	/**
	 * 根据活动id或者动态id以及用户id查询举报信息
	 * @Title: selectId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param activityId
	 * @param @param dynamicId
	 * @param @param typeId
	 * @param @param userId
	 * @param @return    设定文件
	 * @return String    返回类型
	 * @throws
	 */
	String selectId(String activityId, String dynamicId, String typeId, Integer userId);
	
	/**
	 * 举报记录
	 * @Title: reportRecord
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param type
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return Map<String,Object>    返回类型
	 * @throws
	 */
	List<Map<String, Object>> selectInfo(String activityId, String dynamicId);
	
	List<Map<String, Object>> reportRecord(String type, Integer pageNum, Integer pageSize);
}
