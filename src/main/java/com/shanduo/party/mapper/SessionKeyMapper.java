package com.shanduo.party.mapper;

import com.shanduo.party.entity.SessionKey;

public interface SessionKeyMapper {
    int deleteByPrimaryKey(String id);

    int insert(SessionKey record);

    int insertSelective(SessionKey record);

    SessionKey selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SessionKey record);

    int updateByPrimaryKey(SessionKey record);
    
    String selectSessionKey(String open_id);
}