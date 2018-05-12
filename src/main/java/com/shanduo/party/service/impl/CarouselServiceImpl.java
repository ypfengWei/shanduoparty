package com.shanduo.party.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ShanduoCarousel;
import com.shanduo.party.mapper.ShanduoCarouselMapper;
import com.shanduo.party.service.CarouselService;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 
 * @ClassName: CarouselServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月12日 下午2:09:55
 *
 */
@Service
@Transactional
public class CarouselServiceImpl implements CarouselService {

	private static final Logger log = LoggerFactory.getLogger(CarouselServiceImpl.class);
	
	@Autowired
	private ShanduoCarouselMapper carouselMapperl;
	
	@Override
	public int saveCarousel(String picture) {
		ShanduoCarousel carousel = new ShanduoCarousel();
		carousel.setId(UUIDGenerator.getUUID());
		carousel.setPicture(picture);
		int i = carouselMapperl.insertSelective(carousel);
		if(i < 1) {
			log.error("轮播图信息录入失败");
			throw new RuntimeException();
		}
		return 0;
	}

	@Override
	public int delleteCarousel(String picture) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String, Object>> carouselList() {
		// TODO Auto-generated method stub
		return null;
	}

}
