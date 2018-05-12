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

	/**
	 * 查询所有的轮播图
	 * @Title: carouselList
	 * @Description: TODO
	 * @param @return
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	List<Map<String, Object>> carouselList();
	
	/**
	 * 修改轮播图
	 * @Title: updateCarousel
	 * @Description: TODO
	 * @param @param carouselId
	 * @param @param picture
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateCarousel(String carouselId,String picture);
	
}
