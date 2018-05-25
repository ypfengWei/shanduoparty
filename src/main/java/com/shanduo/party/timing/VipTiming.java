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
	
	private static final Logger log = LoggerFactory.getLogger(ActivityScoreTiming.class);
	
	@Autowired
	private VipExperienceMapper vipExperienceMapper;
	
	/**
	 * 定时每天 0:0:0自动执行
	 * 增减vip成长值
	 * @Title: updateTiming
	 * @Description: TODO
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void updateTiming() {
		int x = vipExperienceMapper.updateByUserId();
		log.info("给"+x+"个SVIP添加了15点成长值");
		int y = vipExperienceMapper.updateByUserIdTwo();
		log.info("给"+y+"个VIP添加了10点成长值");
		int z = vipExperienceMapper.updateByUserIdThree();
		log.info("给"+z+"非VIP扣除了10点成长值");
	}
}
