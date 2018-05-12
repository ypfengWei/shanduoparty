package com.shanduo.party.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.controller.ActivityController;
import com.shanduo.party.entity.ShanduoVip;
import com.shanduo.party.entity.VipExperience;
import com.shanduo.party.mapper.ShanduoVipMapper;
import com.shanduo.party.mapper.VipExperienceMapper;
import com.shanduo.party.service.VipService;

@Service
@Transactional(rollbackFor = Exception.class)
public class VipServiceImpl implements VipService {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ShanduoVipMapper shanduoVipMapper;

	@Autowired
	private VipExperienceMapper vipExperienceMapper;

	@Override
	public List<ShanduoVip> selectByUserIdAndTime(Integer userId) {
		List<ShanduoVip> resultList = shanduoVipMapper.selectByUserIdAndTime(userId);
		if (resultList == null) {
			return null;
		}
		return resultList;
	}

	@Override
	public int insertSelective(Integer userId, String vipType, String vipStartTime) {
		ShanduoVip shanduoVip = new ShanduoVip();
		shanduoVip.setUserId(userId);
		shanduoVip.setVipType(vipType);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = formatter.parse(vipStartTime);
			shanduoVip.setVipStartTime(date);
			long time = date.getTime() + 24 * 60 * 60 * 1000 * 30;
			String vipEndTime = formatter.format(time);
			Date datetime = formatter.parse(vipEndTime);
			shanduoVip.setVipEndTime(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int i = shanduoVipMapper.insertSelective(shanduoVip);
		if (i <= 0) {
			log.error("会员添加失败");
			throw new RuntimeException();
		}
		VipExperience vip = new VipExperience();
		vip.setUserId(userId);
		int count = vipExperienceMapper.insertSelective(vip);
		if (count <= 0) {
			log.error("成长值添加失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updateByUserId(Integer userId, Integer month) {
		long longtime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = format.format(longtime);
		Date date = null;
		try {
			date = format.parse(createDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<ShanduoVip> resultList = shanduoVipMapper.selectByUserIdAndTime(userId);
		if(resultList != null) {
			for (ShanduoVip shanduoVip : resultList) {
				if(shanduoVip.getVipEndTime().getTime() < date.getTime()) {
					long time = date.getTime() + 24 * 60 * 60 * 1000 * 30 * month;
					String vipEndTime = format.format(time);
					int i = shanduoVipMapper.updateByUserId(createDate, vipEndTime, userId);
					if (i <= 0) {
						log.error("会员开通失败");
						throw new RuntimeException();
					}
				}
				if(shanduoVip.getVipEndTime().getTime() > date.getTime()) {
					long time = shanduoVip.getVipEndTime().getTime() + 24 * 60 * 60 * 1000 * 30 * month;
					String vipEndTime = format.format(time);
					int i = shanduoVipMapper.updateByUserIdTwo(vipEndTime, userId);
					if (i <= 0) {
						log.error("会员开通失败");
						throw new RuntimeException();
					}
				}
			}
		}
		return 1;
	}

}
