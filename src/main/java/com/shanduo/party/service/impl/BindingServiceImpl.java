package com.shanduo.party.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserBinding;
import com.shanduo.party.mapper.UserBindingMapper;
import com.shanduo.party.service.BindingService;
import com.shanduo.party.util.UUIDGenerator;

@Service
@Transactional(rollbackFor = Exception.class)
public class BindingServiceImpl implements BindingService {
	
	@Autowired
	private UserBindingMapper bindingMapper;

	@Override
	public Integer selectUserId(String union_id,String type) {
		return bindingMapper.selectUserId(union_id,type);
	}

	@Override
	public int insertSelective(int userId, String openId, String unionId,String type) {
		UserBinding userBinding = new UserBinding();
    	userBinding.setId(UUIDGenerator.getUUID());
    	userBinding.setUserId(userId);
    	userBinding.setOpenId(openId);
    	userBinding.setUnionId(unionId);
    	userBinding.setType(type);
		return bindingMapper.insertSelective(userBinding);
	}

	@Override
	public String selectOpenId(Integer userId, String type) {
		return bindingMapper.selectOpenId(userId, type);
	}

}
