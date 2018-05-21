package com.shanduo.party.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.UserMoney;
import com.shanduo.party.entity.UserOrder;
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.service.OrderService;
import com.shanduo.party.service.UserService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 钱包控制层
 * @ClassName: MoneyControllrt
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月3日 下午2:46:21
 *
 */
@Controller
@RequestMapping(value = "jmoney")
public class MoneyControllrt {
	
	private static final Logger log = LoggerFactory.getLogger(MoneyControllrt.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private MoneyService moneyService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	@Autowired
	private CodeService codeService;
	
	/**
	 * 查询用户余额
	 * @Title: getMoney
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "getmoney",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean getMoney(HttpServletRequest request,String token) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		UserMoney money = moneyService.selectByUserId(userId);
		if(money == null) {
			log.error("查询钱包出错");
			return new ErrorBean("查询钱包出错");
		}
		return new SuccessBean(money);
	}
	
	/**
	 * 查询余额历史操作记录
	 * @Title: moneyList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param page 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "moneyList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean moneyList(HttpServletRequest request,String token,String page,String pageSize) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean("页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean("记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = moneyService.moneyList(userId, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 余额支付订单
	 * @Title: expenditure
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param orderId 订单ID
	 * @param @param password 支付密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "expenditure",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean expenditure(HttpServletRequest request, String token,String orderId,String password) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		if(StringUtils.isNull(orderId)) {
			log.error("订单号为空");
			return new ErrorBean("订单号为空");
		}
		UserOrder order = orderService.selectByOrderId(orderId);
		if(order == null) {
			log.error("订单不存在或已支付");
			return new ErrorBean("订单不存在或已支付");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternCode(password)) {
			log.error("密码格式错误");
			return new ErrorBean("密码格式错误");
		}
		if(!moneyService.checkPassword(userId, password)) {
			log.error("支付密码错误");
			return new ErrorBean("密码错误");
		}
		if(moneyService.checkMoney(userId,order.getMoney())) {
			log.error("余额不足");
			return new ErrorBean("余额不足");
		}
		try {
			orderService.updateOrder(orderId);
		} catch (Exception e) {
			log.error("支付失败");
			return new ErrorBean("支付失败");
		}
		return new SuccessBean("支付成功");
	}
	
	/**
	 * 修改支付密码
	 * @Title: updatepassword
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型:1.手机验证码修改支付密码,2.原密码修改支付密码
	 * @param @param code 验证码
	 * @param @param password 原始密码
	 * @param @param newPassword 新密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updatepassword",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updatepassword(HttpServletRequest request,String token,String typeId,String code,String password,String newPassword) {
		UserToken userToken = baseService.checkUserToken(token);
		if(userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Integer userId = userToken.getUserId();
		
		if(StringUtils.isNull(typeId) || typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		if(StringUtils.isNull(newPassword) || PatternUtils.patternCode(newPassword)) {
			log.error("新密码格式错误");
			return new ErrorBean("新密码格式错误");
		}
		if("1".equals(typeId)) {
			String phone = userService.selectByPhone(userId);
			if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
				log.error("验证码错误");
				return new ErrorBean("验证码错误");
			}
			if(codeService.selectByQuery(phone, code, "4")) {
				log.error("验证码超时或错误");
				return new ErrorBean("验证码超时或错误");
			}
		}else {
			if(StringUtils.isNull(password) || PatternUtils.patternCode(password)) {
				log.error("原始密码格式错误");
				return new ErrorBean("原始密码格式错误");
			}
			if(!moneyService.checkPassword(userId, password)) {
				log.error("原始密码错误");
				return new ErrorBean("原始密码错误");
			}
		}
		try {
			moneyService.updatePassWord(userId, newPassword);
		} catch (Exception e) {
			log.error("支付密码修改失败");
			return new ErrorBean("修改失败");
		}
		return new SuccessBean("修改成功");
	}

}
