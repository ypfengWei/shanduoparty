package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ShanduoCollect;

public interface ShanduoCollectMapper {
    int deleteByPrimaryKey(String id);

    int insert(ShanduoCollect record);

    int insertSelective(ShanduoCollect record);

    ShanduoCollect selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ShanduoCollect record);

    int updateByPrimaryKey(ShanduoCollect record);
    
    ShanduoCollect checkCollect(Integer userId,String fileUrl);
    
    int deleteByUserFile(Integer userId,String collectId);
    
    int selectByCount(Integer userId);
    
    List<Map<String, Object>> selectByUserList(Integer userId,Integer page,Integer pageSize);
}