package com.shanduo.party.timing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.service.ScoreService;

/**
 * 信誉分定时器   (reputation一天最多加减十分)
 * @ClassName: ReputationTiming
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月15日 下午5:18:17
 */
@Component
public class ReputationTiming {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityScoreTiming.class);
	
	@Autowired
	private ScoreService scoreService;
	@Scheduled(cron = "59 59 23 * * ? ")
	public void reputationTiming() {
		int i =  scoreService.updateByReputation();
		logger.info("信誉分:" + i);
	}
}
