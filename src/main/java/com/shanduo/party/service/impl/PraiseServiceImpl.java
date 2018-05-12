package com.shanduo.party.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.DynamicPraise;
import com.shanduo.party.mapper.DynamicPraiseMapper;
import com.shanduo.party.service.PraiseService;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 
 * @ClassName: PraiseServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月24日 下午5:36:45
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PraiseServiceImpl implements PraiseService {

	private static final Logger log = LoggerFactory.getLogger(PraiseServiceImpl.class);
	
	@Autowired
	private DynamicPraiseMapper praiseMapper;
	
	@Override
	public int savePraise(Integer userId, String dynamicId) {
		DynamicPraise praise = new DynamicPraise();
		praise.setId(UUIDGenerator.getUUID());
		praise.setUserId(userId);
		praise.setDynamicId(dynamicId);
		int i = praiseMapper.insertSelective(praise);
		if(i < 1) {
			log.error("点赞录入失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public boolean checkPraise(Integer userId, String dynamicId) {
		if(userId == null) {
			return false;
		}
		DynamicPraise praise = praiseMapper.selectByUserId(userId, dynamicId);
		if(praise != null) {
			return true;
		}
		return false;
	}

	@Override
	public int deletePraise(Integer userId, String dynamicId) {
		int i = praiseMapper.deletePraise(userId, dynamicId);
		if(i < 1) {
			log.error("点赞取消失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int selectByCount(String dynamicId) {
		int i = praiseMapper.selectByCount(dynamicId);
		if(i > 0) {
			return i;
		}
		return 0;
	}

}
