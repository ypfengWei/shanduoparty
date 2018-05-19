package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.UserAttention;

public interface UserAttentionMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserAttention record);

    int insertSelective(UserAttention record);

    UserAttention selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserAttention record);

    int updateByPrimaryKey(UserAttention record);
    
    UserAttention checkAttention(Integer userId, Integer attention,String typeId);
    
    int selectAttentionCount(Integer userId,String typeId);
    
    List<Map<String, Object>> selectAttentionList(Integer userId,String typeId);
    
    int deleteAttention(Integer userId,Integer attention, String typeId);
}