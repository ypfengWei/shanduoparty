package com.shanduo.party.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.common.ErrorCodeConsts;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.entity.service.TokenInfo;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.service.UserService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 用户接口
 * @ClassName: UserController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月14日 下午4:20:00
 *
 */
@Controller
@RequestMapping(value="juser")
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private UserService userService;
	@Autowired
	private CodeService codeService;
	
	/**
	 * 用户注册
	 * @Title: saveUser
	 * @Description: TODO
	 * @param @param request
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @param password 密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "saveuser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveUser(HttpServletRequest request,String phone,String code,String password) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.error("手机号格式错误");
			return new ErrorBean(10002,"手机号格式错误");
		}
		if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
			log.error("验证码错误");
			return new ErrorBean(10002,"验证码错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.error("密码格式错误");
			return new ErrorBean(10002,"密码格式错误");
		}
		if(codeService.checkCode(phone, code, "1")) {
			log.error("验证码超时或错误");
			return new ErrorBean(10002,"验证码超时或错误");
		}
		if(userService.checkPhone(phone)) {
			log.error("手机号已被注册");
			return new ErrorBean(10002,"手机号已被注册");
		}
		try {
			userService.saveUser(phone,password);
		} catch (Exception e) {
			log.error("注册失败");
			return new ErrorBean(10003,"注册失败");
		}
		String token = userService.loginUser(phone, password);
		if(token != null) {
			return new SuccessBean(token);
		}
		return new SuccessBean("注册成功");
	}

	/**
	 * 用户登录
	 * @Title: loginUser
	 * @Description: TODO
	 * @param @param request
	 * @param @param username 手机号或闪多号
	 * @param @param password 密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "loginuser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean loginUser(HttpServletRequest request,String username,String password) {
		if(StringUtils.isNull(username) || PatternUtils.patternUser(username)) {
			log.error("账号格式错误");
			return new ErrorBean(10002,"账号格式错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.error("密码格式错误");
			return new ErrorBean(10002,"密码格式错误");
		}
		String token = userService.loginUser(username, password);
		if(token == null) {
			log.error("账号或密码错误");
			return new ErrorBean(10002,"账号或密码错误");
		}
		return new SuccessBean(token);
	}
	
	/**
	 * 查询用户详细资料
	 * 好友,活动,动态数量,vip,等级
	 * @Title: details
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "details",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean details(HttpServletRequest request,String token) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10002,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		TokenInfo tokens = userService.selectById(token,isUserId);
		if(tokens == null) {
			log.error("获取用户详细错误");
			return new ErrorBean(10002,"获取用户详细错误");
		}
		return new SuccessBean(tokens);
	}
	
	/**
	 * 修改绑定手机号
	 * @Title: updatePhone
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updatephone",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updatePhone(HttpServletRequest request,String token,String phone,String code) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10002,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.error("手机号格式错误");
			return new ErrorBean(10002,"手机号格式错误");
		}
		if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
			log.error("验证码错误");
			return new ErrorBean(10002,"验证码错误");
		}
		if(userService.checkPhone(phone)) {
			log.error("手机号已被注册");
			return new ErrorBean(10002,"手机号已被注册");
		}
		if(codeService.checkCode(phone, code, "2")) {
			log.error("验证码超时或错误");
			return new ErrorBean(10002,"验证码超时或错误");
		}
		try {
			userService.updatePhone(isUserId, phone);
		} catch (Exception e) {
			log.error("修改手机号失败");
			return new ErrorBean(10003,"修改失败");
		}
		return new SuccessBean("修改成功");
	}
	
	/**
	 * 修改登录密码
	 * @Title: updatePassword
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型 1.验证码修改密码,2.原始密码修改密码
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @param password 原始密码
	 * @param @param newPassword 新密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updatepassword",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updatePassword(HttpServletRequest request,String token,String typeId,String phone,
			String code,String password,String newPassword) {
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		if(StringUtils.isNull(newPassword) || PatternUtils.patternPassword(newPassword)) {
			log.error("新密码格式错误");
			return new ErrorBean(10002,"新密码格式错误");
		}
		if("1".equals(typeId)) {
			if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
				log.error("手机号格式错误");
				return new ErrorBean(10002,"手机号格式错误");
			}
			if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
				log.error("验证码错误");
				return new ErrorBean(10002,"验证码错误");
			}
			if(codeService.checkCode(phone, code, "3")) {
				log.error("验证码超时或错误");
				return new ErrorBean(10002,"验证码超时或错误");
			}
			try {
				userService.updatePasswordByPhone(phone, newPassword);
			} catch (Exception e) {
				log.error("修改密码失败");
				return new ErrorBean(10003,"修改失败");
			}
		}else {
			Integer isUserId = baseService.checkUserToken(token);
			if(isUserId == null) {
				log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
				return new ErrorBean(10001,ErrorCodeConsts.USER_TOKEN_PASTDUR);
			}
			if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
				log.error("密码格式错误");
				return new ErrorBean(10002,"密码格式错误");
			}
			if(userService.checkPassword(isUserId, password)) {
				log.error("旧密码错误");
				return new ErrorBean(10002,"旧密码错误");
			}
			try {
				userService.updatePassword(isUserId,newPassword);
			} catch (Exception e) {
				log.error("修改失败");
				return new ErrorBean(10003,"修改失败");
			}
		}
		return new SuccessBean("修改成功");
	}
	
	/**
	 * 修改用户个人信息
	 * @Title: updateUser
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param name 昵称
	 * @param @param picture 头像图片ID
	 * @param @param birthday 生日
	 * @param @param gender 性别:0.女;1.男
	 * @param @param emotion 情感状态:0.保密;1.已婚;2.未婚
	 * @param @param signature 个性签名
	 * @param @param background 背景图片
	 * @param @param hometown 家乡
	 * @param @param occupation 职业
	 * @param @param school 学校
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updateuser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updateUser(HttpServletRequest request,String token,String name,String picture,String birthday,String gender,
			String emotion,String signature,String background,String hometown,String occupation,String school) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10002,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		if(!StringUtils.isNull(birthday) && PatternUtils.patternBirthday(birthday)) {
			log.error("生日错误");
			return new ErrorBean(10002,"生日错误");
		}
		if(!StringUtils.isNull(gender) && !gender.matches("^[01]$")) {
			log.error("性别错误");
			return new ErrorBean(10002,"性别错误");
		}
		if(!StringUtils.isNull(emotion) && !emotion.matches("^[012]$")) {
			log.error("情感状态错误");
			return new ErrorBean(10002,"情感状态错误");
		}
		try {
			userService.updateUser(token,isUserId, name, picture, birthday, 
					gender, emotion, signature, background, hometown, occupation, school);
		} catch (Exception e) {
			log.error("修改失败");
			return new ErrorBean(10003,"修改失败");
		}
		return new SuccessBean("修改成功");
	}
	
	/**
	 * 检查token是否有效
	 * @Title: checkToken
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "checktoken",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean checkToken(HttpServletRequest request,String token) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		return new SuccessBean();
	}
		
}
