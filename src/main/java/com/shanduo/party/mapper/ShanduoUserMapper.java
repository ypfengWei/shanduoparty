package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ShanduoUser;

public interface ShanduoUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShanduoUser record);

    int insertSelective(ShanduoUser record);

    ShanduoUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShanduoUser record);

    int updateByPrimaryKey(ShanduoUser record);
    
    ShanduoUser selectByPhone(String phone);
    
    int insertUser(ShanduoUser record);
    
    ShanduoUser loginById(int id,String password);
    
    ShanduoUser loginByPhone(String phone,String password);
    
    int updateByPhone(String phone,String password);
    
    List<Map<String, Object>> seekUser(String query);
    
    List<Map<String, Object>> seekAttention(Integer userId,String query);
    
    Map<String, Object> selectById(Integer userId);
    
    List<Map<String, Object>> listUser(String jurisdiction);
}