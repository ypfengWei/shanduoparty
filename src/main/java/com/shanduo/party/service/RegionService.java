package com.shanduo.party.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 区域代理业务层
 * @ClassName: RegionService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月4日 上午9:56:03
 *
 */
public interface RegionService {

	/**
	 * 区域代理登录
	 * @Title: loginRegion
	 * @Description: TODO
	 * @param @param account
	 * @param @param password
	 * @param @return
	 * @return String
	 * @throws
	 */
	Map<String, Object> loginRegion(String account,String password);
	
	/**
	 * 区域代理账号修改密码
	 * @Title: updatePassword
	 * @Description: TODO
	 * @param @param userId
	 * @param @param password
	 * @param @param newPassword
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updatePassword(Integer userId,String password,String newPassword);
	
	/**
	 * 当月提成
	 * @Title: selectCurrentMonth
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return Double
	 * @throws
	 */
	BigDecimal selectCurrentMonth(Integer userId);
	
	/**
	 * 查看历史月度提成
	 * @Title: countList
	 * @Description: TODO
	 * @param @param userId
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> countList(Integer userId,Integer pageNum,Integer pageSize);
	
	/**
	 * 每月统计
	 * @Title: monthCount
	 * @Description: TODO
	 * @param @return
	 * @return int
	 * @throws
	 */
	int monthCount();
	
}
