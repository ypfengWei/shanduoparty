package com.shanduo.party.mapper;

import com.shanduo.party.entity.UserToken;

public interface UserTokenMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserToken record);

    int insertSelective(UserToken record);

    UserToken selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserToken record);

    int updateByPrimaryKey(UserToken record);
    
    UserToken selectByToken(String token);
    
    UserToken selectByUserId(Integer userId);
    
    int insertOrUpdate(String id,String token,Integer useId);
    
}