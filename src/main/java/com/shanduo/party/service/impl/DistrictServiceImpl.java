package com.shanduo.party.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shanduo.party.entity.ToArea;
import com.shanduo.party.entity.ToCity;
import com.shanduo.party.entity.ToProvince;
import com.shanduo.party.mapper.ToAreaMapper;
import com.shanduo.party.mapper.ToCityMapper;
import com.shanduo.party.mapper.ToProvinceMapper;
import com.shanduo.party.service.DistrictService;

/**
 * 
 * @ClassName: DistrictServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月30日 下午2:33:18
 *
 */
@Service
public class DistrictServiceImpl implements DistrictService {
	
	private static final Logger log = LoggerFactory.getLogger(DistrictServiceImpl.class);

	@Autowired
	private ToProvinceMapper provinceMapper;
	@Autowired
	private ToCityMapper cityMapper;
	@Autowired
	private ToAreaMapper areaMapper;
	
	@Override
	public List<ToProvince> listProvince() {
		return provinceMapper.listProvince();
	}

	@Override
	public List<ToCity> listCity(String provinceId) {
		return cityMapper.listCity(provinceId);
	}

	@Override
	public List<ToArea> listArea(String cityId) {
		return areaMapper.listArea(cityId);
	}

}
