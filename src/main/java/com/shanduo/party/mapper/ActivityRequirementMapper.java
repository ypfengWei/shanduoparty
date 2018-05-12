package com.shanduo.party.mapper;

import com.shanduo.party.entity.ActivityRequirement;

public interface ActivityRequirementMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActivityRequirement record);

    int insertSelective(ActivityRequirement record);

    ActivityRequirement selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActivityRequirement record);

    int updateByPrimaryKey(ActivityRequirement record);
    
    int updateByActivityIdSelective(ActivityRequirement record);
    
    int deleteByActivityId(String activityId);
    
    /**
     * 根据活动id查询要求信息
     * @Title: selectByNumber
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param activityId
     * @param @return    设定文件
     * @return ActivityRequirement    返回类型
     * @throws
     */
    ActivityRequirement selectByNumber(String activityId);
}