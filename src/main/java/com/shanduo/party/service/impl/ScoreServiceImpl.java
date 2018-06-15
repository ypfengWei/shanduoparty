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
import com.shanduo.party.entity.ReportRecord;
import com.shanduo.party.entity.ShanduoReputationRecord;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ReportRecordMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.mapper.ShanduoReputationRecordMapper;
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.ScoreUtils;
import com.shanduo.party.util.SensitiveWord;
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
	private ReportRecordMapper recordMapper;
	
	@Autowired
	private VipService vipService;
	
	@Override
	public int updateActivityScore(Integer userId, String activityId, Integer score, String evaluationcontent) {
		String content = SensitiveWord.filterInfo(evaluationcontent);
		int i = activityScoreMapper.updateByUserIdTwo(userId,activityId,score,content);
		if (i < 1) {
			log.error("评价失败");
			throw new RuntimeException();
		}
		if(score != 3) {
			if(!getRecord(userId, activityId, score, content, 1)) {
				log.error("信誉历史数据添加失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	@Override
	public int updateByUserId(String activityId, List<Map<String, Object>> list) {
		int len = list.size();
		for (int i = 0; i < len; i++) {
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
			String evaluated = SensitiveWord.filterInfo(map.get("evaluated").toString());
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
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
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
		Map<String, Object> initiatorMap = new HashMap<String, Object>(8);
		List<Map<String, Object>> scoreMapList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> maps : list) {
			if(activityIdSet.contains(maps.get("id").toString())) { //activityList中有活动id
				if(maps.get("uid").equals(userId)) { //用户为发起者
					getInitiatorMap(initiatorMap, maps, userId); //得到活动信息
				} else {
					getScoreMap(maps).clear(); //清除上一条评论信息
					scoreMapList.add(getScoreMap(maps)); //用户为参与者，添加评论信息到ScoreMapList中
				}
			} else { //activityList中没有活动id
				activityIdSet.add(maps.get("id").toString()); //给activityList赋活动Id的值
				if(!initiatorMap.isEmpty()) { //活动信息不为空循环获取评论信息到ScoreMapLists
					List<Map<String, Object>> scoreMapLists = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> scoremap : scoreMapList){
						Map<String, Object> newscoremap = new HashMap<String, Object>(6);
//						newscoremap.put("head_portrait_id", scoremap.get("head_portrait_id"));
//						newscoremap.put("score", scoremap.get("score"));
//						newscoremap.put("evaluation_content", scoremap.get("evaluation_content"));
//						newscoremap.put("others_score", scoremap.get("others_score"));
//						newscoremap.put("be_evaluated", scoremap.get("be_evaluated"));
//						newscoremap.put("user_name", scoremap.get("user_name"));
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
		initiatorMap.put("scoreList", scoreMapList);
		activityList.add(initiatorMap);
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		resultMap.put("map", map);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityList);
		updateReport(userId);
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
	public Map<String, Object> reportRecord(String typeId, Integer pageNum, Integer pageSize) {
		int totalrecord = recordMapper.selectCount(typeId); //举报活动或者动态记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = recordMapper.selectInfo(typeId, pageNum, page.getPageSize()); //举报的活动或动态
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("list", list);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		return resultMap;
	}
	
	@Override
	public int updateReputation(String activityId, String type, String dynamicId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(StringUtils.isNull(activityId)) {
			list = recordMapper.selectByDynamicId(dynamicId);
		} else {
			list = recordMapper.selectReportId(activityId);
		}
		int reputation = 0;
		int reportId = 0;
		for (Map<String, Object> map : list) {
			reportId = Integer.parseInt(map.get("user_id").toString());
			reputation = shanduoReputationMapper.selectByUserId(reportId);
			int i = 0;
			if("1".equals(type)) {
				i = shanduoReputationMapper.updateByUserId(reportId, reputation+1);
			} else {
				i = shanduoReputationMapper.updateByUserId(reportId, reputation-1);
			}
			if(i < 1) {
				log.error("信誉等级修改失败");
				throw new RuntimeException();
			}
		}
		if("1".equals(type)) {
			int userIds = 0;
			if(StringUtils.isNull(activityId)) {
				userIds = recordMapper.selectUserId(dynamicId);
			} else {
				userIds = shanduoActivityMapper.selectUserId(activityId);
			}
			int deduction = shanduoReputationMapper.selectDeduction(userIds);
			int i = shanduoReputationMapper.updateDeduction(userIds, deduction+5);
			if(i < 1) {
				log.error("扣分失败");
				throw new RuntimeException();
			}
		}
		int i = 0;
		if(StringUtils.isNull(activityId)) {
			i = recordMapper.updateByDynamicId(dynamicId);
		} else {
			i = recordMapper.updateByActivityId(activityId);
		}
		if(i < 1) {
			log.error("删除举报记录失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int report(Integer userId, String activityId, String dynamicId, String typeId, String remarks) {
		ReportRecord reportrecord = new ReportRecord();
		reportrecord.setId(UUIDGenerator.getUUID());
		reportrecord.setUserId(userId);
		if("1".equals(typeId)) {
			reportrecord.setActivityId(activityId);
		} else {
			reportrecord.setDynamicId(dynamicId);
		}
		reportrecord.setTypeId(typeId);
		reportrecord.setRemarks(remarks);
		int i = recordMapper.insertSelective(reportrecord);
		if(i < 1) {
			log.error("举报记录添加失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public String selectId(String activityId, Integer userId) {
		return recordMapper.selectId(activityId, userId);
	}

	@Override
	public String selectIds(String dynamicId, Integer userId) {
		return recordMapper.selectId(dynamicId, userId);
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
							if(scoreCount/lowScore < 2) { //在一次活动中差评所占百分比大于50%的扣一分信誉分
								int reputation = shanduoReputationMapper.selectByUserId(userId);
								int reputations = shanduoReputationMapper.updateByUserId(userId, reputation-1);
								if(reputations < 1) {
									log.error("信誉等级修改失败");
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
