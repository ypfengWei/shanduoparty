package com.shanduo.party.timing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.service.RegionService;

/**
 * 每月统计区域代理月度提成
 * @ClassName: RegionTiming
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月4日 下午5:42:44
 *
 */
@Component
public class RegionTiming {

	private static final Logger log = LoggerFactory.getLogger(RegionTiming.class);
	
	@Autowired
	private RegionService regionService;
	
	/**
	 * 每月统计区域代理月度提成
	 * @Title: delTiming
	 * @Description: TODO
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 1 * ?")
	public void delTiming() {
		long time = System.currentTimeMillis();
		try {
			regionService.monthCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long times = System.currentTimeMillis();
		log.info("统计区域代理提成耗时毫秒:"+(times-time));
	}
}
