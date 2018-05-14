package com.shanduo.party.service.impl;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.controller.ActivityController;
import com.shanduo.party.entity.ShanduoVip;
import com.shanduo.party.entity.VipExperience;
import com.shanduo.party.mapper.ShanduoVipMapper;
import com.shanduo.party.mapper.VipExperienceMapper;
import com.shanduo.party.service.VipService;

@Service
@Transactional(rollbackFor = Exception.class)
public class VipServiceImpl implements VipService {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ShanduoVipMapper shanduoVipMapper;

	@Autowired
	private VipExperienceMapper vipExperienceMapper;

	@Override
	public int insertSelective(Integer userId, String vipType, Integer month) {
		ShanduoVip shanduoVip = new ShanduoVip();
		shanduoVip.setUserId(userId);
		shanduoVip.setVipType(vipType);
		shanduoVip.setVipStartTime(new Date());
		long time = System.currentTimeMillis();
		shanduoVip.setVipEndTime(new Date(time + 1000L * 60L * 60L * 24L * 31L * month));
		int i = shanduoVipMapper.insertSelective(shanduoVip);
		if (i <= 0) {
			log.error("会员添加失败");
			throw new RuntimeException();
		}
		VipExperience vip = vipExperienceMapper.selectByPrimaryKey(userId);
		if(vip == null) {
			vip = new VipExperience();
			vip.setUserId(userId);
			int count = vipExperienceMapper.insertSelective(vip);
			if (count <= 0) {
				log.error("成长值添加失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	public void updateVip(ShanduoVip vip,Integer month,long time,String type) {
		Date date = new Date(time);
		if("1".equals(type)) {
			vip.setVipStartTime(date);
		}
		vip.setVipEndTime(new Date(time + 1000L * 60L * 60L * 24L * 31L * month));
		int i = shanduoVipMapper.updateByPrimaryKeySelective(vip);
		if (i <= 0) {
			log.error("会员开通失败");
			throw new RuntimeException();
		}
	}
	
	@Override
	public int updateByUserId(Integer userId, Integer month, String vipType) {
		long longtime = System.currentTimeMillis();
		ShanduoVip vip = shanduoVipMapper.selectUserIdAndType(userId,"0");
		ShanduoVip svip = shanduoVipMapper.selectUserIdAndType(userId,"1");
		if(vipType.equals("0")) {
			if(vip != null) {
				if(svip != null) {
					if(svip.getVipEndTime().getTime() < longtime && vip.getVipEndTime().getTime() < longtime) {
						//两个vip都过期
						updateVip(vip, month, longtime,"1");
					} else if(vip.getVipEndTime().getTime() > longtime) {
						//svip过期
						updateVip(vip, month, vip.getVipEndTime().getTime(),"0");
					} else {
						//vip过期
						updateVip(vip, month, vip.getVipEndTime().getTime(),"0");
					}
				} else {
					if(vip.getVipEndTime().getTime() < longtime) {
						updateVip(vip, month, longtime,"1");
					} else {
						updateVip(vip, month, vip.getVipEndTime().getTime(),"0");
					}
				}
			} else {
				int i = insertSelective(userId, vipType, month);
				if(i < 1) {
					log.error("添加会员失败");
					throw new RuntimeException();
				}
			}
		} else {
			if(svip != null) {
				if(vip != null) {
					if(svip.getVipEndTime().getTime() < longtime && vip.getVipEndTime().getTime() < longtime) {
						//两个vip都过期
						updateVip(svip, month, longtime,"1");
					} else if(svip.getVipEndTime().getTime() > longtime) {
						//svip过期
						updateVip(svip, month, svip.getVipEndTime().getTime(),"0");
						updateVip(vip, month, vip.getVipEndTime().getTime(),"0");
					}
				} else {
					if(svip.getVipEndTime().getTime() < longtime) {
						updateVip(svip, month, longtime,"1");
					} else {
						updateVip(svip, month, svip.getVipEndTime().getTime(),"0");
					}
				}
			} else {
				int i = insertSelective(userId, vipType, month);
				if(i < 1) {
					log.error("添加会员失败");
					throw new RuntimeException();
				}
				if(vip != null) {
					updateVip(vip, month, vip.getVipEndTime().getTime(),"0");
				}
			}
		}
		return 1;
	}
}
