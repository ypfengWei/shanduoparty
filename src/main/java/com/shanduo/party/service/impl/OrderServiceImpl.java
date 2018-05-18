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
	public String saveOrder(Integer userId, String orderType, String money, String month, String activityId) {
		String id = UUIDGenerator.getUUID();
		UserOrder order = new UserOrder();
		order.setId(id);
		order.setUserId(userId);
		order.setOrderType(orderType);
		BigDecimal moneys = new BigDecimal("0");
		if("1".equals(orderType)) {
			order.setMonth(Integer.parseInt(month));
			moneys = new BigDecimal(month+"").multiply(new BigDecimal("8.8"));
			order.setMoney(moneys);
		}else if("2".equals(orderType)) {
			order.setMonth(Integer.parseInt(month));
			moneys = new BigDecimal(month+"").multiply(new BigDecimal("12.8"));
			order.setMoney(moneys);
		}else if("3".equals(orderType)) {
			order.setMoney(new BigDecimal(money));
		}else if("4".equals(orderType)) {
			order.setActivityId(activityId);
			int vip = vipService.selectVipExperience(userId);
			if(vip == 0) {
				order.setMoney(new BigDecimal("0.2"));
			}else if(vip < 11) {
				order.setMoney(new BigDecimal("0.15"));
			}else {
				order.setMoney(new BigDecimal("0.1"));
			}
		}else {
			order.setActivityId(activityId);
			int vip = vipService.selectVipExperience(userId);
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
		return id;
	}

	@Override
	public int updateOrder(String orderId,Integer userId) {
		UserOrder order = orderMapper.selectById(orderId, userId);
		if(order == null) {
			log.error("订单出错:"+orderId);
			throw new RuntimeException();
		}
		BigDecimal money = order.getMoney();
		Integer month = order.getMonth();
		String activityId = order.getActivityId();
		//类型:1.vip,2.svip,3.充值,4.活动刷新,5.活动置顶
		switch (order.getOrderType()) {
			case "1":
				try {
					moneyService.consumeMoney(userId, money, "开通VIP");
					vipService.updateByUserId(userId, month, "0");
				} catch (Exception e) {
					throw new RuntimeException();
				}
				break;
			case "2":
				try {
					moneyService.consumeMoney(userId, money, "开通SVIP");
					vipService.updateByUserId(userId, month, "1");
				} catch (Exception e) {
					throw new RuntimeException();
				}
				break;
//			case "3":
//				try {
//					moneyService.payMoney(userId, money);
//				} catch (Exception e) {
//					throw new RuntimeException();
//				}
//				break;
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
		order = new UserOrder();
		order.setId(orderId);
		order.setStatus("2");
		order.setPaymentType("1");
		int i = orderMapper.updateByPrimaryKeySelective(order);
		if(i < 1) {
			log.error("修改订单失败");
			throw new RuntimeException();
		}
		return 1;
	}

}
