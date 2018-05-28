package com.shanduo.party.service.impl;

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
import com.shanduo.party.util.UUIDGenerator;

/**
 * vip操作实现类
 * @ClassName: VipServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月18日 下午4:19:30
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipServiceImpl implements VipService {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ShanduoVipMapper vipMapper;

	@Autowired
	private VipExperienceMapper experienceMapper;

	@Override
	public int saveVip(Integer userId, Date date,Integer month,String vipType) {
		ShanduoVip userVip = vipMapper.selectByVipType(userId,vipType);
		if(userVip == null) {
			userVip = new ShanduoVip();
			userVip.setId(UUIDGenerator.getUUID());
			userVip.setUserId(userId);
			userVip.setVipType(vipType);
			userVip.setVipStartTime(date);
			userVip.setVipEndTime(new Date(date.getTime() + 1000L * 60L * 60L * 24L * 31L * month));
			int i = vipMapper.insertSelective(userVip);
			if (i < 1) {
				log.error("会员添加失败");
				throw new RuntimeException();
			}
		}else {
			userVip.setVipStartTime(date);
			userVip.setVipEndTime(new Date(date.getTime() + 1000L * 60L * 60L * 24L * 31L * month));
			int i = vipMapper.updateByPrimaryKeySelective(userVip);
			if (i < 1) {
				log.error("重新开通会员失败");
				throw new RuntimeException();
			}
		}
		//添加成长值
		VipExperience vip = experienceMapper.selectByPrimaryKey(userId);
		if(vip == null) {
			vip = new VipExperience();
			vip.setUserId(userId);
			int count = experienceMapper.insertSelective(vip);
			if (count < 1) {
				log.error("成长值添加失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}
	
	/**
	 * 续费vip
	 * @Title: renewVip
	 * @Description: TODO
	 * @param @param vipId
	 * @param @param date 续费的开始时间
	 * @param @param month 续费月份
	 * @return void
	 * @throws
	 */
	public void renewVip(String vipId, Date date, Integer month) {
		ShanduoVip userVip = new ShanduoVip();
		userVip.setId(vipId);
		userVip.setVipEndTime(new Date(date.getTime() + 1000L * 60L * 60L * 24L * 31L * month));
		int i = vipMapper.updateByPrimaryKeySelective(userVip);
		if (i < 1) {
			log.error("续费会员失败");
			throw new RuntimeException();
		}
	}
	
	@Override
	public int updateVip(Integer userId, Integer month, String vipType) {
		Date date = new Date();
		List<ShanduoVip> resultList = vipMapper.selectByUserId(userId);
		if(resultList == null || resultList.isEmpty()) {
			//开通
			saveVip(userId, date, month, vipType);
		}else if(resultList.size() == 2) {
			ShanduoVip vip1 = resultList.get(0);
			ShanduoVip vip2 = resultList.get(1);
			if("0".equals(vipType)) {
				//续费vip
				if("0".equals(vip1.getVipType())) {
					renewVip(vip1.getId(), vip1.getVipEndTime(), month);
				}else {
					renewVip(vip2.getId(), vip2.getVipEndTime(), month);
				}
			}else {
				//续费svip和vip延长
				renewVip(vip1.getId(), vip1.getVipEndTime(), month);
				renewVip(vip2.getId(), vip2.getVipEndTime(), month);
			}
		}else {
			ShanduoVip vip = resultList.get(0);
			if(vipType.equals(vip.getVipType())) {
				//续费
				renewVip(vip.getId(), vip.getVipEndTime(), month);
			}else {
				if("0".equals(vipType)) {
					//开通vip
					saveVip(userId, vip.getVipEndTime(), month, vipType);
				}else {
					//开通svip
					saveVip(userId, date, month, vipType);
					//vip延长
					renewVip(vip.getId(), vip.getVipEndTime(), month);
				}
			}
		}
		return 1;
	}
	
	/**
	 * 计算vip等级
	 * @Title: getVipGrade
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int getVipGrade(Integer userId) {
		int experience = experienceMapper.selectByUserId(userId);
		if(experience < 300) {
			return 1;
		}else if(experience < 900) {
			return 2;
		}else if(experience < 1800) {
			return 3;
		}else if(experience < 3600) {
			return 4;
		}else if(experience < 7200) {
			return 5;
		}else if(experience < 14400) {
			return 6;
		}else if(experience < 28800) {
			return 7;
		}
		return 8;
	}
	
	@Override
	public int selectVipLevel(Integer userId) {
		List<ShanduoVip> resultList = vipMapper.selectByUserId(userId);
		if(resultList == null || resultList.isEmpty()) {
			return 0;
		}
		if(resultList.size() == 2) {
			return 10+getVipGrade(userId);
		}
		if("0".equals(resultList.get(0).getVipType())) {
			return getVipGrade(userId);
		}
		return 10+getVipGrade(userId);
	}
		
}
