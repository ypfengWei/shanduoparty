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
    
    List<Map<String, Object>> selectReportId(String activityId, String dynamicId);
    
    String selectId(String activityId, String dynamicId, String typeId, Integer userId);
    
    int selectUserId(String dynamicId);
    
    int deleteCount(String activityId, String dynamicId);
    
    List<Map<String, Object>> selectInfo(String activityId, String dynamicId);
    
    List<Map<String, Object>> selectActivityInfo(Integer page, Integer pageSize);
    
    List<Map<String, Object>> selectDynamicInfo(Integer page, Integer pageSize);
    
    int activityCount();
    
    int dynamicCount();
}