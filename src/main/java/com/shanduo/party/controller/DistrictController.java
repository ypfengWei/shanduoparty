package com.shanduo.party.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.ToArea;
import com.shanduo.party.entity.ToCity;
import com.shanduo.party.entity.ToProvince;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.DistrictService;
import com.shanduo.party.util.StringUtils;


/**
 * 地区接口层
 * @ClassName: DistrictController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月30日 下午2:39:45
 *
 */
@Controller
@RequestMapping(value = "jdistrict")
public class DistrictController {

	private static final Logger log = LoggerFactory.getLogger(DistrictController.class);
	
	@Autowired
	private DistrictService districtService;
	
	
	/**
	 * 获取所有省
	 * @Title: listProvince
	 * @Description: TODO
	 * @param @param request
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "listProvince",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean listProvince(HttpServletRequest request) {
		List<ToProvince> list = new ArrayList<>();
		try {
			list = districtService.listProvince();
		} catch (Exception e) {
			log.error("获取所有省错误");
			return new ErrorBean(10003, "内部错误");
		}
		return new SuccessBean(list);
	}
	
	/**
	 * 获取省下面的市
	 * @Title: listCity
	 * @Description: TODO
	 * @param @param request
	 * @param @param provinceId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "listCity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean listCity(HttpServletRequest request, String provinceId) {
		if(StringUtils.isNull(provinceId)) {
			log.warn("省级ID为空");
			return new ErrorBean(10002,"省级ID为空");
		}
		List<ToCity> list = new ArrayList<>();
		try {
			list = districtService.listCity(provinceId);
		} catch (Exception e) {
			log.error("获取市错误");
			return new ErrorBean(10003, "内部错误");
		}
		return new SuccessBean(list);
	}
	
	/**
	 * 获取市下面的区或县
	 * @Title: listArea
	 * @Description: TODO
	 * @param @param request
	 * @param @param cityId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "listArea",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean listArea(HttpServletRequest request, String cityId) {
		if(StringUtils.isNull(cityId)) {
			log.warn("市级ID为空");
			return new ErrorBean(10002,"市级ID为空");
		}
		List<ToArea> list = new ArrayList<>();
		try {
			list = districtService.listArea(cityId);
		} catch (Exception e) {
			log.error("获取区域错误");
			return new ErrorBean(10003, "内部错误");
		}
		return new SuccessBean(list);
	}
}
