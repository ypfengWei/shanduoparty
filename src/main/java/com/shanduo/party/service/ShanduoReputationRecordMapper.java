package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ShanduoReputationRecord;

public interface ShanduoReputationRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(ShanduoReputationRecord record);

    int insertSelective(ShanduoReputationRecord record);

    ShanduoReputationRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ShanduoReputationRecord record);

    int updateByPrimaryKey(ShanduoReputationRecord record);
    
    /**
     * 根据userid查reputationType的信息
     * @Title: selectByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @return    设定文件
     * @return ShanduoReputationRecord    返回类型
     * @throws
     */
    Integer selectByUserId(Integer userId, String startTime, String reputationType);
    
    List<Map<String,Integer>> selectByTime(String startTime);
    
    Integer selectByMany(Integer userId, Integer otherUserId, String reputationType, String startTime);
}