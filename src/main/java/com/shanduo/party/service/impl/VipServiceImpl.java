package com.shanduo.party.service.impl;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ShanduoVip;
import com.shanduo.party.entity.VipExperience;
import com.shanduo.party.mapper.ShanduoVipMapper;
import com.shanduo.party.mapper.VipExperienceMapper;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.UUIDGenerator;
import com.shanduo.party.xg.XGHighUtils;

/**
 * 
 * @ClassName: VipServiceImpl
 * @Description: TODO
 * @author lishan
 * @date 2018年5月18日 下午4:19:30
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipServiceImpl implements VipService {

	private static final Logger log = LoggerFactory.getLogger(VipServiceImpl.class);

	@Autowired
	private ShanduoVipMapper vipMapper;
	@Autowired
	private VipExperienceMapper experienceMapper;
	@Autowired
	private MoneyService moneyService;

	@Override
	public int saveVip(Integer userId, Date date,Integer month,String vipType,String isRefresh) {
		ShanduoVip userVip = vipMapper.selectByVipType(userId,vipType);
		Date endDate = getDate(date, month);
		if(userVip == null) {
			userVip = new ShanduoVip();
			userVip.setId(UUIDGenerator.getUUID());
			userVip.setUserId(userId);
			userVip.setVipType(vipType);
			userVip.setVipStartTime(date);
			userVip.setVipEndTime(endDate);
			int i = vipMapper.insertSelective(userVip);
			if (i < 1) {
				log.error("开通会员失败");
				throw new RuntimeException();
			}
		}else {
			userVip.setVipStartTime(date);
			userVip.setVipEndTime(endDate);
			int i = vipMapper.updateByPrimaryKeySelective(userVip);
			if (i < 1) {
				log.error("重新开通会员失败");
				throw new RuntimeException();
			}
		}
		if("0".equals(isRefresh)) {
			//修改刷新次数
			if("0".equals(vipType)) {
				moneyService.updateRefresh(userId, 50);
			}else {
				moneyService.updateRefresh(userId, 100);
			}
		}
		//添加成长值
		VipExperience vip = experienceMapper.selectByPrimaryKey(userId);
		if(vip == null) {
			vip = new VipExperience();
			vip.setUserId(userId);
			int count = experienceMapper.insertSelective(vip);
			if (count < 1) {
				log.error("成长值添加失败");
				throw new RuntimeException();
			}
		}
		XGHighUtils.getInstance().pushSingleAccount("ShanDuo", "恭喜你成功开通VIP", userId,1);
		return 1;
	}
	
	/**
	 * 开通svip时保存vip没用完的刷新次数
	 * @Title: addRemarks
	 * @Description: TODO
	 * @param @param userId
	 * @return void
	 * @throws
	 */
	public void addRemarks(Integer userId) {
		Map<String, Object> resultMap = moneyService.selectByUserId(userId);
		VipExperience vip = new VipExperience();
		vip.setUserId(userId);
		vip.setRemarks(resultMap.get("refresh").toString());
		int i = experienceMapper.updateByPrimaryKeySelective(vip);
		if (i < 1) {
			log.error("添加备注刷新次数失败");
			throw new RuntimeException();
		}
	}
	
	/**
	 * 续费vip
	 * @Title: renewVip
	 * @Description: TODO
	 * @param @param vipId
	 * @param @param date 续费的开始时间
	 * @param @param month 续费月份
	 * @return void
	 * @throws
	 */
	public void renewVip(String vipId, Date date, Integer month) {
		ShanduoVip userVip = new ShanduoVip();
		userVip.setId(vipId);
		userVip.setVipEndTime(new Date(date.getTime() + 1000L * 60L * 60L * 24L * 31L * month));
		int i = vipMapper.updateByPrimaryKeySelective(userVip);
		if (i < 1) {
			log.error("续费会员失败");
			throw new RuntimeException();
		}
	}
	
	@Override
	public int updateVip(Integer userId, Integer month, String vipType) {
		Date date = new Date();
		List<ShanduoVip> resultList = vipMapper.selectByUserId(userId);
		if(resultList == null || resultList.isEmpty()) {
			//开通
			saveVip(userId, date, month, vipType,"0");
		}else if(resultList.size() == 2) {
			ShanduoVip vip1 = resultList.get(0);
			ShanduoVip vip2 = resultList.get(1);
			if("0".equals(vipType)) {
				//续费vip
				if("0".equals(vip1.getVipType())) {
					renewVip(vip1.getId(), vip1.getVipEndTime(), month);
				}else {
					renewVip(vip2.getId(), vip2.getVipEndTime(), month);
				}
			}else {
				addRemarks(userId);
				//续费svip和vip延后
				renewVip(vip1.getId(), vip1.getVipEndTime(), month);
				renewVip(vip2.getId(), vip2.getVipEndTime(), month);
			}
		}else {
			ShanduoVip vip = resultList.get(0);
			if(vipType.equals(vip.getVipType())) {
				//续费
				renewVip(vip.getId(), vip.getVipEndTime(), month);
			}else {
				if("0".equals(vipType)) {
					//开通vip
					saveVip(userId, vip.getVipEndTime(), month, vipType,"1");
				}else {
					addRemarks(userId);
					//开通svip
					saveVip(userId, date, month, vipType,"0");
					//vip延长
					renewVip(vip.getId(), vip.getVipEndTime(), month);
				}
			}
		}
		return 1;
	}
	
	/**
	 * 计算vip等级
	 * @Title: getVipLevel
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int getVipLevel(Integer userId) {
		int experience = experienceMapper.selectByUserId(userId);
		if(experience < 300) {
			return 1;
		}else if(experience < 900) {
			return 2;
		}else if(experience < 1800) {
			return 3;
		}else if(experience < 3600) {
			return 4;
		}else if(experience < 7200) {
			return 5;
		}else if(experience < 14400) {
			return 6;
		}else if(experience < 28800) {
			return 7;
		}
		return 8;
	}
	
	@Override
	public int selectVipLevel(Integer userId) {
		List<ShanduoVip> resultList = vipMapper.selectByUserId(userId);
		if(resultList == null || resultList.isEmpty()) {
			return 0;
		}
		if(resultList.size() == 2) { //svip
			return 10+getVipLevel(userId);
		}
		if("0".equals(resultList.get(0).getVipType())) { //vip
			return getVipLevel(userId);
		}
		return 10+getVipLevel(userId); //svip
	}
	
	@Override
	public int getMonth(Integer userId) {
		List<ShanduoVip> resultList = vipMapper.selectByUserId(userId);
		int i = 0;
		if(resultList != null && resultList.size() < 2) { //vip
			if("0".equals(resultList.get(0).getVipType())) {
				ShanduoVip vip = resultList.get(0);
				Date date = new Date();
				long vipEndTime = vip.getVipEndTime().getTime();
				long time = vipEndTime - date.getTime();
				long b = 1000L*60*60*24*31;
				if(1000L*60*60*24*16 > time) { //vip剩余时间不足16天不能升级
					i = 0;
				} else {
					i = (int) (time/b); 
					if(i >= 1 && time%b>= 1000L*60*60*24*16) {
						i = i+1;
					}
					if(i == 0) {
						i = i+1;
					}
				}
			}
		}
		return i; //i=0:会员剩余时长不足16天或用户为svip不能升级
	}
	
	@Override
	public int upgradeVip(Integer userId, Integer month, String vipType) {
		if(getMonth(userId) > 0) {
			List<ShanduoVip> resultList = vipMapper.selectByUserId(userId);
			if(resultList != null && resultList.size() < 2) {
				if("0".equals(resultList.get(0).getVipType())) { //用户为vip
					ShanduoVip vip = resultList.get(0);
					Date date = new Date();
					long vipEndTime = vip.getVipEndTime().getTime();
					long time = vipEndTime - date.getTime();
					if(1000L*60*60*24*31*month - 1000L*60*60*24*15 <= time) { //会员剩余时间在允许升级的月份之内
						if(1000L*60*60*24*31*month > time) { //升级时间大于会员结束时间将vip延迟
							//vip延长
							renewVip(vip.getId(), getDate(date, month), month);
						} 
						addRemarks(userId);
						//开通svip
						saveVip(userId, date, month, vipType,"0");
						return 1; //操作成功
					}
				}
			}
		}
		return 0;//操作失败：高于可升级月份的限制或会员剩余时长不足16天或用户为svip不能升级
	}
	
	public static Date getDate(Date date, Integer month) {
		Format format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String vipEndDate = format.format(date.getTime() + 1000L * 60L * 60L * 24L * 31L * month);
		DateFormat formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date endDate = null;
        try {
			endDate = formats.parse(vipEndDate);
		} catch (ParseException e) {
			throw new RuntimeException();
		}
		return endDate;
	}
}
