package com.shanduo.party.mapper;

import com.shanduo.party.entity.ShanduoReputation;

public interface ShanduoReputationMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(ShanduoReputation record);

    int insertSelective(ShanduoReputation record);

    ShanduoReputation selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(ShanduoReputation record);

    int updateByPrimaryKey(ShanduoReputation record);
    
    /**
     * 根据userid修改信誉分
     * @Title: updateByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param reputation
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserId(Integer userId, Integer reputation);
    
    int selectByUserId(Integer userId);
    
    int selectDeduction(Integer userId);
    
    int updateDeduction(Integer userId, Integer deduction);
    
    int updateReputation(Integer userId, Integer reputation);
    
    int updateReputations();
    
}