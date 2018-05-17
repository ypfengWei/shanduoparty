package com.shanduo.party.mapper;

import java.util.List;

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
    int selectByUserId(Integer userId, String startTime, String reputationType);
    
    List<ShanduoReputationRecord> selectByTime(String startTime);
    
    int selectByMany(Integer userId, Integer otheruserId, String reputationType, String startTime);
}