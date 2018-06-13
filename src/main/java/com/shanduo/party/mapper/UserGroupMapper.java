package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.UserGroup;

public interface UserGroupMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserGroup record);

    int insertSelective(UserGroup record);

    UserGroup selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserGroup record);

    int updateByPrimaryKey(UserGroup record);
    
    int userGroupCount(Integer userId,String groupType);
    
    int deleteGroup(Integer userId,String groupId);
    
    List<String> nameList(String name);
}