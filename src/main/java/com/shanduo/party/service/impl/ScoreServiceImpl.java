package com.shanduo.party.service.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Map<String, Object> maps = activityScoreMapper.selectReputation(userId);
		int totalrecord = activityScoreMapper.activityCount(userId); //举办活动记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivity(userId, pageNum, page.getPageSize()); //举办的活动
		List<Map<String, Object>> lists = new ArrayList<>();
		for (Map<String, Object> map : list) {
			map.put("birthday", AgeUtils.getAgeFromBirthTime(map.get("birthday").toString()));
			map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
			map.put("vipGrade",vipService.selectVipLevel(userId)); //vip等级
			String activityId = map.get("id").toString();
			lists = activityScoreMapper.selectScore(activityId); //活动下的参与人与平均
			for (Map<String, Object> smap : lists) {
				smap.put("head_portrait_id", PictureUtils.getPictureUrl(smap.get("head_portrait_id").toString()));
			}
			map.put("lists", lists);
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("maps", maps);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", list);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectReleaseActivity(Integer userId, Integer pageNum, Integer pageSize) {
		int totalrecord = activityScoreMapper.activityCount(userId); //举办活动记录
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivity(userId, pageNum, page.getPageSize()); //举办的活动
		List<Map<String, Object>> lists = new ArrayList<>();
		for (Map<String, Object> map : list) {
			map.put("birthday", AgeUtils.getAgeFromBirthTime(map.get("birthday").toString()));
			map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
			map.put("vipGrade",vipService.selectVipLevel(userId)); //vip等级
			String activityId = map.get("id").toString();
			lists = activityScoreMapper.selectScore(activityId); //活动下的参与人与平均
			for (Map<String, Object> smap : lists) {
				smap.put("head_portrait_id", PictureUtils.getPictureUrl(smap.get("head_portrait_id").toString()));
			}
			map.put("lists", lists);
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("list", list);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectJoinActivity(Integer userId, Integer pageNum, Integer pageSize) {
		int totalrecord = activityScoreMapper.activityCounts(userId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> list = activityScoreMapper.selectActivitys(userId, pageNum, page.getPageSize());
		List<Map<String, Object>> lists = new ArrayList<>();
		for (Map<String, Object> map : list) {
			map.put("birthday", AgeUtils.getAgeFromBirthTime(map.get("birthday").toString()));
			map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
			map.put("vipGrade",vipService.selectVipLevel(userId));
			String activityId = map.get("id").toString();
			lists = activityScoreMapper.selectScores(activityId);
			for (Map<String, Object> smap : lists) {
				smap.put("head_portrait_id", PictureUtils.getPictureUrl(smap.get("head_portrait_id").toString()));
			}
			map.put("lists", lists);
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("list", list);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		return resultMap;
	}

}
