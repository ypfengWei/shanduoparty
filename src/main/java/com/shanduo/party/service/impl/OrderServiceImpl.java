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
@Transactional(rollbackFor = Exception.class)
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
		}else if("5".equals(orderType)){
			order.setActivityId(activityId);
			int vip = vipService.selectVipLevel(userId);
			if(vip == 0) {
				order.setMoney(new BigDecimal("2"));
			}else if(vip < 11) {
				order.setMoney(new BigDecimal("1.5"));
			}else {
				order.setMoney(new BigDecimal("1"));
			}
		}else {
			order.setMonth(Integer.parseInt(month));
			moneys = new BigDecimal(month+"").multiply(new BigDecimal("4"));
			order.setMoney(moneys);
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
	public int updateOrder(String orderId,String typeId) {
		UserOrder order = selectByOrderId(orderId);
		if(order == null) {
			throw new RuntimeException();
		}
		Integer userId = order.getUserId();
		BigDecimal money = order.getMoney();
		Integer month = order.getMonth();
		String activityId = order.getActivityId();
		payOrder(orderId, userId, money, month, activityId, order.getOrderType(),"1",typeId);
		return 1;
	}

	@Override
	public int updateOrders(String orderId,String payId) {
		pay(orderId, payId);
		return 1;
	}
	
	/**
	 * 支付订单
	 * @Title: pay
	 * @Description: TODO
	 * @param @param orderId 订单ID
	 * @param @param payId 1.余额或赏金,2.支付宝,3.微信,4.小程序,5.公众号
	 * @return void
	 * @throws
	 */
	public void pay(String orderId,String payId) {
		UserOrder order = selectByOrderId(orderId);
		if(order == null) {
			throw new RuntimeException();
		}
		Integer userId = order.getUserId();
		BigDecimal money = order.getMoney();
		Integer month = order.getMonth();
		String activityId = order.getActivityId();
		String remarks = "";
		switch (payId) {
			case "1":
				remarks = "支付宝";
				break;
			case "2":
				remarks = "微信";
				break;
			case "3":
				remarks = "小程序";
				break;
			case "4":
				remarks = "公众号";
				break;
			default:
				break;
		}
		moneyService.payMoney(userId, money, remarks);
		payOrder(orderId, userId, money, month, activityId, order.getOrderType(),payId,"1");
	}
	
	/**
	 * 支付订单
	 * @Title: payOrder
	 * @Description: TODO
	 * @param @param orderId 订单ID
	 * @param @param userId 用户ID
	 * @param @param money 订单金额
	 * @param @param month 月份
	 * @param @param activityId 活动ID
	 * @param @param orderType 订单类型
	 * @param @param payId 支付类型ID
	 * @param @param typeId 币种类型:1.余额,2.赏金
	 * @return void
	 * @throws
	 */
	public void payOrder(String orderId,Integer userId,BigDecimal money,Integer month,String activityId,String orderType,String payId,String typeId) {
		//1.充值,2.vip,3.svip,4.活动刷新,5.活动置顶
		switch (orderType) {
			case "2":
				moneyService.consumeMoney(userId, money, "开通"+month+"个月VIP",typeId);
				vipService.updateVip(userId, month, "0");
				break;
			case "3":
				moneyService.consumeMoney(userId, money, "开通"+month+"个月SVIP",typeId);
				vipService.updateVip(userId, month, "1");
				break;
			case "4":
				moneyService.consumeMoney(userId, money, "活动刷新",typeId);
				activityService.activityRefresh(activityId);
				break;
			case "5":
				moneyService.consumeMoney(userId, money, "活动置顶",typeId);
				activityService.updateBysetTop(activityId);
				break;
			case "6":
				moneyService.consumeMoney(userId, money, "升级"+month+"个月SVIP",typeId);
				vipService.upgradeVip(userId, month);
				break;
			default:
				break;
		}
		//修改订单状态
		UserOrder order = new UserOrder();
		order.setId(orderId);
		order.setStatus("2");
		order.setPaymentType(payId);
		int i = orderMapper.updateByPrimaryKeySelective(order);
		if(i < 1) {
			log.error("修改订单失败");
			throw new RuntimeException();
		}
	}
}
