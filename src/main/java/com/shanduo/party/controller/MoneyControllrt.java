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
import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.service.MoneyService;
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
	private UserService userService;
	@Autowired
	private CodeService codeService;
	
	/**
	 * 查询钱包信息
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
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		Map<String, Object> resultMap = moneyService.selectByUserId(isUserId);
		if(resultMap == null) {
			log.error("查询钱包出错");
			return new ErrorBean(10002,"查询钱包出错");
		}
		return new SuccessBean(resultMap);
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
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(10002,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(10002,"记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = moneyService.moneyList(isUserId, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 检查支付密码
	 * @Title: checkPassword
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param password
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "checkpassword",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean checkPassword(HttpServletRequest request,String token,String password) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(password) || PatternUtils.patternCode(password)) {
			log.error("密码格式错误");
			return new ErrorBean(10002,"密码格式错误");
		}
		if(moneyService.checkPassword(isUserId, password)) {
			log.error("密码错误");
			return new ErrorBean(10002,"密码错误");
		}
		return new SuccessBean("密码正确");
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
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		if(StringUtils.isNull(newPassword) || PatternUtils.patternCode(newPassword)) {
			log.error("新密码格式错误");
			return new ErrorBean(10002,"新密码格式错误");
		}
		if("1".equals(typeId)) {
			ShanduoUser user = userService.selectByUserId(isUserId);
			if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
				log.error("验证码错误");
				return new ErrorBean(10002,"验证码错误");
			}
			if(codeService.selectByQuery(user.getPhoneNumber(), code, "4")) {
				log.error("验证码超时或错误");
				return new ErrorBean(10002,"验证码超时或错误");
			}
		}else {
			if(StringUtils.isNull(password) || PatternUtils.patternCode(password)) {
				log.error("原始密码格式错误");
				return new ErrorBean(10002,"原始密码格式错误");
			}
			if(moneyService.checkPassword(isUserId, password)) {
				log.error("原始密码错误");
				return new ErrorBean(10002,"原始密码错误");
			}
		}
		try {
			moneyService.updatePassWord(isUserId, newPassword);
		} catch (Exception e) {
			log.error("支付密码修改失败");
			return new ErrorBean(10003,"修改失败");
		}
		return new SuccessBean("修改成功");
	}

	/**
	 * 刷新活动
	 * @Title: refreshActivity
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型:1.刷新次数刷新;2.闪多豆刷新
	 * @param @param activityId 活动ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "refreshactivity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean refreshActivity(HttpServletRequest request,String token,String typeId,String activityId) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]")) {
			log.error("类型错误");
			return new ErrorBean(1002, "类型错误");
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean(1002, "活动ID为空");
		}
		Map<String, Object> resultMap = moneyService.selectByUserId(isUserId);
		if("1".equals(typeId)) {
			int refresh = (int) resultMap.get("refresh");
			if(refresh < 1) {
				log.error("刷新次数已使用完毕");
				return new ErrorBean(10002,"刷新次数已使用完毕");
			}
		}else {
			int beans = (int) resultMap.get("beans");
			if(beans < 200) {
				log.error("闪多豆不足");
				return new ErrorBean(10002,"闪多豆不足");
			}
		}
		try {
			moneyService.refreshActivity(isUserId, typeId, activityId);
		} catch (Exception e) {
			log.error("活动刷新失败");
			return new ErrorBean(10003,"刷新失败");
		}
		return new SuccessBean("刷新成功");
	}
}
