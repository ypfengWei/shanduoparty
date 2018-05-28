package com.shanduo.party.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserOrder;
import com.shanduo.party.mapper.UserOrderMapper;
import com.shanduo.party.service.ActivityService;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.service.OrderService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.UUIDGenerator;


/**
 * 
 * @ClassName: OrderServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月17日 下午3:15:08
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired
	private UserOrderMapper orderMapper;
	@Autowired
	private MoneyService moneyService;
	@Autowired
	private VipService vipService;
	@Autowired
	private ActivityService activityService;
	
	@Override
	public UserOrder saveOrder(Integer userId, String orderType, String money, String month, String activityId,String location) {
		UserOrder order = new UserOrder();
		order.setId(UUIDGenerator.getUUID());
		order.setUserId(userId);
		order.setOrderType(orderType);
		order.setLocation(location);
		BigDecimal moneys = new BigDecimal("0");
		if("1".equals(orderType)) {
			order.setMoney(new BigDecimal(money));
		}else if("2".equals(orderType)) {
			order.setMonth(Integer.parseInt(month));
			moneys = new BigDecimal(month+"").multiply(new BigDecimal("8.8"));
			order.setMoney(moneys);
		}else if("3".equals(orderType)) {
			order.setMonth(Integer.parseInt(month));
			moneys = new BigDecimal(month+"").multiply(new BigDecimal("12.8"));
			order.setMoney(moneys);
		}else if("4".equals(orderType)) {
			order.setActivityId(activityId);
			int vip = vipService.selectVipLevel(userId);
			if(vip == 0) {
				order.setMoney(new BigDecimal("0.2"));
			}else if(vip < 11) {
				order.setMoney(new BigDecimal("0.15"));
			}else {
				order.setMoney(new BigDecimal("0.1"));
			}
		}else {
			order.setActivityId(activityId);
			int vip = vipService.selectVipLevel(userId);
			if(vip == 0) {
				order.setMoney(new BigDecimal("2"));
			}else if(vip < 11) {
				order.setMoney(new BigDecimal("1.5"));
			}else {
				order.setMoney(new BigDecimal("1"));
			}
		}
		int i = orderMapper.insertSelective(order);
		if(i < 1) {
			log.error("生成订单失败");
			throw new RuntimeException();
		}
		return order;
	}

	@Override
	public UserOrder selectByOrderId(String orderId) {
		UserOrder order = orderMapper.selectById(orderId);
		if(order == null) {
			return null;
		}
		return order;
	}
	
	@Override
	public int updateOrder(String orderId) {
		UserOrder order = selectByOrderId(orderId);
		if(order == null) {
			throw new RuntimeException();
		}
		Integer userId = order.getUserId();
		BigDecimal money = order.getMoney();
		Integer month = order.getMonth();
		String activityId = order.getActivityId();
		payOrder(orderId, userId, money, month, activityId, order.getOrderType());
		return 1;
	}

	@Override
	public int zfbUpdateOrder(String orderId) {
		pay(orderId, "2");
		return 1;
	}
	
	@Override
	public int wxUpdateOrder(String orderId) {
		pay(orderId, "3");
		return 1;
	}
	
	/**
	 * 第三方支付订单
	 * @Title: pay
	 * @Description: TODO
	 * @param @param orderId 订单ID
	 * @param @param type 1.余额,2.支付宝,3.微信,4.小程序
	 * @return void
	 * @throws
	 */
	public void pay(String orderId,String type) {
		UserOrder order = selectByOrderId(orderId);
		if(order == null) {
			throw new RuntimeException();
		}
		Integer userId = order.getUserId();
		BigDecimal money = order.getMoney();
		Integer month = order.getMonth();
		String activityId = order.getActivityId();
		String remarks = "";
		if("2".equals(type)) {
			remarks = "支付宝";
		}else if("3".equals(type)) {
			remarks = "微信";
		}else {
			remarks = "小程序";
		}
		try {
			moneyService.payMoney(userId, money, remarks);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		payOrder(orderId, userId, money, month, activityId, order.getOrderType());
	}
	
	/**
	 * 支付订单
	 * @Title: payOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param userId
	 * @param @param money
	 * @param @param month
	 * @param @param activityId
	 * @param @param type
	 * @return void
	 * @throws
	 */
	public void payOrder(String orderId,Integer userId,BigDecimal money,Integer month,String activityId,String type) {
		//1.充值,2.vip,3.svip,4.活动刷新,5.活动置顶
		switch (type) {
			case "2":
				try {
					moneyService.consumeMoney(userId, money, "开通VIP");
					vipService.updateVip(userId, month, "0");
				} catch (Exception e) {
					throw new RuntimeException();
				}
				break;
			case "3":
				try {
					moneyService.consumeMoney(userId, money, "开通SVIP");
					vipService.updateVip(userId, month, "1");
				} catch (Exception e) {
					throw new RuntimeException();
				}
				break;
			case "4":
				try {
					moneyService.consumeMoney(userId, money, "活动刷新");
					activityService.activityRefresh(activityId);
				} catch (Exception e) {
					throw new RuntimeException();
				}
				break;
			case "5":
				try {
					moneyService.consumeMoney(userId, money, "活动置顶");
					activityService.updateBysetTop(activityId);
				} catch (Exception e) {
					throw new RuntimeException();
				}
				break;
		}
		//修改订单状态
		UserOrder order = new UserOrder();
		order.setId(orderId);
		order.setStatus("2");
		order.setPaymentType(type);
		int i = orderMapper.updateByPrimaryKeySelective(order);
		if(i < 1) {
			log.error("修改订单失败");
			throw new RuntimeException();
		}
	}
}
