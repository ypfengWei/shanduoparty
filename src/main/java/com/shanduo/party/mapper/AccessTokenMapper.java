package com.shanduo.party.mapper;

import com.shanduo.party.entity.AccessToken;

public interface AccessTokenMapper {
    int deleteByPrimaryKey(String id);

    int insert(AccessToken record);

    int insertSelective(AccessToken record);

    AccessToken selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AccessToken record);

    int updateByPrimaryKey(AccessToken record);
    
    AccessToken selectByAppId(String appId);
}