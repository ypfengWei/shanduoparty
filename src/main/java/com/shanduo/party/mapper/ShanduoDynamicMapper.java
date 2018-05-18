package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ShanduoDynamic;

public interface ShanduoDynamicMapper {
    int deleteByPrimaryKey(String id);

    int insert(ShanduoDynamic record);

    int insertSelective(ShanduoDynamic record);

    ShanduoDynamic selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ShanduoDynamic record);

    int updateByPrimaryKeyWithBLOBs(ShanduoDynamic record);

    int updateByPrimaryKey(ShanduoDynamic record);
    
    Map<String, Object> selectById(String id);
    
    int updateByDelFlag(String id,Integer userId);
    
    int attentionCount(Integer userId);
    
    List<Map<String, Object>> attentionList(Integer userId,Integer page,Integer pageSize);
    
    int nearbyCount(double minlon, double maxlon, double minlat, double maxlat);
    
    List<Map<String, Object>> nearbyList(double minlon, double maxlon, double minlat, double maxlat, Integer page, Integer pageSize);
    
    int selectMyCount(Integer userId);
    
    List<Map<String, Object>> selectMyList(Integer userId,Integer page,Integer pageSize);
    
    Map<String, Object> selectByDynamicId(String dynamicId);
    
}