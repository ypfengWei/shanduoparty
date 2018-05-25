package com.shanduo.party.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.controller.ActivityController;
import com.shanduo.party.entity.AccessToken;
import com.shanduo.party.entity.UserWechat;
import com.shanduo.party.mapper.AccessTokenMapper;
import com.shanduo.party.mapper.UserWechatMapper;
import com.shanduo.party.service.WechatService;
import com.shanduo.party.util.GetAccessTokenUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class WechatServiceImpl implements WechatService{
	
	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	@Autowired
	private UserWechatMapper userWechatMapper;
	
	@Autowired
	private AccessTokenMapper accessTokenMapper;

	@Override
	public boolean selectByPrimaryKey(String appid, String secret, String code) {
		if(GetAccessTokenUtils.getUserWechat(appid, secret, code).getUserId() != null) {
			UserWechat userWechat = userWechatMapper.selectByPrimaryKey(GetAccessTokenUtils.getUserWechat(appid, secret, code).getUserId());
			if(userWechat != null) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public int insertSelective(String appid, String secret, String code) {
		if(GetAccessTokenUtils.getUserWechat(appid, secret, code).getUserId() != null) {
			a(appid, secret, code);
			if(selectByPrimaryKey(appid, secret, code)) {
				UserWechat userWechat = GetAccessTokenUtils.getUserWechat(appid, secret, code);
	        	int i = userWechatMapper.insertSelective(userWechat);
	        	if(i < 1) {
	        		log.error("绑定失败");
	        		throw new RuntimeException();
	        	}
	        } else { 
	        	log.error("该用户已绑定");
	    		throw new RuntimeException();
	        }
		} else {
			return 0;
		}
        return 1;
	}
	
	@Override
	public Integer selectByUserId(String appid, String secret, String code) {
		if(GetAccessTokenUtils.getUserWechat(appid, secret, code).getUserId() != null) {
			a(appid, secret, code);
			Integer a = userWechatMapper.selectByUserId(GetAccessTokenUtils.getUserWechat(appid, secret, code).getUnionId());
			if(a == null) {
				log.error("该用户没有注册");
	    		throw new RuntimeException();
			}
		} else {
			return 0;
		}
		return 1;
	}

	public boolean a(String appid, String secret, String code) {
		if(GetAccessTokenUtils.getUserWechat(appid, secret, code).getUserId() != null) {
			AccessToken accessToken = accessTokenMapper.selectByAppId(appid);
	        if (accessToken == null) {
	        	AccessToken newAccessToken = GetAccessTokenUtils.getAccessToken(appid, secret, code);
	        	if(newAccessToken != null) {
	        		int i = accessTokenMapper.insertSelective(newAccessToken);
	        		if(i < 1) {
	        			log.error("凭证添加失败");
	        			throw new RuntimeException();
	        		}
	        	}
	        } else {
	            long newTime = System.currentTimeMillis() / 1000;
	            long oldTime = accessToken.getCreateDate().getTime() / 1000;
	            if (newTime - oldTime >= accessToken.getExpiresIn()) {
	            	AccessToken newAccessToken = GetAccessTokenUtils.getAccessToken(appid, secret, code);
	                if (newAccessToken != null) {
	                	accessToken.setAccessToken(newAccessToken.getAccessToken());
	                	accessToken.setAppId(newAccessToken.getAppId());
	                	accessToken.setExpiresIn(newAccessToken.getExpiresIn());
	                	accessToken.setCreateDate(new Date());
	                	accessToken.getId();
	                	int i = accessTokenMapper.updateByPrimaryKey(accessToken);
	                	if(i < 1) {
	                		log.error("凭证修改失败");
	            			throw new RuntimeException();
	                	}
	                }
	            }
	        }
		} else {
			return false;
		}
		return true;
	}

}
