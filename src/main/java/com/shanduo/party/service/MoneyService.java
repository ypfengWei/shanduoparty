package com.shanduo.party.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 余额-闪多豆-刷新次数业务层
 * @ClassName: MoneyService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月25日 上午11:24:37
 *
 */
public interface MoneyService {
	
	/**
	 * 查询用户钱包的信息
	 * @Title: selectByUserId
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @return
	 * @return Map<String, Object>
	 * @throws
	 */
	Map<String, Object> selectByUserId(Integer userId);
	
	/**
	 * 查询用户钱包的历史记录
	 * @Title: moneyList
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param pageNum 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> moneyList(Integer userId,Integer pageNum,Integer pageSize);
	
	/**
	 * 充值余额
	 * @Title: payMoney
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param money 充值金额
	 * @param @param remarks 备注：充值方式等
	 * @param @return
	 * @return int
	 * @throws
	 */
	int payMoney(Integer userId,BigDecimal money,String remarks);
	
	/**
	 * 消费闪多币前检查余额是否足够支付
	 * @Title: checkMoney
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param money 消费金额
	 * @param @param typeId 币种类型:1.余额,2.赏金
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkMoney(Integer userId,BigDecimal money,String typeId);
	
	/**
	 * 消费余额
	 * @Title: consumeMoney
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param money 消费金额
	 * @param @param remarks 备注：消费方式等
	 * @param @param typeId 币种类型:1.余额,2.赏金
	 * @param @return
	 * @return int
	 * @throws
	 */
	int consumeMoney(Integer userId,BigDecimal money,String remarks,String typeId);
	
	/**
	 * 赠送闪多豆
	 * @Title: payBeans
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param beans 闪多豆数量
	 * @param @param typeId 1.签到;2.升级
	 * @param @return
	 * @return int
	 * @throws
	 */
	int payBeans(Integer userId,Integer beans,String typeId);
	
	/**
	 * 闪多豆装换余额:1000:1
	 * @Title: switchBeans
	 * @Description: TODO
	 * @param @return
	 * @return int
	 * @throws
	 */
	int switchBeans();
	
	/**
	 * 检查支付密码
	 * @Title: checkPassword
	 * @Description: TODO
	 * @param @param userId
	 * @param @param password
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkPassword(Integer userId,String password);
	
	/**
	 * 修改支付密码
	 * @Title: updatePassWord
	 * @Description: TODO
	 * @param @param userId
	 * @param @param password
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updatePassWord(Integer userId,String password);
	
	/**
	 * 重置刷新次数
	 * @Title: updateRefresh
	 * @Description: TODO
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateRefresh();
	
	/**
	 * 修改刷新次数，开通vip，svip使用
	 * @Title: updateRefresh
	 * @Description: TODO
	 * @param @param userId
	 * @param @param refresh
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateRefresh(Integer userId,Integer refresh);
	
	/**
	 * 减少1次刷新次数
	 * @Title: reduceRefresh
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int reduceRefresh(Integer userId);
	
	/**
	 * 刷新活动
	 * @Title: refreshActivity
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param activityId 活动ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int refreshActivity(Integer userId,String activityId);
}
