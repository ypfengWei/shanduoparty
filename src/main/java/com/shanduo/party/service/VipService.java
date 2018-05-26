package com.shanduo.party.service;

import java.util.Date;

/**
 * vip业务层
 * @ClassName: VipService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月18日 下午4:20:43
 *
 */
public interface VipService {
	
	/**
	 * 开通会员
	 * @Title: saveVip
	 * @Description: TODO
	 * @param @param userId
	 * @param @param date 开始时间
	 * @param @param month 月份
	 * @param @param vipType 类型:0.vip;1.svip
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveVip(Integer userId, Date date,Integer month,String vipType);
	
	/**
	 * 开通/续费会员
	 * @Title: updateVip
	 * @Description: TODO
	 * @param @param userId
	 * @param @param month
	 * @param @param vipType 类型:0.vip;1.svip
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateVip(Integer userId, Integer month, String vipType);

	/**
	 * 查会员等级
	 * @Title: selectVipLevel
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int selectVipLevel(Integer userId);
	
}
