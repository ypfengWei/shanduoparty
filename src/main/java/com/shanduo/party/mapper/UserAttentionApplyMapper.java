package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.UserAttentionApply;

public interface UserAttentionApplyMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserAttentionApply record);

    int insertSelective(UserAttentionApply record);

    UserAttentionApply selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserAttentionApply record);

    int updateByPrimaryKey(UserAttentionApply record);
    
    UserAttentionApply checkAttentionApply(Integer userId,Integer attention);
    
    int selectAttentionCount(Integer attention);
    
    List<Map<String, Object>> selectAttentionList(Integer attention,Integer pageNum,Integer pageSize);
    
    UserAttentionApply selectAttentionApply(String id,Integer attention);
    
    int updateAttentionApply(String id, Integer attention);
}