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
import com.shanduo.party.entity.Reportrecord;
import com.shanduo.party.entity.ShanduoReputationRecord;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ReportrecordMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.mapper.ShanduoReputationRecordMapper;
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.ScoreUtils;
import com.shanduo.party.util.StringUtils;
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
	private ReportrecordMapper reportrecordMapper;
	
	@Autowired
	private VipService vipService;
	
	@Override
	public int updateActivityScore(Integer userId, String activityId, Integer score, String evaluationcontent) {
		int i = activityScoreMapper.updateByUserIdTwo(userId,activityId,score,evaluationcontent);
		if (i < 1) {
			log.error("评价失败");
			throw new RuntimeException();
		}
		if(score != 3) {
			if(!getRecord(userId, activityId, score, evaluationcontent, 1)) {
				log.error("信誉历史数据添加失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	@Override
	public int updateByUserId(String activityId, List<Map<String, Object>> list) {
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			int score = Integer.parseInt(map.get("score").toString());
			if (StringUtils.isNull(score+"") || !(score+"").matches("^[1-5]$")) {
				log.error("评分错误");
				throw new RuntimeException();
			}
			int userId = Integer.parseInt(map.get("userId").toString());
			if (StringUtils.isNull(userId+"")) {
				log.error("评价用户为空");
				throw new RuntimeException();
			}
			String evaluated = map.get("evaluated").toString();
			int n = activityScoreMapper.updateByUserId(userId, activityId, score, evaluated);
			if (n < 1) {
				log.error("评价失败");
				throw new RuntimeException();
			}
			if(score != 3) {
				if(!getRecord(userId, activityId, score, evaluated, 2)) {
					log.error("信誉历史数据添加失败");
					throw new RuntimeException();
				}
			}
		}
		return 1;
	}

	@Override
	public Map<String, Object> selectByIdScore(Integer userId, Integer pageNum, Integer pageSize) {
		int totalrecord = activityScoreMapper.selectByIdScoreCount(userId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<ActivityScore> activityScores = activityScoreMapper.selectByIdScore(userId, pageNum, page.getPageSize());
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
		Map<String, Object> InitiatorMap = new HashMap<String, Object>();
		List<Map<String, Object>> ScoreMapList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> maps : list) {
			if(activityIdSet.contains(maps.get("id").toString())) {
				if(maps.get("uid").equals(userId)) {
					getInitiatorMap(InitiatorMap, maps, userId);
				} else {
					getScoreMap(maps).clear();
					ScoreMapList.add(getScoreMap(maps));
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
						newscoremap.put("user_name", scoremap.get("user_name"));
						ScoreMapLists.add(newscoremap);
					}
					Map<String, Object> InitiatorMaps = new HashMap<>();
					InitiatorMaps.putAll(InitiatorMap);
					InitiatorMaps.put("scoreList", ScoreMapLists);
					activityList.add(InitiatorMaps);
					ScoreMapList.clear();
					InitiatorMap.clear();//清除上一个活动的信息
				}
				if(maps.get("uid").equals(userId)) {
					getInitiatorMap(InitiatorMap, maps, userId);
				} else {
					getScoreMap(maps).clear();
					ScoreMapList.add(getScoreMap(maps));
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
		Map<String, Object> map = activityScoreMapper.selectReputation(userId);
		map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
		int totalrecord = activityScoreMapper.activityCounts(userId); //参加活动记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivitys(userId, pageNum, page.getPageSize()); //参与的活动
		List<Map<String, Object>> scoreList = new ArrayList<>();
		for (Map<String, Object> maps : list) {
			maps.put("birthday", AgeUtils.getAgeFromBirthTime(maps.get("birthday").toString()));
			maps.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString()));
			maps.put("vipGrade",vipService.selectVipLevel(userId));
			String activityId = maps.get("id").toString();
			scoreList = activityScoreMapper.selectScore(activityId);//活动下的发起人评分
			for (Map<String, Object> ScoreMap : scoreList) {
				ScoreMap.put("head_portrait_id", PictureUtils.getPictureUrl(ScoreMap.get("head_portrait_id").toString()));
			}
			maps.put("scoreList", scoreList);
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("map", map);
		resultMap.put("list", list);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		return resultMap;
	}
	
	public Map<String, Object> getInitiatorMap(Map<String, Object> InitiatorMap,Map<String, Object> maps, Integer userId){
		InitiatorMap.put("birthday", AgeUtils.getAgeFromBirthTime(maps.get("birthday").toString())); //年龄
		InitiatorMap.put("vipGrade",vipService.selectVipLevel(userId)); //vip等级
		InitiatorMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString())); //发起者头像
		InitiatorMap.put("user_name",maps.get("user_name")); //发起者名称
		InitiatorMap.put("uid",maps.get("uid")); //发起者id
		InitiatorMap.put("mode",maps.get("mode")); //活动支付方式
		InitiatorMap.put("id",maps.get("id"));	//活动Id
		InitiatorMap.put("activity_name",maps.get("activity_name")); //活动名称
		return InitiatorMap;
	}
	
	public Map<String, Object> getScoreMap(Map<String, Object> maps){
		Map<String, Object> ScoreMap = new HashMap<String, Object>();
		ScoreMap.put("head_portrait_id", PictureUtils.getPictureUrl(maps.get("head_portrait_id").toString())); //参与者头像
		ScoreMap.put("score", maps.get("score")); //参与者评分
		ScoreMap.put("evaluation_content", maps.get("evaluation_content")); //参与者评价
		ScoreMap.put("others_score", maps.get("others_score")); //发起者评分
		ScoreMap.put("be_evaluated", maps.get("be_evaluated")); //发起者评价
		ScoreMap.put("user_name", maps.get("user_name")); //参与者名称
		return ScoreMap;
	}
 
	
	@Override
	public int updateReputation(String activityId, String type) {
		List<Map<String, Object>> list = reportrecordMapper.selectReportId(activityId);
		int deduction = 0;
		int reputation = 0;
		int userId = 0;
		int reportId = 0;
		if("1".equals(type)) {
			for (Map<String, Object> map : list) {
				userId = Integer.parseInt(map.get("user_id").toString());
				reportId = Integer.parseInt(map.get("report_id").toString());
				deduction = shanduoReputationMapper.selectDeduction(userId);
				reputation = shanduoReputationMapper.selectByUserId(reportId);
				int reputations = shanduoReputationMapper.updateDeduction(reportId, reputation+1);
				if(reputations < 1) {
					log.error("信誉等级修改失败");
					throw new RuntimeException();
				}
			}
			int deductions = shanduoReputationMapper.updateDeduction(userId, deduction+5);
			if(deductions < 1) {
				log.error("扣分失败");
				throw new RuntimeException();
			}
		} else {
			for (Map<String, Object> map : list) {
				int reportIds = Integer.parseInt(map.get("report_id").toString());
				reputation = shanduoReputationMapper.selectByUserId(reportIds);
				int reputations = shanduoReputationMapper.updateDeduction(reportIds, reputation-1);
				if(reputations < 1) {
					log.error("信誉等级修改失败");
					throw new RuntimeException();
				}
			}
		}
		return 1;
	}

	@Override
	public int report(String activityId, Integer report, Integer beReported) {
		Reportrecord reportrecord = new Reportrecord();
		reportrecord.setId(UUIDGenerator.getUUID());
		reportrecord.setActivityId(activityId);
		reportrecord.setUserId(beReported);
		reportrecord.setReportId(report);
		int i = reportrecordMapper.insert(reportrecord);
		if(i < 1) {
			log.error("举报记录添加失败");
			throw new RuntimeException();
		}
		return 1;
	}
}
