package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ActivityRequirement;
import com.shanduo.party.entity.ShanduoActivity;
import com.shanduo.party.entity.ShanduoUser;

/**
 * 活动业务层
 * @ClassName: ActivityService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年4月16日   下午14:48:50
 *
 */
public interface ActivityService {
	
	/**
	 * 参加活动时查询活动有无冲突信息
	 * @Title: selectByAll
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @return    设定文件
	 * @return List<ShanduoActivity>    返回类型
	 * @throws
	 */
	boolean selectByAll(Integer userId, String activityId);
	
	/**
	 * 发布活动时查询活动有无冲突信息
	 * @Title: selectByTwoAll
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param time
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 * @throws
	 */
	boolean selectByTwoAll(Integer userId, String time);
	
	/**
	 * 添加活动
	 * @Title: saveActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param activityName
	 * @param @param activityType
	 * @param @param activityStartTime
	 * @param @param activityAddress
	 * @param @param mode
	 * @param @param manNumber
	 * @param @param womanNumber
	 * @param @param remarks
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int saveActivity(Integer userId, String activityName, String activityStartTime, String activityAddress,String mode, String manNumber,
			String womanNumber, String remarks, String activityCutoffTime, String lon, String lat, String detailedAddress);
	
	/**
	 * 删除活动信息
	 * @Title: deleteActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param id
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int deleteActivity(String id);

	/**
	 * 展示热点活动以及活动要求信息
	 * @Title: selectByScore
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param page
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return Map<String,Object>    返回类型
	 * @throws
	 */
	Map<String, Object> selectByScore(Integer page, Integer pageSize, String lon,String lat);

	/**
	 * 根据好友userId来查询好友活动
	 * @Title: selectByFriendsUserId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param page
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return Map<String,Object>    返回类型
	 * @throws
	 */
	Map<String, Object> selectByFriendsUserId(Integer userId, Integer page, Integer pageSize, String lon,String lat);
	
	/**
	 * 根据经纬度来查询附近十公里以内的活动
	 * @Title: selectByNearbyUserId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param time
	 * @param @param userId
	 * @param @return    设定文件
	 * @param i 
	 * @return List<ShanduoActivity>    返回类型
	 * @throws
	 */
	Map<String, Object> selectByNearbyUserId(String lon,String lat, Integer page, Integer pageSize); 
	
	/**
	 * 查看用户举办的活动
	 * @Title: selectByUserId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param time
	 * @param @param userId
	 * @param @return    设定文件
	 * @return List<ShanduoActivity>    返回类型
	 * @throws
	 */
	Map<String, Object> selectByUserId(Integer userId, Integer pageNum, Integer pageSize, String lon,String lat);
	
	/**
	 * 报名活动
	 * @Title: selectByUserIdTime
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param time
	 * @param @return    设定文件
	 * @return List<ShanduoActivity>    返回类型
	 * @throws
	 */
	Map<String, Object> selectByUserIdTime(Integer userId, Integer pageNum, Integer pageSize, String lon,String lat);
	
	 /**
	  * 参加活动
	  * @Title: selectByUserIdInTime
	  * @Description: TODO(这里用一句话描述这个方法的作用)
	  * @param @param userId
	  * @param @return    设定文件
	  * @return List<ShanduoActivity>    返回类型
	  * @throws
	  */
	Map<String, Object> selectByUserIdInTime(Integer userId, Integer pageNum, Integer pageSize, String lon,String lat);
	 
	/**
	 * 参加一个活动的男女人数
	 * @Title: selectByGender
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param activityId
	 * @param @return    设定文件
	 * @return ShanduoUser    返回类型
	 * @throws
	 */
	List<Map<String, Object>> selectByGender(String activityId);
    
	/**
	 * 根据id来查询用户性别
	 * @Title: selectById
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param id
	 * @param @return    设定文件
	 * @return ShanduoUser    返回类型
	 * @throws
	 */
    ShanduoUser selectById(Integer id);
    
    /**
     * 参加活动
     * @Title: insertSelective
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param activityId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int insertSelective(Integer userId,String activityId);
    
    /**
     * 根据活动id来查询要求表中的男女人数
     * @Title: selectByNumber
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return ActivityRequirement    返回类型
     * @throws
     */
    ActivityRequirement selectByNumber(String activityId);
    
    /**
     * 根据id查询信息
     * @Title: selectByPrimaryKey
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param id
     * @param @return    设定文件
     * @return ShanduoActivity    返回类型
     * @throws
     */
    ShanduoActivity selectByPrimaryKey(String id);
    
    /**
	 * 发起者评价以及用户者评价
	 * @Title: selectByHistorical
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @param page
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return List<ActivityInfo>    返回类型
	 * @throws
	 */
	Map<String, Object> selectByHistorical(Integer userId, Integer page, Integer pageSize);
	
	/**
	 * 根据活动Id查询参加活动的用户
	 * @Title: selectByScoreActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param activityId
	 * @param @param page
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return Map<String,Object>    返回类型
	 * @throws
	 */
	Map<String, Object> selectByScoreActivity(String activityId, Integer page, Integer pageSize);
	
	/**
     * 查询活动下的参加用户
     * @Title: selectByActivityId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return List<Map<String,Object>>    返回类型
     * @throws
     */
    List<Map<String, Object>> selectByActivityId(String activityId, Integer page, Integer pageSize, Integer userId);

    /**
     * 活动刷新
     * @Title: activityRefresh
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int activityRefresh(String activityId);
    
    /**
     * 活动置顶
     * @Title: updateBysetTop
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateBysetTop(String activityId);
    
    //取消参加活动
    int deleteByUserId(String activityId, Integer userId);
}
