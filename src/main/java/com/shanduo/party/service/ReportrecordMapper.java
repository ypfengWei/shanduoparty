package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.Reportrecord;

public interface ReportrecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(Reportrecord record);

    int insertSelective(Reportrecord record);

    Reportrecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Reportrecord record);

    int updateByPrimaryKey(Reportrecord record);
    
    List<Map<String, Object>> selectReportId(String activityId);
}