package com.shanduo.party.mapper;

import com.shanduo.party.entity.ShanduoCarousel;

public interface ShanduoCarouselMapper {
    int deleteByPrimaryKey(String id);

    int insert(ShanduoCarousel record);

    int insertSelective(ShanduoCarousel record);

    ShanduoCarousel selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ShanduoCarousel record);

    int updateByPrimaryKey(ShanduoCarousel record);
}