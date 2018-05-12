package com.shanduo.party.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ShanduoCollect;
import com.shanduo.party.mapper.ShanduoCollectMapper;
import com.shanduo.party.service.CollectServic;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 
 * @ClassName: CollectServicImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月26日 下午2:56:50
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CollectServicImpl implements CollectServic {
	
	private static final Logger log = LoggerFactory.getLogger(CollectServicImpl.class);
	
	@Autowired
	private ShanduoCollectMapper collectMapper;

	@Override
	public int saveCollect(Integer userId,String fileUrl) {
		ShanduoCollect collect = new ShanduoCollect();
		collect.setId(UUIDGenerator.getUUID());
		collect.setUserId(userId);
		collect.setFileUrl(fileUrl);
		int i = collectMapper.insertSelective(collect);
		if(i < 1) {
			log.error("收藏录入失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public boolean checkCollect(Integer userId,String collectId) {
		ShanduoCollect collect = collectMapper.checkCollect(userId, collectId);
		if(collect != null) {
			return true;
		}
		return false;
	}

	@Override
	public int deleteCollect(Integer userId,String collectId) {
		String[] collectIds = collectId.split(",");
		for (int i = 0; i < collectIds.length; i++) {
			if(StringUtils.isNull(collectIds[i])) {
				continue;
			}
			int n = collectMapper.deleteByUserFile(userId, collectIds[i]);
			if(n < 1) {
				log.error("删除收藏记录失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	@Override
	public Map<String, Object> selectByUserList(Integer userId, Integer pageNum, Integer pageSize) {
		int totalRecord = collectMapper.selectByCount(userId);
		if(totalRecord == 0) {
			return null;
		}
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList = collectMapper.selectByUserList(userId, pageNum, page.getPageSize());
		if(resultList == null) {
			return null;
		}
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

}
