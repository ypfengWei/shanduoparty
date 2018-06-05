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
import com.shanduo.party.service.ActivityService;
import com.shanduo.party.service.ExperienceService;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.util.MD5Utils;
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
	@Autowired
	private ActivityService activityService;

	@Override
	public Map<String, Object> selectByUserId(Integer userId) {
		UserMoney money = moneyMapper.selectByUserId(userId);
		if(money == null) {
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(2);
		resultMap.put("money", money.getMoney());
		resultMap.put("beans", money.getBeans());
		resultMap.put("refresh", money.getRefresh());
		return resultMap;
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
	public int payMoney(Integer userId, BigDecimal money,String remarks) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		money = userMoney.getMoney().add(money);
		userMoney.setMoney(money);
		int i = moneyMapper.updateByPrimaryKeySelective(userMoney);
		if(i < 1) {
			log.error("充值失败");
			throw new RuntimeException();
		}
		try {
			experienceService.saveMoneyRecord(userId, "1",money+"", remarks+"充值余额");
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public boolean checkMoney(Integer userId, BigDecimal money) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		if(userMoney.getMoney().compareTo(money) < 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public int consumeMoney(Integer userId, BigDecimal money, String remarks) {
		UserMoney userMoney = moneyMapper.selectByUserId(userId);
		BigDecimal moneys = userMoney.getMoney().subtract(money);
		userMoney.setMoney(moneys);
		int i = moneyMapper.updateByPrimaryKeySelective(userMoney);
		if(i < 1) {
			log.error("消费失败");
			throw new RuntimeException();
		}
		try {
			experienceService.saveMoneyRecord(userId, "2", money+"",remarks);
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
			remarks = "签到赠送闪多豆"+beans+"颗";
		}else {
			remarks = "升级赠送闪多豆"+beans+"颗";
		}
		try {
			experienceService.saveMoneyRecord(userId, "9", beans+"",remarks);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int consumeBeans(Integer userId, Integer beans) {
		UserMoney money = moneyMapper.selectByUserId(userId);
		if(money == null) {
			throw new RuntimeException();
		}
		int beansa = money.getBeans();
		if(beansa < beans) {
			log.error("闪多豆不足");
			throw new RuntimeException();
		}
		money = new UserMoney();
		money.setUserId(userId);
		money.setBeans(beansa - beans);
		int i = moneyMapper.updateByPrimaryKeySelective(money);
		if(i < 1) {
			throw new RuntimeException();
		}
		try {
			experienceService.saveMoneyRecord(userId, "10", beans+"","刷新活动");
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public boolean checkPassword(Integer userId, String password) {
		UserMoney money = moneyMapper.selectByPrimaryKey(userId);
		password = MD5Utils.getInstance().getMD5(password);
		if(!password.equals(money.getPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public int updatePassWord(Integer userId, String password) {
		UserMoney money = new UserMoney();
		money.setUserId(userId);
		money.setPassword(MD5Utils.getInstance().getMD5(password));
		int i = moneyMapper.updateByPrimaryKeySelective(money);
		if(i < 1) {
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updateRefresh() {
		int i = 0;
//		i = moneyMapper.updateRefresh();
//		log.info("重置普通用户刷新次数:"+i+"人");
		i = moneyMapper.updateVipRefresh();
		log.info("重置vip用户刷新次数:"+i+"人");
		i = moneyMapper.updateSVipRefresh();
		log.info("重置svip用户刷新次数:"+i+"人");
		return 1;
	}
	
	@Override
	public int updateRefresh(Integer userId, Integer refresh) {
		UserMoney money = new UserMoney();
		money.setUserId(userId);
		money.setRefresh(refresh);
		int i = moneyMapper.updateByPrimaryKeySelective(money);
		if(i < 1) {
			throw new RuntimeException();
		}
		log.info("刷新次数修改成功");
		return 1;
	}

	@Override
	public int reduceRefresh(Integer userId) {
		UserMoney money = moneyMapper.selectByUserId(userId);
		Integer refresh = money.getRefresh()-1;
		if(refresh < 0) {
			log.error("刷新次数错误");
			throw new RuntimeException();
		}
		money = new UserMoney();
		money.setUserId(userId);
		money.setRefresh(refresh);
		int i = moneyMapper.updateByPrimaryKeySelective(money);
		if(i < 1) {
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int refreshActivity(Integer userId, String typeId, String activityId) {
		if("1".equals(typeId)) {
			reduceRefresh(userId);
		}else {
			consumeBeans(userId, 200);
		}
		try {
			activityService.activityRefresh(activityId);
		} catch (Exception e) {
			log.error("活动刷新失败");
			throw new RuntimeException();
		}
		return 1;
	}

}
