package com.shanduo.party.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.WechatService;
import com.shanduo.party.util.StringUtils;

@Controller
@RequestMapping(value = "wechat")
public class WechatController {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private WechatService wechatService;
	
	@RequestMapping(value = "saveWechat", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean saveWechat(HttpServletRequest request,String code) {
		if(StringUtils.isNull(code)) {
			log.error("code不能为空");
			return new ErrorBean("code不能为空");
		}
		if(wechatService.selectByPrimaryKey(null, null, code)) {
			try {
				wechatService.insertSelective(null, null, code);
			} catch (Exception e) {
				log.error("绑定失败");
				return new ErrorBean("绑定失败");
			}
		} else {
			try {
				wechatService.selectByUserId(null, null, code);
			} catch (Exception e) {
				log.error("登录失败");
				return new ErrorBean("登录失败");
			}
		}
		return new SuccessBean("操作成功");
	}
}
