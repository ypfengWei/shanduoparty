package com.shanduo.party.service.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ActivityScore;
import com.shanduo.party.entity.ShanduoReputationRecord;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.mapper.ShanduoReputationRecordMapper;
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.ScoreUtils;
import com.shanduo.party.util.UUIDGenerator;


/**
 * 评价操作实现类
 * @ClassName: ScoreServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月18日 下午4:18:25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ScoreServiceImpl implements ScoreService {

	private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);

	@Autowired
	private ActivityScoreMapper activityScoreMapper;

	@Autowired
	private ShanduoReputationMapper shanduoReputationMapper;
	
	@Autowired
	private ShanduoReputationRecordMapper reputationRecordMapper;
	
	@Autowired
	private ShanduoActivityMapper shanduoActivityMapper;
	
	@Autowired
	private VipService vipService;
	
	@Override
	public int updateActivityScore(Integer userId, String activityId, Integer score, String evaluationcontent) {
		int i = activityScoreMapper.updateByUserIdTwo(userId,activityId,score,evaluationcontent);
		if (i < 1) {
			log.error("评价失败");
			throw new RuntimeException();
		}
		if(!getRecord(userId, activityId, score, evaluationcontent, 1)) {
			log.error("信誉历史数据添加失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updateByUserId(Integer userId, String activityId, Integer othersScore, String beEvaluated) {
		int i = activityScoreMapper.updateByUserId(userId, activityId, othersScore, beEvaluated);
		if (i < 1) {
			log.error("评价失败");
			throw new RuntimeException();
		}
		if(!getRecord(userId, activityId, othersScore, beEvaluated, 2)) {
			log.error("信誉历史数据添加失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public Map<String, Object> selectByIdScore(Integer userId, Integer pageNum, Integer pageSize) {
		int totalrecord = activityScoreMapper.selectByIdScoreCount(userId);
		if (totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<ActivityScore> activityScores = activityScoreMapper.selectByIdScore(userId, pageNum, page.getPageSize());
		if (activityScores == null || activityScores.isEmpty()) {
			return null;
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityScores);
		return resultMap;
	}

	@Override
	public int updateById(String time) {
		int i = activityScoreMapper.updateById(time);
		if (i > 0) {
			return i;
		}
		return 0;
	}

	@Override
	public int updateByIdTime(String time) {
		int i = activityScoreMapper.updateByIdTime(time);
		if (i > 0) {
			return i;
		}
		return 0;
	}
	
	@Override
	public int updateByReputation() {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String startTime = format.format(time);
		int i = 0;
		List<Map<String,Integer>> resultList = reputationRecordMapper.selectByTime(startTime);
		if(resultList == null || resultList.isEmpty()) {
			return 0;
		} else {
			for (Map<String,Integer> shanduoReputationRecord : resultList) {
				int otherUserId = shanduoReputationRecord.get("user_id");
				Integer shanduoReputation = shanduoReputationMapper.selectByUserId(otherUserId);
				i = shanduoReputationMapper.updateByUserId(otherUserId, shanduoReputation+ScoreUtils.getScore(otherUserId));
			}
			if(i > 0) {
				return i;
			}
		}
		return 0;
	}
	
	public boolean getRecord(Integer userId, String activityId, Integer score, String evaluation, Integer type) {
		ShanduoReputationRecord shanduoReputationRecord = new ShanduoReputationRecord();
		shanduoReputationRecord.setId(UUIDGenerator.getUUID());
		if(type == 1) {
			shanduoReputationRecord.setUserId(shanduoActivityMapper.selectById(activityId));
			shanduoReputationRecord.setOtheruserId(userId);
		} else {
			shanduoReputationRecord.setUserId(userId);
			shanduoReputationRecord.setOtheruserId(shanduoActivityMapper.selectById(activityId));
		}
		if(ScoreUtils.getScore(shanduoReputationRecord.getUserId(), shanduoReputationRecord.getOtheruserId())){
			switch (score) {//未满6分
			case 1:
			case 2:
				shanduoReputationRecord.setDeductionCount(-1);
				shanduoReputationRecord.setReputationType("1");
				break;
			case 3:
				break;
			case 4:
			case 5:
				shanduoReputationRecord.setDeductionCount(1);
				shanduoReputationRecord.setReputationType("1");
				break;
			default:
				log.error("评分异常");
				break;
			}
		}else {
			switch (score) {
			case 1:
			case 2:
				shanduoReputationRecord.setDeductionCount(-1);
				shanduoReputationRecord.setReputationType("2");
				break;
			case 3:
				break;
			case 4:
			case 5:
				shanduoReputationRecord.setDeductionCount(1);
				shanduoReputationRecord.setReputationType("2");
				break;
			default:
				log.error("评分异常");
				break;
		}
		}
		int n =reputationRecordMapper.insertSelective(shanduoReputationRecord);
		if(n < 1) {
			log.error("信誉历史记录添加失败");
			throw new RuntimeException();
		}
		return true;
	}

	@Override
	public Map<String, Object> selectReputation(Integer userToken,Integer userId, Integer pageNum, Integer pageSize) {
		Map<String, Object> map = activityScoreMapper.selectReputation(userId);
		int totalrecord = activityScoreMapper.activityCount(userId); //发布活动记录
//			totalrecord = activityScoreMapper.activityCounts(userId); //参加活动记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivity(userId, pageNum, page.getPageSize()); //发布的活动与评价
//			list = activityScoreMapper.selectActivitys(userId, pageNum, page.getPageSize()); //参与的活动与评价
		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();
		Set<String> activityIdSet = new HashSet<String>();
		Map<String, Object> InitiatorMap = new HashMap<String, Object>();
		List<Map<String, Object>> ScoreMapList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> maps : list) {
			Map<String, Object> ScoreMap = new HashMap<>();
			if(activityIdSet.contains(maps.get("id").toString())) {
				if(maps.get("uid").equals(userId)) {
					InitiatorMap.put("birthday", AgeUtils.getAgeFromBirthTime(maps.get("birthday").toString()));
					InitiatorMap.put("vipGrade",vipService.selectVipLevel(userId)); //vip等级
					InitiatorMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString()));
					InitiatorMap.put("user_name",maps.get("user_name"));
					InitiatorMap.put("uid",maps.get("uid"));
					InitiatorMap.put("mode",maps.get("mode"));
					InitiatorMap.put("id",maps.get("id"));	
				} else {
					ScoreMap.clear();
					ScoreMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString()));
					ScoreMap.put("score", maps.get("score"));
					ScoreMap.put("evaluation_content", maps.get("evaluation_content"));
					ScoreMap.put("others_score", maps.get("others_score"));
					ScoreMap.put("be_evaluated", maps.get("be_evaluated"));
					ScoreMapList.add(ScoreMap);
				}
			} else {
				activityIdSet.add(maps.get("id").toString());
				if(!InitiatorMap.isEmpty()) {
					List<Map<String, Object>> ScoreMapLists = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> scoremap : ScoreMapList){
						Map<String, Object> newscoremap = new HashMap<String, Object>();
						newscoremap.put("head_portrait_id", scoremap.get("head_portrait_id"));
						newscoremap.put("score", scoremap.get("score"));
						newscoremap.put("evaluation_content", scoremap.get("evaluation_content"));
						newscoremap.put("others_score", scoremap.get("others_score"));
						newscoremap.put("be_evaluated", scoremap.get("be_evaluated"));
						ScoreMapLists.add(newscoremap);
					}
					Map<String, Object> InitiatorMaps = new HashMap<>();
					InitiatorMaps.put("id",InitiatorMap.get("id"));
					InitiatorMaps.put("user_name",InitiatorMap.get("user_name"));
					InitiatorMaps.put("mode",InitiatorMap.get("mode"));
					InitiatorMaps.put("birthday",InitiatorMap.get("birthday"));
					InitiatorMaps.put("vipGrade",InitiatorMap.get("vipGrade"));
					InitiatorMaps.put("head_portrait_id",InitiatorMap.get("head_portrait_id"));
					InitiatorMaps.put("uid",InitiatorMap.get("uid"));
					InitiatorMaps.put("scoreList", ScoreMapLists);
					activityList.add(InitiatorMaps);
					ScoreMapList.clear();
					InitiatorMap.clear();//清除上一个活动的信息
				}
				if(maps.get("uid").equals(userId)) {
					InitiatorMap.put("birthday", AgeUtils.getAgeFromBirthTime(maps.get("birthday").toString()));
					InitiatorMap.put("vipGrade",vipService.selectVipLevel(userId)); //vip等级
					InitiatorMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString()));
					InitiatorMap.put("user_name",maps.get("user_name"));
					InitiatorMap.put("uid",maps.get("uid"));
					InitiatorMap.put("mode",maps.get("mode"));
					InitiatorMap.put("id",maps.get("id"));
				} else {
					ScoreMap.clear();
					ScoreMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString()));
					ScoreMap.put("score", maps.get("score"));
					ScoreMap.put("evaluation_content", maps.get("evaluation_content"));
					ScoreMap.put("others_score", maps.get("others_score"));
					ScoreMap.put("be_evaluated", maps.get("be_evaluated"));
					ScoreMapList.add(ScoreMap);
				}
			}
		}
		InitiatorMap.put("scoreList", ScoreMapList);
		activityList.add(InitiatorMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("map", map);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectJoinActivity(Integer userId, Integer pageNum, Integer pageSize) {
		int totalrecord = activityScoreMapper.activityCounts(userId); //参加活动记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivitys(userId, pageNum, page.getPageSize()); //参与的活动
//		List<Map<String, Object>> scoreList = new ArrayList<>();
//		for (Map<String, Object> map : list) {
//			map.put("birthday", AgeUtils.getAgeFromBirthTime(map.get("birthday").toString()));
//			map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
//			map.put("vipGrade",vipService.selectVipLevel(userId));
//			String activityId = map.get("id").toString();
//			scoreList = activityScoreMapper.selectScores(activityId);//活动下的发起人评分
//			for (Map<String, Object> smap : scoreList) {
//				smap.put("head_portrait_id", PictureUtils.getPictureUrl(smap.get("head_portrait_id").toString()));
//			}
//			map.put("scoreList", scoreList);
//		}
		getList(userId, totalrecord, list, 2);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("list", list);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		return resultMap;
	}

	public List<Map<String,Object>> getList(Integer userId, Integer totalrecord, List<Map<String, Object>> list, Integer type){
		List<Map<String, Object>> scoreList = new ArrayList<>();
		for (Map<String, Object> map : list) {
			map.put("birthday", AgeUtils.getAgeFromBirthTime(map.get("birthday").toString()));
			map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
			map.put("vipGrade",vipService.selectVipLevel(userId)); //vip等级
			String activityId = map.get("id").toString();
			if(type == 1) {
				scoreList = activityScoreMapper.selectScore(activityId); //活动下的参与人与评分 
			} else {
				scoreList = activityScoreMapper.selectScores(activityId); //活动下的发起人评分
			}
			for (Map<String, Object> scoreMap : scoreList) {
				scoreMap.put("head_portrait_id", PictureUtils.getPictureUrl(scoreMap.get("head_portrait_id").toString()));
			}
			map.put("scoreList", scoreList);
		}
		return list;
	}
}
