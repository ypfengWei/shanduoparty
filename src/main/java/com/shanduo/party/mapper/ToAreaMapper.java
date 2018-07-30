package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.ToArea;

public interface ToAreaMapper {
    int deleteByPrimaryKey(String areaId);

    int insert(ToArea record);

    int insertSelective(ToArea record);

    ToArea selectByPrimaryKey(String areaId);

    int updateByPrimaryKeySelective(ToArea record);

    int updateByPrimaryKey(ToArea record);
    
    List<ToArea> listArea(String cityId);
}