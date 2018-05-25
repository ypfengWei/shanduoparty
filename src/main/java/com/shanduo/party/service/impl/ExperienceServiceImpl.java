package com.shanduo.party.service.impl;

import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserMoney;
import com.shanduo.party.entity.UserMoneyRecord;
import com.shanduo.party.mapper.UserMoneyMapper;
import com.shanduo.party.mapper.UserMoneyRecordMapper;
import com.shanduo.party.service.ExperienceService;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.LevelUtils;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 
 * @ClassName: ExperienceServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月9日 上午10:18:05
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExperienceServiceImpl implements ExperienceService {

	private static final Logger log = LoggerFactory.getLogger(ExperienceServiceImpl.class);
	
	@Autowired
	private UserMoneyMapper moneyMapper;
	@Autowired
	private UserMoneyRecordMapper moneyRecordMapper;
	@Autowired
	private MoneyService moneyService;
	@Autowired
	private VipService vipService;
	
	@Override
	public int saveMoneyRecord(Integer userId, String moneyType, String remarks) {
		UserMoneyRecord moneyRecord = new UserMoneyRecord();
		moneyRecord.setId(UUIDGenerator.getUUID());
		moneyRecord.setUserId(userId);
		moneyRecord.setMoneyType(moneyType);
		moneyRecord.setRemarks(remarks);
		int i = moneyRecordMapper.insertSelective(moneyRecord);
		if(i < 1) {
			log.error("添加记录失败");
			throw new RuntimeException();
		}
		return 1;
	}

	/**
	 * 根据类型返回每日添加经验次数限制
	 * @Title: getCount
	 * @Description: TODO
	 * @param @param moneyType
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int getCount(String moneyType) {
		if("3".equals(moneyType)) {
			//签到
			return 1;
		}else if("4".equals(moneyType)) {
			//发表动态
			return 2;
		}else if("5".equals(moneyType)) {
			//发起活动
			return 2;
		}else if("6".equals(moneyType)) {
			//动态点赞
			return 10;
		}else if("7".equals(moneyType)) {
			//动态评论
			return 5;
		}else if("8".equals(moneyType)) {
			//参加活动
			return 2;
		}
		return 0;
	}
	
	@Override
	public boolean checkCount(Integer userId, String moneyType) {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String createDate = format.format(time);
		int i = moneyRecordMapper.selectByAstrict(userId, moneyType, createDate);
		if(i >= getCount(moneyType)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据VIP等级返回加倍后的检验值
	 * @Title: getVipExperience
	 * @Description: TODO
	 * @param @param userId
	 * @param @param moneyType
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int getVipExperience(Integer userId,String moneyType) {
		int vip = vipService.selectVipExperience(userId);
		BigDecimal rate = new BigDecimal(vip).divide(new BigDecimal("10"));
		if(vip < 10) {
			rate = rate.add(new BigDecimal("1"));
		}else {
			rate = rate.add(new BigDecimal("0.1"));
		}
		switch (moneyType) {
		case "0":
			//每个星期第1次签到
			rate = rate.multiply(new BigDecimal("10"));
			break;
		case "1":
			rate = rate.multiply(new BigDecimal("15"));
			//每个星期第3次签到
			break;
		case "2":
			rate = rate.multiply(new BigDecimal("20"));
			//每个星期第5次签到
			break;
		case "3":
			rate = rate.multiply(new BigDecimal("25"));
			//每个星期第6次签到
			break;
		case "4":
			//发表动态
			rate = rate.multiply(new BigDecimal("5"));
			break;
		case "5":
			//发起活动
			rate = rate.multiply(new BigDecimal("20"));
			break;
		case "6":
			//动态点赞
			rate = rate.multiply(new BigDecimal("1"));
			break;
		case "7":
			//动态评论
			rate = rate.multiply(new BigDecimal("2"));
			break;
		case "8":
			//参加活动
			rate = rate.multiply(new BigDecimal("10"));
			break;
		}
		rate = rate.setScale(0,BigDecimal.ROUND_HALF_UP);
		return rate.intValue();
	}
	
	@Override
	public int addExperience(Integer userId, String moneyType) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		int gradeA = LevelUtils.getLevel(userMoney.getExperience());
		int addExperience = getVipExperience(userId,moneyType);
		Integer experience = userMoney.getExperience() + addExperience;
		int gradeB = LevelUtils.getLevel(experience);
		userMoney.setExperience(experience);
		int i = moneyMapper.updateByPrimaryKeySelective(userMoney);
		if(i < 1) {
			log.error("添加经验失败");
			throw new RuntimeException();
		}
		String remarks = "";
		if("4".equals(moneyType)) {
			remarks = "发表动态获得经验值:+"+addExperience;
		}else if("5".equals(moneyType)) {
			remarks = "发起活动获得经验值:+"+addExperience;
		}else if("6".equals(moneyType)) {
			remarks = "动态点赞获得经验值:+"+addExperience;
		}else if("7".equals(moneyType)) {
			remarks = "动态评论获得经验值:+"+addExperience;
		}else if("8".equals(moneyType)) {
			remarks = "参加活动获得经验值:+"+addExperience;
		}
		try {
			saveMoneyRecord(userId, moneyType,remarks);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		if(gradeB > gradeA) {
			try {
				moneyService.payBeans(userId, 10*gradeA,"2");
			} catch (Exception e) {
				throw new RuntimeException();
			}
			//推送升级奖励
		}
		return 1;
	}

	@Override
	public int weekSignInCount(Integer userId) {
		Date time = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//设置时间格式  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
        System.out.println(dayWeek);
        if(1 == dayWeek) {
          cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        //设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //获得当前日期是一个星期的第几天  
        int day = cal.get(Calendar.DAY_OF_WEEK);
        //根据日历的规则，给当前日期减去星期几与一个星期第一天的差值   
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);
        String startDate = sdf.format(cal.getTime());  
        cal.add(Calendar.DATE, 7);
        String endDate = sdf.format(cal.getTime());  
        return moneyRecordMapper.weekSignInCount(userId, startDate, endDate);
	}
	
	@Override
	public int signin(Integer userId) {
		int weekSignInCount = weekSignInCount(userId)+1;
		if(weekSignInCount == 1) {
			saveSignin(userId, "0");
		}else if(weekSignInCount == 2) {
			moneyService.payBeans(userId, 10,"1");
		}else if(weekSignInCount == 3) {
			saveSignin(userId, "1");
		}else if(weekSignInCount == 4) {
			moneyService.payBeans(userId, 15,"1");
		}else if(weekSignInCount == 5) {
			saveSignin(userId, "2");
		}else if(weekSignInCount == 6) {
			saveSignin(userId, "3");
		}else if(weekSignInCount == 7) {
			moneyService.payBeans(userId, 20,"1");
		}
		return 1;
	}
	
	public int saveSignin(Integer userId,String signinType) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		int gradeA = LevelUtils.getLevel(userMoney.getExperience());
		int addExperience = getVipExperience(userId,signinType);
		Integer experience = userMoney.getExperience() + addExperience;
		int gradeB = LevelUtils.getLevel(experience);
		userMoney.setExperience(experience);
		int i = moneyMapper.updateByPrimaryKeySelective(userMoney);
		if(i < 1) {
			log.error("添加经验失败");
			throw new RuntimeException();
		}
		try {
			saveMoneyRecord(userId, "3","签到获得经验值:+"+addExperience);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		if(gradeB > gradeA) {
			try {
				moneyService.payBeans(userId, 10*gradeA,"2");
			} catch (Exception e) {
				throw new RuntimeException();
			}
			//推送升级奖励
		}
		return 1;
	}
	
	@Override
	public int selectBySignInCount(Integer userId) {
		int i = moneyRecordMapper.selectBySignInCount(userId);
		if(i > 0) {
			return i;
		}
		return 0;
	}

	@Override
	public int selectLevel(Integer userId) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		return LevelUtils.getLevel(userMoney.getExperience());
	}

}
