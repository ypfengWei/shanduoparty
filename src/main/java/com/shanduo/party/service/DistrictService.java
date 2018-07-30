package com.shanduo.party.service;

import java.util.List;

import com.shanduo.party.entity.ToArea;
import com.shanduo.party.entity.ToCity;
import com.shanduo.party.entity.ToProvince;

/**
 * 地区业务层
 * @ClassName: DistrictService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月30日 下午2:29:22
 *
 */
public interface DistrictService {

	/**
	 * 获取所有省
	 * @Title: listProvince
	 * @Description: TODO
	 * @param @return
	 * @return List<ToProvince>
	 * @throws
	 */
	public List<ToProvince> listProvince();
	
	/**
	 * 获取省下面的市
	 * @Title: listCity
	 * @Description: TODO
	 * @param @param provinceId
	 * @param @return
	 * @return List<ToCity>
	 * @throws
	 */
	public List<ToCity> listCity(String provinceId);
	
	/**
	 * 获取市下面的区或县
	 * @Title: listArea
	 * @Description: TODO
	 * @param @param cityId
	 * @param @return
	 * @return List<ToArea>
	 * @throws
	 */
	public List<ToArea> listArea(String cityId);
}
