package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ReportRecord;

public interface ReportRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(ReportRecord record);

    int insertSelective(ReportRecord record);

    ReportRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReportRecord record);

    int updateByPrimaryKey(ReportRecord record);
    
    List<Map<String, Object>> selectReportId(String activityId);
    
    List<Map<String, Object>> selectByDynamicId(String dynamicId);
    
    String selectId(String activityId, Integer userId, Integer reportId);
    
    String selectIds(String dynamicId, Integer userId, Integer reportId);
}