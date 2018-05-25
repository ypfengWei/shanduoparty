package com.shanduo.party.service;

/**
 * 经验业务层
 * @ClassName: ExperienceService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月9日 上午10:14:43
 *
 */
public interface ExperienceService {
	
	/**
	 * 添加积分或闪多币操作记录
	 * @Title: saveMoneyRecord
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param moneyType 记录类型
	 * @param @param remarks 备注
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveMoneyRecord(Integer userId,String moneyType,String remarks);
	
	/**
	 * 加经验前检查次数是否已受限制
	 * @Title: checkCount
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param moneyType 记录类型
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkCount(Integer userId,String moneyType);
	
	/**
	 * 添加经验值
	 * @Title: addExperience
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param moneyType 记录类型
	 * @param @return
	 * @return int
	 * @throws
	 */
	int addExperience(Integer userId,String moneyType);
	
	/**
	 * 查询周签到天数
	 * @Title: weekSignInCount
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int weekSignInCount(Integer userId);
	
	/**
	 * 每日签到
	 * @Title: signin
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int signin(Integer userId);
	
	/**
     * 查询用户连续签到天数
     * @Title: selectBySignInCount
     * @Description: TODO
     * @param @param userId 用户ID
     * @param @return
     * @return int
     * @throws
     */
	int selectBySignInCount(Integer userId);
	
	/**
	 * 查询经验等级
	 * @Title: selectLevel
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int selectLevel(Integer userId);
}
