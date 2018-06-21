package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.UserTaskRecord;

public interface UserTaskRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserTaskRecord record);

    int insertSelective(UserTaskRecord record);

    UserTaskRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserTaskRecord record);

    int updateByPrimaryKey(UserTaskRecord record);
    
    int taskRecord(Integer userId, String time, String type);
    
    List<String> releaseRecord(String time, Integer userId, Integer limit);
    
    List<String> joinRecord(String time, Integer userId, Integer limit);
}