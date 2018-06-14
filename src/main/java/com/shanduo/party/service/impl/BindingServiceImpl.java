package com.shanduo.party.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserBinding;
import com.shanduo.party.mapper.UserBindingMapper;
import com.shanduo.party.service.BindingService;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 绑定实现类
 * @ClassName: BindingServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fangwei
 * @date 2018年6月14日 上午10:05:52
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BindingServiceImpl implements BindingService {
	
	private static final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);
	
	@Autowired
	private UserBindingMapper bindingMapper;

	@Override
	public Integer selectUserId(String union_id,String type) {
		return bindingMapper.selectUserId(union_id,type);
	}

	@Override
	public int insertSelective(int userId, String unionId,String type) {
		UserBinding userBinding = new UserBinding();
    	userBinding.setId(UUIDGenerator.getUUID());
    	userBinding.setUserId(userId);
    	userBinding.setType(type);
    	userBinding.setUnionId(unionId);
    	int i = bindingMapper.insertSelective(userBinding);
    	if(i < 1) {
    		log.error("绑定失败");
			throw new RuntimeException();
    	}
		return 1;
	}
	
	@Override
	public String selectOpenId(Integer userId, String type) {
		return bindingMapper.selectOpenId(userId, type);
	}

}
