package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shanduo.party.entity.ActivityScore;

public interface ActivityScoreMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActivityScore record);

    int insertSelective(ActivityScore record);

    ActivityScore selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActivityScore record);

    int updateByPrimaryKey(ActivityScore record);
    
    /**
     * 根据userId查询记录
     * @Title: selectByIdScoreCount
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int selectByIdScoreCount(Integer userId);
    
    /**
     * 根据用户发起的所有活动查询此用户活动下的评论信息
     * @Title: selectByIdScore
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param page
     * @param @param pageSize
     * @param @return    设定文件
     * @return List<ActivityScore>    返回类型
     * @throws
     */
    List<ActivityScore> selectByIdScore(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 参与者默认好评
     * @Title: updateById
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityStartTime
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateById(String activityStartTime);
    
    /**
     * 发起者默认好评
     * @Title: updateByIdTime
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityStartTime
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByIdTime(String activityStartTime);
    
    /**
     * 活动发起人对活动参与人进行评价
     * @Title: updateByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param activityId
     * @param @param othersScore
     * @param @param beEvaluated
     * @param @param remarks
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserId(Integer userId, String activityId, Integer othersScore, String beEvaluated);
    
    /**
     * 参与者对活动发起人进行评价
     * @Title: updateByUserIdTwo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param activityId
     * @param @param score
     * @param @param evaluated
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserIdTwo(Integer userId, String activityId, Integer score, String evaluated);
    
    int deleteByUserId(String activityId, Integer userId);
    
    int deleteByUserIds(String activityId, @Param("list") List<Integer> list);
    
    int deleteByActivityId(String activityId);
    
    List<Map<String, Object>> selectByGender(String activityId);
    
    String selectByUserId(Integer userId);
    
    int selectByGenders(String activityId, String gender);
    
    Map<String, Object> selectReputation(Integer userId);
    
    List<Map<String, Object>> selectActivity(Integer userId);
    
    List<Map<String, Object>> selectScore(String activityId);
}