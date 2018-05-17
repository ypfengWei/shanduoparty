package com.shanduo.party.mapper;

import com.shanduo.party.entity.UserOrder;

public interface UserOrderMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);
    
    UserOrder selectById(String id,Integer userId);
}