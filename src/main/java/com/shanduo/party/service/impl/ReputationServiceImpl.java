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

import com.shanduo.party.entity.ShanduoReputationRecord;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.mapper.ShanduoReputationRecordMapper;
import com.shanduo.party.service.ReputationService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.ScoreUtils;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 信誉操作实现类
 * @ClassName: ReputationServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月23日 下午4:46:18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReputationServiceImpl implements ReputationService {
	
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
	
	@Override
	public boolean getRecord(Integer userId, Integer otheruserId, Integer score) {
		ShanduoReputationRecord shanduoReputationRecord = new ShanduoReputationRecord();
		shanduoReputationRecord.setId(UUIDGenerator.getUUID());
		shanduoReputationRecord.setUserId(otheruserId);
		shanduoReputationRecord.setOtheruserId(userId);
		if(ScoreUtils.getScore(shanduoReputationRecord.getUserId(), shanduoReputationRecord.getOtheruserId())){
			switch (score) {//未满6分
			case 1:
			case 2:
				shanduoReputationRecord.setDeductionCount(-1);
				shanduoReputationRecord.setReputationType("1");
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
		map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
		int totalrecord = activityScoreMapper.activityCount(userId); //发布活动记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivity(userId, pageNum, page.getPageSize()); //发布的活动与评价
		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();
		Set<String> activityIdSet = new HashSet<String>();
		Map<String, Object> initiatorMap = new HashMap<String, Object>(8);
		List<Map<String, Object>> scoreMapList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> maps : list) {
			if(activityIdSet.contains(maps.get("id").toString())) { //activityList中有该活动id
				if(maps.get("uid").equals(userId)) { //用户为发起者
					getInitiatorMap(initiatorMap, maps, userId); //得到活动信息
				} else {
					getScoreMap(maps).clear(); //清除上一条评论信息
					scoreMapList.add(getScoreMap(maps)); //用户为参与者
				}
			} else { //activityList中没有该活动id
				activityIdSet.add(maps.get("id").toString()); //给activityList赋活动Id的值
				if(!initiatorMap.isEmpty()) { //活动信息不为空循环获取评论信息到ScoreMapLists
					List<Map<String, Object>> scoreMapLists = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> scoremap : scoreMapList){
						Map<String, Object> newscoremap = new HashMap<String, Object>(2);
						newscoremap.putAll(scoremap);
						scoreMapLists.add(newscoremap);
					}
					//将单个活动信息和此活动下的评论信息放在InitiatorMaps中代表一个整体
					Map<String, Object> initiatorMaps = new HashMap<String, Object>(2); 
					initiatorMaps.putAll(initiatorMap);
					initiatorMaps.put("scoreList", scoreMapLists);
					activityList.add(initiatorMaps);
					scoreMapList.clear();//清除上一个活动所有评论信息
					initiatorMap.clear();//清除上一个活动的信息
				}
				if(maps.get("uid").equals(userId)) {
					getInitiatorMap(initiatorMap, maps, userId);
				} else {
					getScoreMap(maps).clear();
					scoreMapList.add(getScoreMap(maps));
				}
			}
		}
		if(scoreMapList.size() > 0) {
			initiatorMap.put("scoreList", scoreMapList);
			activityList.add(initiatorMap);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		resultMap.put("map", map);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectJoinActivity(Integer userId, Integer pageNum, Integer pageSize) {
		Map<String, Object> map = activityScoreMapper.selectReputation(userId);
		map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
		int totalrecord = activityScoreMapper.activityCounts(userId); //参加活动记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivitys(userId, pageNum, page.getPageSize()); //参与的活动
		List<Map<String, Object>> scoreList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> maps : list) {
			maps.put("birthday", AgeUtils.getAgeFromBirthTime(maps.get("birthday").toString()));
			maps.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString()));
			maps.put("vipGrade",vipService.selectVipLevel(userId));
			String activityId = maps.get("id").toString();
			scoreList = activityScoreMapper.selectScore(activityId);//活动下的发起人评分
			for (Map<String, Object> scoreMap : scoreList) {
				scoreMap.put("head_portrait_id", PictureUtils.getPictureUrl(scoreMap.get("head_portrait_id").toString()));
			}
			maps.put("scoreList", scoreList);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		resultMap.put("map", map);
		resultMap.put("list", list);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		return resultMap;
	}
	
	public Map<String, Object> getInitiatorMap(Map<String, Object> initiatorMap,Map<String, Object> maps, Integer userId){
		initiatorMap.put("birthday", AgeUtils.getAgeFromBirthTime(maps.get("birthday").toString())); //年龄
		initiatorMap.put("vipGrade",vipService.selectVipLevel(userId)); //vip等级
		initiatorMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString())); //发起者头像
		initiatorMap.put("user_name",maps.get("user_name")); //发起者名称
		initiatorMap.put("uid",maps.get("uid")); //发起者id
		initiatorMap.put("mode",maps.get("mode")); //活动支付方式
		initiatorMap.put("id",maps.get("id"));	//活动Id
		initiatorMap.put("activity_name",maps.get("activity_name")); //活动名称
		initiatorMap.put("gender",maps.get("gender")); //性别
		return initiatorMap;
	}
	
	public Map<String, Object> getScoreMap(Map<String, Object> maps){
		Map<String, Object> scoreMap = new HashMap<String, Object>(6);
		scoreMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString())); //参与者头像
		scoreMap.put("score", maps.get("score")); //参与者评分
		scoreMap.put("evaluation_content", maps.get("evaluation_content")); //参与者评价
		scoreMap.put("others_score", maps.get("others_score")); //发起者评分
		scoreMap.put("be_evaluated", maps.get("be_evaluated")); //发起者评价
		scoreMap.put("user_name", maps.get("user_name")); //参与者名称
		return scoreMap;
	}
 
	@Override
	public int updateReport(Integer userId) {
		List<Map<String, Object>> activityIds = shanduoActivityMapper.selectId(userId); //查询该用户下所有扣分标志为0以及开始时间过期的活动
		if(activityIds != null) {
			for(Map<String,Object> maps:activityIds){
				String activityId = maps.get("id").toString();
				Map<String, Object> scoreRecords = shanduoActivityMapper.numberScore(activityId);
				int join = Integer.parseInt(scoreRecords.get("number").toString()); //参加记录
				int scoreCount = Integer.parseInt(scoreRecords.get("score").toString()); //评分记录
				if(join != 0) {
					if(join == scoreCount) { //如果参与的人全部评价完
						List<Map<String, Object>> scores = shanduoActivityMapper.selectScore(activityId); //查询活动下的评分信息
						int lowScore = 0;
						for (Map<String, Object> scoresMap : scores) {
							int score = Integer.parseInt(scoresMap.get("score").toString()); //评分
							if(score < 3) {  
								lowScore++;//如果评分小于3的累加
							}
						}
						if(lowScore != 0) {
							if(scoreCount/lowScore < 2) { //在一次活动中差评所占百分比大于50%的扣一分
								int deduction = shanduoReputationMapper.selectDeduction(userId);
								int reputations = shanduoReputationMapper.updateDeduction(userId, deduction+1);
								if(reputations < 1) {
									log.error("扣分修改失败");
									throw new RuntimeException();
								}
								//修改信誉等级后将活动表中的扣分标志改为1，避免重复扣分
								int i = shanduoActivityMapper.updateDownFlag(activityId);
								if(i  < 1) {
									log.error("活动扣分标志修改失败");
									throw new RuntimeException();
								}
							}
						}
					}
				}
			}
		}
		return 1;
	}

}
