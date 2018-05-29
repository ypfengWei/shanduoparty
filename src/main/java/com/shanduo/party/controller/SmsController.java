package com.shanduo.party.controller;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.SmsUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 发送验证码控制层
 * @ClassName: SmsController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月14日 上午11:08:00
 *
 */
@Controller
@RequestMapping(value = "sms")
public class SmsController {
	
	private static final Logger log = LoggerFactory.getLogger(SmsController.class);
	
	@Autowired
	private CodeService codeService;
	
	/**
	 * 发送多种类型短信接口
	 * @Title: envoyer
	 * @Description: TODO
	 * @param @param request
	 * @param @param phone 手机号码
	 * @param @param typeId 短信类型ID 1.注册;2.换手机号;3.修改密码;4.修改支付密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "envoyer",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean envoyer(HttpServletRequest request,String phone,String typeId) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.error("手机号格式错误");
			return new ErrorBean(10002,"手机号格式错误");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[1234]$")) {
			log.error("类型错误");
			return new ErrorBean(10002,"类型错误");
		}
		if(codeService.selectByPhone(phone)) {
			log.error("60S内只能发送一次");
			return new ErrorBean(10002,"60S内只能发送一次");
		}
		int code = new Random().nextInt(900000)+100000;
		SendSmsResponse sendSmsResponse = null;
		try {
			sendSmsResponse = SmsUtils.sendSms(phone,code+"",typeId);
		} catch (ClientException e) {
			log.error("发送短信验证码错误");
			return new ErrorBean(10002,"发送失败");
		}
		if(sendSmsResponse.getCode() == null || !"OK".equals(sendSmsResponse.getCode())) {
			log.error(sendSmsResponse.getMessage());
			return new ErrorBean(10002,"发送失败");
		}
		try {
			codeService.savePhoneVerifyCode(phone, code+"", typeId);
		} catch (Exception e) {
			log.error("发送失败");
			return new ErrorBean(10002,"发送失败");
		}
		return new SuccessBean("发送成功");
	}

}
