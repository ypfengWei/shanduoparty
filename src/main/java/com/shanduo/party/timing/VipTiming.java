package com.shanduo.party.timing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
	
	/**
	 * 定时每天 0:0:0自动执行
	 * @Title: delTiming
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void updTiming() {
		int i = vipExperienceMapper.updateByUserId();
		int b = vipExperienceMapper.updateByUserIdTwo();
		int a = vipExperienceMapper.updateByUserIdThree();
		logger.info("给"+i+"svip添加了成长值");
		logger.info("给"+b+"vip添加了成长值");
		logger.info("给"+a+"vip扣除了成长值");
	}
}
