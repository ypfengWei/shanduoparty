package com.shanduo.party.timing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.service.MoneyService;

/**
 * 每日闪多豆转换余额
 * @ClassName: BeansTiming
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月14日 下午3:22:35
 *
 */
@Component
public class BeansTiming {

	@Autowired
	private MoneyService moneyService;
	
	/**
	 * 每日闪多豆转换余额
	 * @Title: switchBeans
	 * @Description: TODO
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void switchBeans() {
		moneyService.switchBeans();
	}
}
