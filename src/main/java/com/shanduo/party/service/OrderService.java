package com.shanduo.party.service;

import com.shanduo.party.entity.UserOrder;

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
	 * @param @param location 位置
	 * @param @return
	 * @return UserOrder
	 * @throws
	 */
	UserOrder saveOrder(Integer userId,String orderType,String money,String month,String activityId,String location);
	
	/**
	 * 返回订单信息
	 * @Title: selectByOrderId
	 * @Description: TODO
	 * @param @param orderId
	 * @param @return
	 * @return UserOrder
	 * @throws
	 */
	UserOrder selectByOrderId(String orderId);
	
	/**
	 * 余额或赏金支付订单
	 * @Title: updateOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param typeId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateOrder(String orderId,String typeId);
	
	/**
	 * 第三方支付订单
	 * @Title: updateOrders
	 * @Description: TODO
	 * @param @param orderId 订单ID
	 * @param @param payId 类型ID:2.支付宝;3.微信;4.小程序;
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateOrders(String orderId,String payId);
	
}
