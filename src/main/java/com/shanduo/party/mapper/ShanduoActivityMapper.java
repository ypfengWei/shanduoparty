 package com.shanduo.party.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ShanduoActivity;
import com.shanduo.party.entity.service.ActivityInfo;

public interface ShanduoActivityMapper {
    int deleteByPrimaryKey(String id);

    int insert(ShanduoActivity record);

    int insertSelective(ShanduoActivity record);

    ShanduoActivity selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ShanduoActivity record);

    int updateByPrimaryKey(ShanduoActivity record);
    
    /**
     * 根据活动Id修改删除标识
     * @Title: deleteByActivity
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int deleteByActivity(String activityId, Integer userId);
    
    /**
     * 热门活动记录数
     * @Title: selectByScoreCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByScoreCount();
    
    /**
     * 查询热门活动
     * @Title: selectByScore
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityInfo>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByScore(Integer page, Integer pageSize);
    
    /**
     * 好友活动记录数
     * @Title: selectByFriendsUserIdCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByFriendsUserIdCount(Integer userId);
    
    /**
     * 好友活动
     * @Title: selectByFriendsUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityInfo>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByFriendsUserId(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 附近活动记录数
     * @Title: selectByNearbyUserIdCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param minlon
     * @param @param maxlon
     * @param @param minlat
     * @param @param maxlat
     * @param @param time
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByNearbyUserIdCount(double lon, double lat);
    
    /**
     * 附近活动
     * @Title: selectByNearbyUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param minlon
     * @param @param maxlon
     * @param @param minlat
     * @param @param maxlat
     * @param @param time
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityInfo>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByNearbyUserId(double lon, double lat, Integer page, Integer pageSize);
    
    /**
     * 用户举办活动记录数
     * @Title: selectByUserIdCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByUserIdCount(Integer userId);
    
    /**
     * 查看用户举办的活动
     * @Title: selectByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityInfo>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByUserId(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 查询活动开始时间大于新创建活动时间的活动信息
     * @Title: selectByActivityUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @return    设定文件
     * @return List<ShanduoActivity>    返回类型
     * @throws
     */
    int selectByActivityUserId(Integer userId, String time);
    
    /**
     * 参加活动时查询活动有无冲突信息
     * @Title: selectByAll
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param mintime
     * @param @param maxtime
     * @param @return    设定文件
     * @return List<ShanduoActivity>    返回类型
     * @throws
     */
    int selectByAll(Integer userId, String mintime, String maxtime);
    
    /**
     * 报名活动记录数
     * @Title: selectByUserIdTimeCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByUserIdTimeCount(Integer userId);
    
    /**
     * 报名活动
     * @Title: selectByUserIdTime
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityInfo>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByUserIdTime(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 参加活动记录数
     * @Title: selectByUserIdInTimeCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByUserIdInTimeCount(Integer userId);
    
    /**
     * 参加活动
     * @Title: selectByUserIdInTime
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityInfo>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByUserIdInTime(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 发起者评价以及用户者评价记录数
     * @Title: selectByHistoricalCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByHistoricalCount(Integer userId);
    
    /**
     * 发起者评价以及用户者评价
     * @Title: selectByHistorical
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityInfo>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByHistorical(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 根据活动Id查询参加活动的用户记录数
     * @Title: selectByUserIdCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByScoreActivityCount(String activityId);
    
    /**
     * 根据活动Id查询参加活动的用户
     * @Title: selectByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityScore>    返回类型
     * @throws
     */
    List<ActivityInfo> selectByScoreActivity(String activityId, Integer page, Integer pageSize);
    
    /**
     * 查询活动下的参加用户
     * @Title: selectByActivityId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return List<Map<String,Object>>    返回类型
     * @throws
     */
    List<Map<String, Object>> selectByActivityId(String activityId, Integer page, Integer pageSize);
    
    /**
     * 活动刷新
     * @Title: activityRefresh
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param time
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
     * @param @param time
     * @param @param activityId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateBysetTop(String activityId);
    
    /**
     * 将置顶时间超过12小时的活动取消置顶
     * @Title: updateById
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param time
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateById(Date time);
    
    /**
     * 根据活动id查userId
     * @Title: selectById
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param ativityId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectById(String ativityId);
    
    /**
     * 查看单个活动详情
     * @Title: selectByActivityIds
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return ActivityInfo    返回类型
     * @throws
     */
    ActivityInfo selectByActivityIds(String activityId);
    
    /**
     * 根据活动id查询参与人
     * @Title: selectActivityIds
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return List<Map<String,Object>>    返回类型
     * @throws
     */
    List<Map<String, Object>> selectActivityIds(String activityId);
    
    Map<String, Object> numberAndScore(String activityId);
    
    List<ActivityInfo> selectQuery(String query, Integer page, Integer pageSize);
    
    int queryCount(String query);
    
    List<Map<String, Object>> selectId(Integer userId);
    
    Map<String, Object> numberScore(String activityId);
    
    List<Map<String, Object>> selectScore(String activityId);
    
    int updateDownFlag(String activityId);
    
    int selectUserId(String activityId);
    
    ShanduoActivity selectByIds(String activityId);
    
    int selectRecord(Integer userId);
    
    ShanduoActivity selectActivityName(String id);
    
    Map<String, Object> selectUserName(Integer userId);
    
}