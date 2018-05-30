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
	 * 参与者默认好评定时每天 0:0:0自动执行
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
		logger.info("参与者默认好评:"+i);
	}
	
	/**
	 * 发起者默认好评定时每天 0:0:0自动执行
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
	
}

