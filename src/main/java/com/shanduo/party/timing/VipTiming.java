package com.shanduo.party.timing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.entity.ShanduoVip;
import com.shanduo.party.entity.VipExperience;
import com.shanduo.party.mapper.ShanduoVipMapper;
import com.shanduo.party.mapper.VipExperienceMapper;

/**
 * vip成长值定时器
 * @ClassName: VipTiming
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author A18ccms a18ccms_gmail_com
 * @date 2018年5月14日 下午4:16:28
 *
 */
@Component
public class VipTiming {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityScoreTiming.class);
	
	@Autowired
	private VipExperienceMapper vipExperienceMapper;
	
	@Autowired
	private ShanduoVipMapper mapper;
	
	/**
	 * 定时每天 0:0:0自动执行
	 * @Title: delTiming
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param 
	 * @return void
	 * @throws
	 */
//	@Scheduled(cron = "0 0 0 * * ? ")
	@Scheduled(cron = "0/9 * * * * ? ")
	public void updTiming() {
		int experience = 0;
		int i = 0;
		List<ShanduoVip> list = mapper.selectAll();
		for (ShanduoVip shanduoVip : list) {
			if(shanduoVip.getVipType().equals("0")) {
				VipExperience vipExperience = vipExperienceMapper.selectByUserId(shanduoVip.getUserId());
				experience = vipExperience.getExperience() + 10;
			} else {
				VipExperience vipExperience = vipExperienceMapper.selectByUserId(shanduoVip.getUserId());
				experience = vipExperience.getExperience() + 15;
			}
			i = vipExperienceMapper.updateExperienceByUserId(experience, shanduoVip.getUserId());
		}
		logger.info("vip成长值:"+i);
	}
}
