package com.shanduo.party.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.SessionKey;
import com.shanduo.party.entity.UserBinding;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.entity.service.TokenInfo;
import com.shanduo.party.mapper.SessionKeyMapper;
import com.shanduo.party.service.BindingService;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.service.UserService;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;
import com.shanduo.party.util.WXBizDataCrypt;
import net.sf.json.JSONObject;

/**
 * 
 * @author silen 获取微信信息工具类
 */
@Controller
@RequestMapping(value = "wechat")
public class WechatController {
	@Autowired
	private BindingService bindingService;
	@Autowired
	private SessionKeyMapper sessionKeyMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CodeService codeService;

	@ResponseBody
	@RequestMapping(value = "loginWechat")
	public ResultBean loginWechat(String code) {
		List<String> str = new WXBizDataCrypt().loginWechat(code);
		if (StringUtils.isNull(str.get(2))) {
			SessionKey key = new SessionKey();
			key.setId(UUIDGenerator.getUUID());
			key.setOpenId(str.get(0));
			key.setSessionKey(str.get(1));
			sessionKeyMapper.insertSelective(key);
			return new ErrorBean(10087, str.get(0));
		}
		Integer userId = bindingService.selectUserId(str.get(2), "1");
		if (null == userId) {
			String json = "{\"openId\":\"" + str.get(0) + "\",\"unionId\":\"" + str.get(2) + "\"}";
			return new ErrorBean(10086, json);
		}
		TokenInfo TokenInfo = userService.loginUser(userId);
		return new SuccessBean(TokenInfo);
	}

	// 获取信息
	@ResponseBody
	@RequestMapping("getOpenid")
	public ResultBean getOpenid(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 需要使用小程序传过来的js_code获取session_key
		String openId = request.getParameter("openId");
		// 这个就是要解密的东西--用户敏感信息加密数据
		String encryptedData = request.getParameter("encryptedData");
		// 加密算法的初始向量
		String iv = request.getParameter("iv");
		// 调用工具类中获取session_key的方法
		String sessionkey = sessionKeyMapper.selectSessionKey(openId);
		// 调用工具类中的解密方法，然后返回给小程序就OK了
		JSONObject obj = new JSONObject();
		try {
			obj = new WXBizDataCrypt().getUserInfo(encryptedData, sessionkey, iv);
		} catch (Exception e) {
			return new ErrorBean(10003, "失败");
		}
		String openid = obj.getString("openId");
		String unionid = obj.getString("unionId");
		Integer userId = bindingService.selectUserId(unionid, "1");
		if (null == userId) {
			return new ErrorBean(10086, unionid);
		}
		TokenInfo TokenInfo = userService.loginUser(userId);
		return new SuccessBean(TokenInfo);
	}

	@ResponseBody
	@RequestMapping(value = "bindingUser")
	public ResultBean bingding(String openId, String unionId, String nickName, String gender, String phone,
			String password, String codes) {
		if (StringUtils.isNull(codes)) {
			TokenInfo TokenInfo = userService.loginUser(phone, password);
			if (null == TokenInfo) {
				return new ErrorBean(10002, "账号或密码错误");
			}
			Integer userId = Integer.valueOf(TokenInfo.getUserId());
			String type = "1";
			int count = bindingService.insertSelective(userId, openId, unionId, type);
			if (count < 1) {
				return new ErrorBean(10003, "失败");
			}
		}
		if (codeService.selectByQuery(phone, codes, "1")) {
			return new ErrorBean(10002, "验证码错误");
		}
		if (userService.checkPhone(phone)) {
			return new ErrorBean(10002, "该手机号已存在");
		}
		int userId = userService.saveUser(phone, password, nickName, gender);
		String type = "1";
		try {
			int count = bindingService.insertSelective(userId, openId, unionId, type);
			if (count < 1) {
				return new ErrorBean(10003, "失败");
			}
		} catch (Exception e) {
			return new ErrorBean(10003, "失败");
		}

		TokenInfo TokenInfo = userService.loginUser(phone, password);
		return new SuccessBean(TokenInfo);

	}
}
