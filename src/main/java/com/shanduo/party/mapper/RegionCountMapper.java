package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.RegionCount;

public interface RegionCountMapper {
    int deleteByPrimaryKey(String id);

    int insert(RegionCount record);

    int insertSelective(RegionCount record);

    RegionCount selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(RegionCount record);

    int updateByPrimaryKey(RegionCount record);
    
    int countListCount(Integer userId);
    
    List<Map<String, Object>> countList(Integer userId, Integer pageNum, Integer pageSize);
}