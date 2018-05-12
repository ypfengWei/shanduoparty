package com.shanduo.party.mapper;

import com.shanduo.party.entity.DynamicPraise;

public interface DynamicPraiseMapper {
    int deleteByPrimaryKey(String id);

    int insert(DynamicPraise record);

    int insertSelective(DynamicPraise record);

    DynamicPraise selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DynamicPraise record);

    int updateByPrimaryKey(DynamicPraise record);
    
    DynamicPraise selectByUserId(Integer userId,String dynamicId);
    
    int deletePraise(Integer userId,String dynamicId);
    
    int selectByCount(String dynamicId);
}