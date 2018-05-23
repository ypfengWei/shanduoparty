package com.shanduo.party.mapper;

import com.shanduo.party.entity.UserWechat;

public interface UserWechatMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserWechat record);

    int insertSelective(UserWechat record);

    UserWechat selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserWechat record);

    int updateByPrimaryKey(UserWechat record);
    
    Integer selectByUserId(String union_id);
}