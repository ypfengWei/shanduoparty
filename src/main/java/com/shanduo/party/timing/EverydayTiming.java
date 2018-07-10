package com.shanduo.party.timing;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.party.common.ShanduoConsts;
import com.shanduo.party.mapper.VipExperienceMapper;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.service.MoneyService;

/**
 * 每日定时工具类
 * @ClassName: EverydayTiming
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月14日 下午3:22:35
 *
 */
@Component
public class EverydayTiming {

	private static final Logger log = LoggerFactory.getLogger(EverydayTiming.class);
	
	@Autowired
	private CodeService codeService;
	@Autowired
	private MoneyService moneyService;
	@Autowired
	private VipExperienceMapper vipExperienceMapper;
	
	
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
		String createDate = format.format(time - ShanduoConsts.WEEK);
		int i = codeService.deleteTimer(createDate);
		log.info("删除过期验证码记录"+i+"条");
	}
	
	/**
	 * 每日闪多豆转换赏金
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
	
	/**
	 * 每日检测重置刷新次数
	 * @Title: updateRefresh
	 * @Description: TODO
	 * @param 
	 * @return void
	 * @throws
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void updateRefresh() {
		log.info("重置刷新次数启动");
		moneyService.updateRefresh();
	}
	
	/**
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
