package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.RegionAgency;

public interface RegionAgencyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RegionAgency record);

    int insertSelective(RegionAgency record);

    RegionAgency selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RegionAgency record);

    int updateByPrimaryKey(RegionAgency record);
    
    RegionAgency login(String account, String password);
    
    List<RegionAgency> agencyList();
}