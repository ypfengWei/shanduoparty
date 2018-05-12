package com.shanduo.party.mapper;

import com.shanduo.party.entity.ShanduoPicture;

public interface ShanduoPictureMapper {
    int deleteByPrimaryKey(String id);

    int insert(ShanduoPicture record);

    int insertSelective(ShanduoPicture record);

    ShanduoPicture selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ShanduoPicture record);

    int updateByPrimaryKey(ShanduoPicture record);
}