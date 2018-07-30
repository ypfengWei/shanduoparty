package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.ToCity;

public interface ToCityMapper {
    int deleteByPrimaryKey(String cityId);

    int insert(ToCity record);

    int insertSelective(ToCity record);

    ToCity selectByPrimaryKey(String cityId);

    int updateByPrimaryKeySelective(ToCity record);

    int updateByPrimaryKey(ToCity record);
    
    List<ToCity> listCity(String provinceId);
}