package com.shanduo.party.timing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.service.MoneyService;

/**
 * 每月重置刷新次数
 * @ClassName: RefreshTiming
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月2日 上午10:18:54
 *
 */
@Component
public class RefreshTiming {

	
	@Autowired
	private MoneyService moneyService;
	
	/**
	 * 每日检测重置刷新次数
	 * @Title: delTiming
	 * @Description: TODO
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void delTiming() {
		moneyService.updateRefresh();
	}
}
