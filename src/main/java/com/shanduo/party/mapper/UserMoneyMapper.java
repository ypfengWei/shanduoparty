package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.UserMoney;

public interface UserMoneyMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserMoney record);

    int insertSelective(UserMoney record);

    UserMoney selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserMoney record);

    int updateByPrimaryKey(UserMoney record);
    
    UserMoney selectByUserId(Integer userId);
    
    List<UserMoney> beansList();
    
}