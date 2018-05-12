package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

/**
 * 轮播图业务层
 * @ClassName: CarouselService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月12日 上午10:00:03
 *
 */
public interface CarouselService {

	int saveCarousel(String picture);
	
	int delleteCarousel(String picture);
	
	List<Map<String, Object>> carouselList();
	
}
