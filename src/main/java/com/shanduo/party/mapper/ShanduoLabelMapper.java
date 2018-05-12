package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ShanduoLabel;

public interface ShanduoLabelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShanduoLabel record);

    int insertSelective(ShanduoLabel record);

    ShanduoLabel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShanduoLabel record);

    int updateByPrimaryKey(ShanduoLabel record);
    
    List<Map<String, Object>> selectList();
}