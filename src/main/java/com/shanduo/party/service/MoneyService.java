package com.shanduo.party.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 闪多币业务层
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
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkMoney(Integer userId,BigDecimal money);
	
	/**
	 * 消费余额
	 * @Title: consumeMoney
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param money 消费金额
	 * @param @param remarks 备注：消费方式等
	 * @param @return
	 * @return int
	 * @throws
	 */
	int consumeMoney(Integer userId,BigDecimal money,String remarks);
	
	/**
	 * 赠送闪多豆
	 * @Title: payBeans
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param beans 闪多豆数量
	 * @param @param typeId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int payBeans(Integer userId,Integer beans,String typeId);
	
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
	
	
}
