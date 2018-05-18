package com.shanduo.party.service;

/**
 * 订单业务层
 * @ClassName: OrderService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月17日 下午3:11:44
 *
 */
public interface OrderService {

	/**
	 * 生成订单
	 * @Title: saveOrder
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param orderType 订单类型
	 * @param @param money 订单金额
	 * @param @param month 开通月份
	 * @param @param activityId 活动ID
	 * @param @return
	 * @return String
	 * @throws
	 */
	String saveOrder(Integer userId,String orderType,String money,Integer month,String activityId);
	
	/**
	 * 余额支付订单
	 * @Title: updateOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateOrder(String orderId,Integer userId);
	
}
