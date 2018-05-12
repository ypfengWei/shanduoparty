package com.shanduo.party.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.controller.ActivityController;
import com.shanduo.party.entity.ActivityScore;
import com.shanduo.party.entity.ShanduoReputationRecord;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ShanduoReputationRecordMapper;
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.UUIDGenerator;

@Service
@Transactional(rollbackFor = Exception.class)
public class ScoreServiceImpl implements ScoreService {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ActivityScoreMapper activityScoreMapper;

//	@Autowired
//	private ShanduoReputationMapper shanduoReputationMapper;
	
	@Autowired
	private ShanduoReputationRecordMapper reputationRecordMapper;

	@Override
	public int saveActivityScore(Integer userId, String activityid, Integer score, String evaluationcontent) {
		ActivityScore activityScore = new ActivityScore();
		activityScore.setId(UUIDGenerator.getUUID());
		activityScore.setUserId(userId);
		activityScore.setActivityId(activityid);
		activityScore.setScore(score);
		activityScore.setEvaluationContent(evaluationcontent);
		int i = activityScoreMapper.insertSelective(activityScore);
		if (i < 1) {
			log.error("评价失败");
			throw new RuntimeException();
		}
//		ShanduoReputation shanduoReputation = shanduoReputationMapper.selectByPrimaryKey(userId);
		ShanduoReputationRecord shanduoReputationRecord = new ShanduoReputationRecord();
//		Integer reputation = null;
		shanduoReputationRecord.setId(UUIDGenerator.getUUID());
		shanduoReputationRecord.setUserId(userId);
		switch (score) {
			case 1:
			case 2:
//				reputation = shanduoReputation.getReputation() - 1;
				shanduoReputationRecord.setDeductionCount(1);
				shanduoReputationRecord.setReputationType("2");
				break;
			case 3:
//				reputation = shanduoReputation.getReputation();
				break;
			case 4:
			case 5:
//				reputation = shanduoReputation.getReputation() + 1;
				shanduoReputationRecord.setDeductionCount(1);
				shanduoReputationRecord.setReputationType("1");
				break;
			default:
				log.error("评分异常");
				break;
		}
		int n =reputationRecordMapper.insertSelective(shanduoReputationRecord);
		if(n < 1) {
			log.error("信誉历史记录添加失败");
			throw new RuntimeException();
		}
//		int count = shanduoReputationMapper.updateByUserId(userId, reputation);
//		if (count < 1) {
//			log.error("信誉修改失败");
//			throw new RuntimeException();
//		}
		return 1;
	}

	@Override
	public int updateByUserId(Integer userId, String activityId, Integer othersScore, String beEvaluated,
			String remarks) {
		int i = activityScoreMapper.updateByUserId(userId, activityId, othersScore, beEvaluated, remarks);
		if (i < 1) {
			log.error("评价失败");
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
		if (i < 1) {
			log.error("修改失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updateByIdTime(String time) {
		int i = activityScoreMapper.updateByIdTime(time);
		if (i < 1) {
			log.error("修改失败");
			throw new RuntimeException();
		}
		return 1;
	}
}
