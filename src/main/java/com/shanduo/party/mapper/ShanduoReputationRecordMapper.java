package com.shanduo.party.mapper;

import com.shanduo.party.entity.ShanduoReputationRecord;

public interface ShanduoReputationRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(ShanduoReputationRecord record);

    int insertSelective(ShanduoReputationRecord record);

    ShanduoReputationRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ShanduoReputationRecord record);

    int updateByPrimaryKey(ShanduoReputationRecord record);
    
    /**
     * 根据userid查reputationType=1的信息
     * @Title: selectByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @return    设定文件
     * @return ShanduoReputationRecord    返回类型
     * @throws
     */
    int selectByUserId(Integer userId, String startTime, String endTime);
    
    /**
     * 根据userid查reputationType=2的信息
     * @Title: selectByUserIdTwo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @return    设定文件
     * @return ShanduoReputationRecord    返回类型
     * @throws
     */
    int selectByUserIdTwo(Integer userId, String startTime, String endTime);
}