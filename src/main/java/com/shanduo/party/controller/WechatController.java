package com.shanduo.party.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.SessionKey;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.mapper.SessionKeyMapper;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.BindingService;
import com.shanduo.party.service.CodeService;
import com.shanduo.party.service.UserService;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;
import com.shanduo.party.util.WXBizDataCrypt;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName: WechatController
 * @Description: TODO
 * @author fangwei
 * @date 2018年5月31日 上午9:51:38
 *
 */
@Controller
@RequestMapping(value = "wechat")
public class WechatController {
	private static final Logger log = LoggerFactory.getLogger(WechatController.class);
	
	@Autowired
	private BindingService bindingService;
	@Autowired
	private SessionKeyMapper sessionKeyMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CodeService codeService;
	@Autowired
	private BaseService baseService;

	@RequestMapping(value = "loginWechat", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResultBean loginWechat(String code) {
		if(StringUtils.isNull(code)) {
			log.error("code不能为空");
			return new ErrorBean(10002,"code不能为空");
		}
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
		String tokenInfo = userService.loginUser(userId);
		if(tokenInfo == null) {
			log.error("登录失败");
			return new ErrorBean(10002,"登录失败");
		}
		return new SuccessBean(tokenInfo);
	}

	// 获取信息
	@RequestMapping(value="getOpenid", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
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
			String json = "{\"openId\":\"" + openid + "\",\"unionId\":\"" + unionid + "\"}";
			return new ErrorBean(10086, json);
		}
		String tokenInfo = userService.loginUser(userId);
		if(tokenInfo == null) {
			log.error("登录失败");
			return new ErrorBean(10002,"登录失败");
		}
		return new SuccessBean(tokenInfo);
	}

	@RequestMapping(value = "bindingUser", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean bingding(String openId, String unionId, String nickName, String gender, String username,
			String password, String codes) {
		//通过unionId去绑定表查记录，如果有，就不能绑定
		Integer userid = bindingService.selectUserId(unionId, "1");
		if(userid != null) {
			return new ErrorBean(10002, "此账号已绑定");
		}
		if (StringUtils.isNull(codes)) {
			String tokenInfo = userService.loginUser(username, password);
			if (null == tokenInfo) {
				return new ErrorBean(10002, "账号或密码错误");
			}
			Integer userId = baseService.checkUserToken(tokenInfo);
			String type = "1";
			if(StringUtils.isNull(openId) || StringUtils.isNull(unionId)) {
				return new ErrorBean(10002, "openId或unionId为空");
			}
			try {
				bindingService.insertSelective(userId, unionId, type);
			} catch (Exception e) {
				return new ErrorBean(10003, "失败");
			}
			return new SuccessBean(tokenInfo);
		}
		if (codeService.checkCode(username, codes, "1")) {
			return new ErrorBean(10002, "验证码错误");
		}
		if (userService.checkPhone(username)) {
			return new ErrorBean(10002, "该手机号已存在");
		}
		int userId = userService.saveUser(username, password, nickName, gender);
		String type = "1";
		try {
			bindingService.insertSelective(userId, unionId, type);
		} catch (Exception e) {
			return new ErrorBean(10003, "失败");
		}
		String tokenInfo = userService.loginUser(username, password);
		if(tokenInfo == null) {
			log.error("登录失败");
			return new ErrorBean(10002,"登录失败");
		}
		return new SuccessBean(tokenInfo);
	}
}
