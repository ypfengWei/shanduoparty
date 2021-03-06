package com.shanduo.party.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ActivityScore;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.service.ReputationService;
import com.shanduo.party.service.ScoreService;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.SensitiveWord;
import com.shanduo.party.util.StringUtils;

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
	private ReputationService reputationService;
	
	@Autowired
	private ShanduoActivityMapper shanduoActivityMapper;
	
	@Override
	public int updateActivityScore(Integer userId, String activityId, Integer score, String evaluationcontent) {
		String content = SensitiveWord.filterInfo(evaluationcontent);
		int i = activityScoreMapper.updateByUserIdTwo(userId,activityId,score,content);
		if (i < 1) {
			log.error("评价失败");
			throw new RuntimeException();
		}
		if(score != 3) {
			if(!reputationService.getRecord(userId, shanduoActivityMapper.selectById(activityId), score)) {
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
				if(!reputationService.getRecord(shanduoActivityMapper.selectById(activityId), userId, score)) {
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
		List<String> scoreId = activityScoreMapper.selectId(time);
		for (String string : scoreId) {
			int i = activityScoreMapper.updateById(string);
			if(i < 1) {
				log.error("修改评分失败");
				throw new RuntimeException();
			}
			Map<String, Object> userIds = activityScoreMapper.selectById(string);
			Integer userId = Integer.parseInt(userIds.get("user_id").toString()); //被评论者id
			Integer otherUserId = Integer.parseInt(userIds.get("otheruser_id").toString()); //评论者id
			if(!reputationService.getRecord(otherUserId, userId, 5)) {
				log.error("信誉历史数据添加失败");
				throw new RuntimeException();
			}
		}
		return scoreId.size();
	}

	@Override
	public int updateByIdTime(String time) {
		List<String> scoreId = activityScoreMapper.selectIds(time);
		for (String string : scoreId) {
			int i = activityScoreMapper.updateByIdTime(string);
			if(i < 1) {
				log.error("修改评分失败");
				throw new RuntimeException();
			}
			Map<String, Object> userIds = activityScoreMapper.selectById(string);
			Integer userId = Integer.parseInt(userIds.get("user_id").toString()); //评论者id
			Integer otherUserId = Integer.parseInt(userIds.get("otheruser_id").toString()); //被评论者id
			if(!reputationService.getRecord(userId, otherUserId, 5)) {
				log.error("信誉历史数据添加失败");
				throw new RuntimeException();
			}
		}
		return scoreId.size();
	}

}
