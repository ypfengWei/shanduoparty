package com.shanduo.party.timing;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.service.ScoreService;

/**
 * 默认好评定时器
 * @ClassName: ActivityScoreTiming
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月7日 下午5:39:25
 *
 */
@Component
public class ActivityScoreTiming {

	private static final Logger logger = LoggerFactory.getLogger(ActivityScoreTiming.class);
	
	@Autowired
	private ScoreService scoreService;
	
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
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = format.format(time - 1000 * 60 * 60 * 24 * 7);
		int i = scoreService.updateById(createDate);
//		int count = shanduoReputationMapper.insertSelective(record);
		logger.info("参与者默认好评:"+i);
	}
	
	/**
	 * 定时每天 0:0:0自动执行
	 * @Title: delTiming
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void twoUpdTiming() {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = format.format(time - 1000 * 60 * 60 * 24 * 7);
		int i = scoreService.updateByIdTime(createDate);
		logger.info("发起者默认好评:"+i);
	}
	
//	@Scheduled(cron = "5 0 0 * * ? ")
//	public void reputationTiming(Integer userId) {
//		long time = System.currentTimeMillis();
//		Format format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
//		String endTime = format.format(time);
//		String startTime = format.format(time - 1000*60*60*24);
//		int i = recordMapper.selectByUserId(userId, startTime, endTime);
//		int count = recordMapper.selectByUserIdTwo(userId, startTime, endTime);
//		ShanduoReputation shanduoReputation = new ShanduoReputation();
//		ShanduoReputation shanduoReputationRecord = shanduoReputationMapper.selectByPrimaryKey(userId);
//		Integer reputation = null;
//		if(-10<i-count && i-count<10) {
//			reputation = shanduoReputation.getDeduction() + (i-count);
//			shanduoReputationRecord.setReputation(reputation);
//		} else if(-10>i-count){
//			reputation = shanduoReputation.getDeduction() - 10;
//			shanduoReputationRecord.setReputation(reputation);
//		} else if(i-count>10){
//			reputation = shanduoReputation.getDeduction() + 10;
//			shanduoReputationRecord.setReputation(reputation);
//		}
//		int n = shanduoReputationMapper.updateByUserId(userId, reputation);
//		logger.info("统计一天的信誉分:"+ n);
//	}
}

