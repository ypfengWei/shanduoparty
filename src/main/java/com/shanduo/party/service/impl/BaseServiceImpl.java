package com.shanduo.party.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.mapper.ShanduoUserMapper;
import com.shanduo.party.mapper.UserTokenMapper;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.VipService;

/**
 * 
 * @ClassName: BaseServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月19日 上午10:51:45
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseServiceImpl implements BaseService {

	private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);
	
	@Autowired
	private UserTokenMapper userTokenMapper;
	@Autowired
	private ShanduoUserMapper userMapper;
	@Autowired
	private VipService vipService;
	
	@Override
	public Integer checkUserToken(String token) {
		UserToken userToken = userTokenMapper.selectByToken(token);
		if(userToken == null) {
			log.error("token已失效");
			return null;
		}
		return userToken.getUserId();
	}

	@Override
	public boolean checkUserRole(Integer userId,String role) {
		if(userId == null) {
			return true;
		}
		ShanduoUser user = userMapper.selectByPrimaryKey(userId);
		if(user == null) {
			return true;
		}
		if(user.getHeadPortraitId().equals(role)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean checkUserVip(Integer userId, Integer vip) {
		int lvVip = vipService.selectVipLevel(userId);
		if(lvVip < vip) {
			return true;
		}
		return false;
	}

}
