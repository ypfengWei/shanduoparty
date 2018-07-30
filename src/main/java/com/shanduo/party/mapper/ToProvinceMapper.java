package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.ToProvince;

public interface ToProvinceMapper {
    int deleteByPrimaryKey(String provinceId);

    int insert(ToProvince record);

    int insertSelective(ToProvince record);

    ToProvince selectByPrimaryKey(String provinceId);

    int updateByPrimaryKeySelective(ToProvince record);

    int updateByPrimaryKey(ToProvince record);
    
    List<ToProvince> listProvince();
}