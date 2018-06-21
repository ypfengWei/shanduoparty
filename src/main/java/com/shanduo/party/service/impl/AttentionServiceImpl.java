package com.shanduo.party.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserAttention;
import com.shanduo.party.entity.UserAttentionApply;
import com.shanduo.party.im.ImUtils;
import com.shanduo.party.mapper.UserAttentionApplyMapper;
import com.shanduo.party.mapper.UserAttentionMapper;
import com.shanduo.party.service.AttentionService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.UUIDGenerator;


/**
 * 
 * @ClassName: AttentionServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月27日 上午9:27:16
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttentionServiceImpl implements AttentionService {

	private static final Logger log = LoggerFactory.getLogger(AttentionServiceImpl.class);
	
	@Autowired
	private UserAttentionMapper attentionMapper;
	@Autowired
	private UserAttentionApplyMapper attentionApplyMapper;
	@Autowired
	private VipService vipService;
	
	@Override
	public int checkAttention(Integer userId, Integer attention) {
		UserAttention attentions = attentionMapper.checkAttention(attention, userId);
		if(attentions == null) {
			return 0;
		}
		if("1".equals(attentions.getAttentionType())) {
			return 1;
		}
		return 2;
	}
	
	@Override
	public int saveAttention(Integer userId, Integer attention) {
		boolean flag = ImUtils.addFriend(userId+"", attention+"");
		if(flag) {
			log.error("IM添加好友失败");
			throw new RuntimeException();
		}
		attentionMapper.deleteAttention(userId, attention, "2");
		UserAttention attentions = new UserAttention();
		attentions.setId(UUIDGenerator.getUUID());
		attentions.setUserId(userId);
		attentions.setAttention(attention);
		int i = attentionMapper.insertSelective(attentions);
		if(i < 1) {
			log.error("添加好友记录失败");
			throw new RuntimeException();
		}
		attentions = new UserAttention();
		attentions.setId(UUIDGenerator.getUUID());
		attentions.setUserId(attention);
		attentions.setAttention(userId);
		i = attentionMapper.insertSelective(attentions);
		if(i < 1) {
			log.error("添加好友记录失败");
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public boolean checkAttentionApply(Integer userId, Integer attention) {
		UserAttentionApply attentionApply = attentionApplyMapper.checkAttentionApply(userId, attention);
		if(attentionApply != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public int saveAttentionApply(Integer userId,Integer attention) {
		UserAttentionApply attentionApply = new UserAttentionApply();
		attentionApply.setId(UUIDGenerator.getUUID());
		attentionApply.setUserId(userId);
		attentionApply.setAttention(attention);
		int i = attentionApplyMapper.insertSelective(attentionApply);
		if(i < 1) {
			log.error("申请好友记录录入失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public Map<String, Object> attentionApplyList(Integer attention,Integer pageNum,Integer pageSize) {
		int totalRecord = attentionApplyMapper.selectAttentionCount(attention);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList = attentionApplyMapper.selectAttentionList(attention, pageNum, pageSize);
		for(Map<String, Object> map : resultList) {
			map.put("picture", PictureUtils.getPictureUrl(map.get("picture")));
		}
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

	@Override
	public int isAttentionApply(String applyId,Integer attention,String typeId) {
		UserAttentionApply attentionApply = attentionApplyMapper.selectAttentionApply(applyId, attention);
		if(attentionApply == null) {
			log.error("此条记录已经操作");
			throw new RuntimeException();
		}
		if("1".equals(typeId)) {
			UserAttention attentions = new UserAttention();
			attentions.setId(UUIDGenerator.getUUID());
			attentions.setUserId(attentionApply.getUserId());
			attentions.setAttention(attentionApply.getAttention());
			int i = attentionMapper.insertSelective(attentions);
			if(i < 1) {
				log.error("添加好友记录失败");
				throw new RuntimeException();
			}
			attentions.setId(UUIDGenerator.getUUID());
			attentions.setUserId(attentionApply.getAttention());
			attentions.setAttention(attentionApply.getUserId());
			i = attentionMapper.insertSelective(attentions);
			if(i < 1) {
				log.error("添加好友记录失败");
				throw new RuntimeException();
			}
		}else {
			attentionApply.setAttentionType(typeId);
			int i = attentionApplyMapper.updateByPrimaryKeySelective(attentionApply);
			if(i < 1) {
				log.error("修改申请好友记录失败");
				throw new RuntimeException();
			}
			return 1;
		}
		attentionApply.setAttentionType(typeId);
		int i = attentionApplyMapper.updateByPrimaryKeySelective(attentionApply);
		if(i < 1) {
			log.error("修改申请好友记录失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int hideAttentionApply(String applyIds, Integer attention) {
		String[] applyId = applyIds.split(",");
		for (int i = 0; i < applyId.length; i++) {
			if(applyId[i] == null || "".equals(applyId[i])) {
				continue;
			}
			int n = attentionApplyMapper.updateAttentionApply(applyId[i], attention);
			if(n < 1) {
				log.error("软删除申请好友记录失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	@Override
	public List<Map<String, Object>> attentionList(Integer userId,String typeId) {
		List<Map<String, Object>> resultList = attentionMapper.selectAttentionList(userId, typeId);
		for(Map<String, Object> map : resultList) {
			map.put("picture", PictureUtils.getPictureUrl(map.get("picture")));
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			map.put("vip", vipService.selectVipLevel(Integer.parseInt(map.get("userId").toString())));
		}
		return resultList;
	}

	@Override
	public int delAttention(Integer userId, Integer attention, String typeId) {
		int i = attentionMapper.deleteAttention(userId, attention, typeId);
		if(i < 1) {
			throw new RuntimeException();
		}
		if("1".equals(typeId)) {
			boolean flag = ImUtils.deleteFriend(userId+"", attention+"");
			if(flag) {
				log.error("IM删除好友失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	@Override
	public int blacklistAttention(Integer userId, Integer attention) {
		int i = checkAttention(userId, attention);
		if(i == 2) {
			return 1;
		}
		if(i == 1) {
			delAttention(userId, attention,"1");
		}
		UserAttention attentions = new UserAttention();
		attentions.setId(UUIDGenerator.getUUID());
		attentions.setUserId(userId);
		attentions.setAttention(attention);
		attentions.setAttentionType("2");
		i = attentionMapper.insertSelective(attentions);
		if(i < 1) {
			log.error("拉黑失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int attentionCount(Integer userId) {
		return attentionMapper.attentionCount(userId);
	}

}
