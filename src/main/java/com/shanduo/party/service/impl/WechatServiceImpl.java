package com.shanduo.party.service.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.shanduo.party.controller.ActivityController;
import com.shanduo.party.entity.AccessToken;
import com.shanduo.party.entity.UserWechat;
import com.shanduo.party.mapper.AccessTokenMapper;
import com.shanduo.party.mapper.UserWechatMapper;
import com.shanduo.party.service.WechatService;
import com.shanduo.party.util.JsonStringUtils;
import com.shanduo.party.util.UUIDGenerator;

public class WechatServiceImpl implements WechatService{
	
	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	@Autowired
	private UserWechatMapper userWechatMapper;
	
	@Autowired
	private AccessTokenMapper accessTokenMapper;

	@Override
	public int insertSelective(Integer userId, String appid, String secret, String code) {
    	UserWechat userWechat = new UserWechat();
    	AccessToken accessToken = new AccessToken();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet method = new HttpGet("https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code".replace("APPID", appid).replace("SECRET", secret).replace("CODE", code));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	Map<String,Object> resultMap = JsonStringUtils.getMap(EntityUtils.toString(response.getEntity()));
            	accessToken.setAccessToken(resultMap.get("access_token").toString());
            	accessToken.setExpiresIn(Integer.parseInt(resultMap.get("expires_in").toString()));
            	userWechat.setOpenId(resultMap.get("openid").toString());
            }
            CloseableHttpResponse responses = null;
            HttpGet methods = new HttpGet("https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID".replace("access_token", accessToken.getAccessToken()).replace("openid", userWechat.getOpenId()));
            responses = httpClient.execute(methods);
            if (responses.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	Map<String,Object> resultMap = JsonStringUtils.getMap(EntityUtils.toString(response.getEntity()));
            	userWechat.setUnionId(resultMap.get("unionid").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        accessToken.setId(UUIDGenerator.getUUID());
        int count = accessTokenMapper.insertSelective(accessToken);
        if(count < 1) {
        	log.error("凭证添加失败");
        	throw new RuntimeException();
        }
        userWechat.setUserId(userId);
        int i = userWechatMapper.insertSelective(userWechat);
        if(i < 1) {
        	log.error("openId添加失败");
			throw new RuntimeException();
        }
		return 1;
	}
	
	@Override
	public Integer selectByUserId(String union_id) {
		
		return null;
	}
	
	//本地获取AccessToken如果失效才向微信服务器获取最新的AccessToken再更新本地AccessToken
	AccessToken getAccessToken() {
		return null;
//	        AccessToken accessToken = tokenService.getLocalAccessToken(Config.APPID);
//	        if (accessToken == null) {
//	            accessToken = getWXAccess_token(Config.APPID, Config.SECRET);
//	            if (accessToken != null) {
//	                accessToken.setAppid(Config.APPID);
//	                accessToken.setCreate_date(System.currentTimeMillis());
//	                tokenService.saveOrUpdateAccessToken(accessToken);
//	            }
//	        } else {
//	            long newTime = System.currentTimeMillis() / 1000;
//	            long oldTime = accessToken.getCreate_date() / 1000;
//	            if ((int) (newTime - oldTime) >= accessToken.getExpires_in()) {
//	                AccessToken newAccessToken = getWXAccess_token(Config.APPID, Config.SECRET);
//	                if (newAccessToken != null) {
//	                    accessToken.setCreate_date(System.currentTimeMillis());
//	                    accessToken.setAccess_token(newAccessToken.getAccess_token());
//	                    accessToken.setExpires_in(newAccessToken.getExpires_in());
//	                    tokenService.saveOrUpdateAccessToken(accessToken);
//	                }
//	            }
//	        }
//	        return accessToken;
	}

}
