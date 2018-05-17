package com.shanduo.party.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserMoney;
import com.shanduo.party.mapper.UserMoneyMapper;
import com.shanduo.party.mapper.UserMoneyRecordMapper;
import com.shanduo.party.service.ExperienceService;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.util.Page;

/**
 * 
 * @ClassName: MoneyServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月25日 下午2:05:58
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MoneyServiceImpl implements MoneyService {

	private static final Logger log = LoggerFactory.getLogger(MoneyServiceImpl.class);
	
	@Autowired
	private UserMoneyMapper moneyMapper;
	@Autowired
	private UserMoneyRecordMapper moneyRecordMapper;
	@Autowired
	private ExperienceService experienceService;

	@Override
	public UserMoney selectByUserId(Integer userId) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		if(userMoney == null) {
			return null;
		}
		return userMoney;
	}
	
	@Override
	public Map<String, Object> moneyList(Integer userId, Integer pageNum, Integer pageSize) {
		int totalRecord = moneyRecordMapper.meneyCount(userId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList = moneyRecordMapper.moneyList(userId, pageNum, page.getPageSize());
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public int payMoney(Integer userId, BigDecimal amount) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		BigDecimal money = userMoney.getMoney().add(amount);
		userMoney.setMoney(money);
		int i = moneyMapper.updateByPrimaryKeySelective(userMoney);
		if(i < 1) {
			log.error("充值失败");
			throw new RuntimeException();
		}
		try {
			experienceService.saveMoneyRecord(userId, "1", "充值闪多币:"+"+"+amount);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public int checkMoney(Integer userId, BigDecimal amount) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		if(userMoney.getMoney().compareTo(amount) < 0) {
			log.error("余额不足");
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public int consumeMoney(Integer userId, BigDecimal amount, String remarks) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		BigDecimal money = userMoney.getMoney().subtract(amount);
		userMoney.setMoney(money);
		int i = moneyMapper.updateByPrimaryKeySelective(userMoney);
		if(i < 1) {
			log.error("消费失败");
			throw new RuntimeException();
		}
		try {
			experienceService.saveMoneyRecord(userId, "2", remarks+":-"+amount);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int payBeans(Integer userId, Integer beans,String typeId) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		Integer beanss = userMoney.getBeans()+ beans;
		userMoney.setBeans(beanss);
		int i = moneyMapper.updateByPrimaryKeySelective(userMoney);
		if(i < 1) {
			log.error("赠送闪多豆失败");
			throw new RuntimeException();
		}
		String remarks = "";
		if("1".equals(typeId)) {
			remarks = "签到获得闪多豆"+beans+"颗";
		}else {
			remarks = "升级获得闪多豆"+beans+"颗";
		}
		try {
			experienceService.saveMoneyRecord(userId, "9", remarks);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return 1;
	}

}
