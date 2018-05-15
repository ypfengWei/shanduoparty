package com.shanduo.party.mapper;

import com.shanduo.party.entity.VipExperience;

public interface VipExperienceMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(VipExperience record);

    int insertSelective(VipExperience record);

    VipExperience selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(VipExperience record);

    int updateByPrimaryKey(VipExperience record);
    
    int updateExperienceByUserId(Integer experience, Integer userId);
    
    VipExperience selectByUserId(Integer userId);
}