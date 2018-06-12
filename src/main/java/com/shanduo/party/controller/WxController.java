package com.shanduo.party.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.entity.service.TokenInfo;
import com.shanduo.party.service.BindingService;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.service.UserService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 微信公众号绑定登录接口
 * @ClassName: WxController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月11日 下午2:15:31
 *
 */
@Controller
@RequestMapping("wx")
public class WxController {

	private static final Logger log = LoggerFactory.getLogger(WxController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private BindingService bindingService;
	
	/**
	 * 绑定注册
	 * @Title: bingding
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param openId
	 * @param @param unionId
	 * @param @param nickName
	 * @param @param gender
	 * @param @param username
	 * @param @param password
	 * @param @param codes
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "bindingUser", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean bingding(String unionId, String phone,String password, String code) {
		Integer userid = bindingService.selectUserId(unionId, "0");
		if(userid != null) {
			log.error("此账号已绑定");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR, "此账号已绑定");
		}
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.error("手机号格式错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"手机号格式错误");
		}
		if(userService.checkPhone(phone)) {
			log.error("手机号已被注册");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"手机号已被注册");
		}
		if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
			log.error("验证码错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"验证码错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.error("密码格式错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"密码格式错误");
		}
		if(codeService.checkCode(phone, code, "1")) {
			log.error("验证码超时或错误");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"验证码超时或错误");
		}
		userService.saveUser(phone, password);
		TokenInfo tokenInfo = userService.loginUser(phone, password);
		try {
			bindingService.insertSelective(Integer.parseInt(tokenInfo.getUserId()), unionId, "0");
		} catch (Exception e) {
			return new ErrorBean(ErrorCodeConstants.BACKSTAGE_ERROR, "失败");
		}
		return new SuccessBean(tokenInfo);
	}
	
	/**
	 * 登录
	 * @Title: loginUser
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param unionId
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "loginUser", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean loginUser(String unionid) {
		if(StringUtils.isNull(unionid)) {
			log.error("unionId为空");
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"unionId为空");
		}
		Integer userid = bindingService.selectUserId(unionid, "0");
		if(userid == null) {
			log.error("此账户未绑定");
			return new ErrorBean(ErrorCodeConstants.UNBOUND, "未绑定");
		}
		TokenInfo tokenInfo = userService.loginUser(userid);
		if(tokenInfo == null) {
			log.error("登录失败");
			return new ErrorBean(ErrorCodeConstants.LOGIN_FAILURE,"登录失败");
		}
		return new SuccessBean(tokenInfo);
	}
	
	/**
	 * 绑定
	 * @Title: binding
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param unionId
	 * @param @param username
	 * @param @param password
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "binding", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean binding(String unionId, String username, String password) {
		TokenInfo tokenInfo = userService.loginUser(username, password);
		if (null == tokenInfo) {
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR, "账号或密码错误");
		}
		Integer userId = Integer.valueOf(tokenInfo.getUserId());
		if(StringUtils.isNull(unionId)) {
			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR, "unionId为空");
		}
		try {
			bindingService.insertSelective(userId, unionId, "0");
		} catch (Exception e) {
			return new ErrorBean(ErrorCodeConstants.BINDINGS_FAILURE, "失败");
		}
		return new SuccessBean(tokenInfo);
	}
}
