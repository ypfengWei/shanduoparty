package com.shanduo.party.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shanduo.party.entity.AccessToken;
import com.shanduo.party.entity.UserWechat;
import com.shanduo.party.pay.WechatPayConfig;

public class GetAccessTokenUtils {

	private static final Logger log = LoggerFactory.getLogger(GetAccessTokenUtils.class);

	public static AccessToken getAccessToken(String code) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet method = new HttpGet(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
						.replace("APPID", WechatPayConfig.APPID).replace("SECRET", WechatPayConfig.secret)
						.replace("CODE", code));
		CloseableHttpResponse response = null;
		Map<String, Object> resultMap = new HashMap<>();
		AccessToken accessToken = new AccessToken();
		try {
			response = httpClient.execute(method);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultMap = JsonStringUtils.getMap(EntityUtils.toString(response.getEntity()));
				if (resultMap.containsKey("access_token")) {
					accessToken.setAccessToken(resultMap.get("access_token").toString());
					accessToken.setExpiresIn(Integer.parseInt(resultMap.get("expires_in").toString()));
					accessToken.setAppId(resultMap.get("appid").toString());
					accessToken.setId(UUIDGenerator.getUUID());
				} else {
					log.error("login weixin getOpenId error" + resultMap.toString());
					httpClient.close();
					response.close();
					return null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	public static UserWechat getUserWechat(String code) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet methods = new HttpGet(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
						.replace("APPID", WechatPayConfig.APPID).replace("SECRET", WechatPayConfig.secret).replace("CODE", code));
		CloseableHttpResponse responses = null;
		Map<String, Object> resultMaps = new HashMap<>();
		UserWechat userWechat = new UserWechat();
		try {
			responses = httpClient.execute(methods);
			if (responses.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultMaps = JsonStringUtils.getMap(EntityUtils.toString(responses.getEntity()));
				if (resultMaps.containsKey("unionid")) {
					resultMaps = JsonStringUtils.getMap(EntityUtils.toString(responses.getEntity()));
					userWechat.setOpenId(resultMaps.get("openid").toString());
				} else {
					log.error("login weixin getOpenId error" + resultMaps.toString());
					httpClient.close();
					responses.close();
					return null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String accessToken = resultMaps.get("access_token").toString();
		String openid = resultMaps.get("openid").toString();
		HttpGet method = new HttpGet("https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID"
				.replace("access_token", accessToken).replace("openid", openid));
		CloseableHttpResponse response = null;
		Map<String, Object> resultMap = new HashMap<>();
		try {
			response = httpClient.execute(method);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultMap = JsonStringUtils.getMap(EntityUtils.toString(response.getEntity()));
				if (resultMap.containsKey("unionid")) {
					userWechat.setUnionId(resultMap.get("unionid").toString());
				} else {
					log.error("login weixin getOpenId error" + resultMap.toString());
					httpClient.close();
					response.close();
					return null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userWechat;
	}
}
