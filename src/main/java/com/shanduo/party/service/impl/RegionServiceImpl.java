package com.shanduo.party.service.impl;

import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.RegionAgency;
import com.shanduo.party.entity.RegionCount;
import com.shanduo.party.mapper.RegionAgencyMapper;
import com.shanduo.party.mapper.RegionCountMapper;
import com.shanduo.party.mapper.UserOrderMapper;
import com.shanduo.party.service.RegionService;
import com.shanduo.party.util.MD5Utils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 
 * @ClassName: RegionServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月4日 上午9:55:56
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegionServiceImpl implements RegionService {

	private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);
	
	@Autowired
	private RegionAgencyMapper agencyMapper;
	@Autowired
	private RegionCountMapper countMapper;
	@Autowired
	private UserOrderMapper orderMapper;
	
	@Override
	public Map<String, Object> loginRegion(String account, String password) {
		password = MD5Utils.getInstance().getMD5(password);
		RegionAgency agency =  agencyMapper.login(account, password);
		if(agency == null) {
			return null;
		}
		Map<String, Object> resultMap = new HashMap<>(2);
		resultMap.put("userId", agency.getId());
		resultMap.put("address", agency.getAddress());
		return resultMap;
	}

	@Override
	public int updatePassword(Integer userId, String password, String newPassword) {
		password = MD5Utils.getInstance().getMD5(password);
		newPassword = MD5Utils.getInstance().getMD5(newPassword);
		int i = agencyMapper.updatePassword(userId, password, newPassword);
		if(i < 1) {
			log.error("修改密码失败");
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public BigDecimal selectCurrentMonth(Integer userId) {
		RegionAgency agency = agencyMapper.selectByPrimaryKey(userId);
		if(agency == null) {
			return null;
		}
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-01 00:00:00");
		String startDate = format.format(time);
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endDate = format.format(time);
		BigDecimal count = orderMapper.regionCount(agency.getAddress(), startDate, endDate);
		count = count.multiply(new BigDecimal(agency.getProportion()+""));
		count = count.divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN);
		return count;
	}

	@Override
	public Map<String, Object> countList(Integer userId, Integer pageNum, Integer pageSize) {
		int totalRecord = countMapper.countListCount(userId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1) * page.getPageSize();
		List<Map<String, Object>> resultList = countMapper.countList(userId, pageNum, page.getPageSize());
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

	@Override
	public int monthCount() {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-01");
		String endDate = format.format(time);
		String month = endDate.substring(5, 7);
		switch (month) {
			case "03":
				time = time-1000L*60*60*24*28;
				break;
			case "05":
			case "07":
			case "10":
			case "12":
				time = time-1000L*60*60*24*30;
				break;
			default:
				time = time-1000L*60*60*24*31;
				break;
		}
		String startDate = format.format(time);
		List<RegionAgency> list = agencyMapper.agencyList();
		for (RegionAgency agency : list) {
			Integer userId = agency.getId();
			String address = agency.getAddress();
			Integer proportion = agency.getProportion();
			BigDecimal count = orderMapper.regionCount(address, startDate, endDate);
			count = count.multiply(new BigDecimal(proportion+""));
			count = count.divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN);
			RegionCount counts = new RegionCount();
			counts.setId(UUIDGenerator.getUUID());
			counts.setUserId(userId);
			counts.setName(startDate.substring(0, 4)+startDate.substring(5, 7));
			counts.setMoney(count);
			int i = countMapper.insertSelective(counts);
			if(i < 1) {
				log.error("录入区域代理每月统计出错");
				throw new RuntimeException();
			}
		}
		return 1;
	}

}
