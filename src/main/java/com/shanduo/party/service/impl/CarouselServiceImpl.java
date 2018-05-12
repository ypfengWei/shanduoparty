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
import com.shanduo.party.util.PictureUtils;
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
@Transactional(rollbackFor = Exception.class)
public class CarouselServiceImpl implements CarouselService {

	private static final Logger log = LoggerFactory.getLogger(CarouselServiceImpl.class);
	
	@Autowired
	private ShanduoCarouselMapper carouselMapper;

	@Override
	public List<Map<String, Object>> carouselList() {
		List<Map<String, Object>> resultList = carouselMapper.carouselList();
		if(resultList == null) {
			return null;
		}
		for (Map<String, Object> map : resultList) {
			String picture = map.get("picture").toString();
			map.put("picture", PictureUtils.getPictureUrl(picture));
		}
		return resultList;
	}

	@Override
	public int updateCarousel(String carouselId, String picture) {
		ShanduoCarousel carousel = new ShanduoCarousel();
		carousel.setId(carouselId);
		carousel.setPicture(picture);
		int i = carouselMapper.updateByPrimaryKeySelective(carousel);
		if(i < 1) {
			log.error("轮播图信息修改失败");
			throw new RuntimeException();
		}
		return 1;
	}

}
