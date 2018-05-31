package com.shanduo.party.mapper;

import com.shanduo.party.entity.UserBinding;

public interface UserBindingMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserBinding record);

    int insertSelective(UserBinding record);

    UserBinding selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserBinding record);

    int updateByPrimaryKey(UserBinding record);
    
    Integer selectUserId(String union_id,String type);
    
    String selectOpenId(Integer userId,String type);
}