package com.shanduo.party.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserToken;
import com.shanduo.party.mapper.UserTokenMapper;
import com.shanduo.party.service.BaseService;

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
	
	@Override
	public UserToken checkUserToken(String token) {
		UserToken userToken = userTokenMapper.selectByToken(token);
		if(userToken == null) {
			log.error("token失效");
			return null;
		}
		return userToken;
	}

}
