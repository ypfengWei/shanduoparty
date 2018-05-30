package com.shanduo.party.timing;


import java.text.Format;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.common.ShanduoConstants;
import com.shanduo.party.service.CodeService;

/**
 * 定时删除验证码记录
 * @ClassName: PhoneCodeTiming
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月14日 下午2:37:52
 *
 */
@Component
public class PhoneCodeTiming {

	private static final Logger log = LoggerFactory.getLogger(PhoneCodeTiming.class);
	
	@Autowired
	private CodeService codeService;
	
	/**
	 * 定时每天 0:0:0自动执行
	 * 删除七天前过期的验证码记录
	 * @Title: delTiming
	 * @Description: TODO
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void delTiming() {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = format.format(time - ShanduoConstants.WEEK);
		int i = codeService.deleteTimer(createDate);
		log.info("删除过期验证码记录"+i+"条");
	}
	
}
