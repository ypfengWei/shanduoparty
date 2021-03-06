package com.shanduo.party.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.DynamicComment;
import com.shanduo.party.entity.ShanduoDynamic;
import com.shanduo.party.mapper.DynamicCommentMapper;
import com.shanduo.party.mapper.ShanduoDynamicMapper;
import com.shanduo.party.service.DynamicService;
import com.shanduo.party.service.ExperienceService;
import com.shanduo.party.service.PraiseService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.SensitiveWord;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.LocationUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.UUIDGenerator;
import com.shanduo.party.util.XGHighUtils;

/**
 * 
 * @ClassName: DynamicServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月16日 下午3:27:07
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DynamicServiceImpl implements DynamicService {

	private static final Logger log = LoggerFactory.getLogger(DynamicServiceImpl.class);
	
	@Autowired
	private ShanduoDynamicMapper dynamicMapper;
	@Autowired
	private DynamicCommentMapper commentMapper;
	@Autowired
	private PraiseService praiseService;
	@Autowired
	private VipService vipService;
	@Autowired
	private ExperienceService experienceService;
	
	@Override
	public int saveDynamic(Integer userId, String content, String picture, String lat, String lon,String location) {
		ShanduoDynamic dynamic = new ShanduoDynamic();
		dynamic.setId(UUIDGenerator.getUUID());
		dynamic.setUserId(userId);
		dynamic.setContent(SensitiveWord.filterInfo(content));
		if(picture != null) {
			picture = picture.replaceAll("\"","");
			picture = picture.replaceAll("\\[","");
			picture = picture.replaceAll("\\]","");
		}
		dynamic.setPicture(picture);
		dynamic.setLat(new BigDecimal(lat));
		dynamic.setLon(new BigDecimal(lon));
		dynamic.setLocation(location);
		int i = dynamicMapper.insertSelective(dynamic);
		if(i < 1) {
			log.error("动态发表失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public boolean checkDynamic(String dynamicId) {
		ShanduoDynamic dynamic = dynamicMapper.selectByPrimaryKey(dynamicId);
		if(dynamic == null || "1".equals(dynamic.getDelFlag())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 给返回的list添加其他信息
	 * @Title: addList
	 * @Description: TODO
	 * @param @param list
	 * @param @param lon
	 * @param @param lat
	 * @return void
	 * @throws
	 */
	public void addList(List<Map<String, Object>> list,Integer lookId,String lon,String lat) {
		for (Map<String, Object> map : list) {
			putMap(map, lookId, lon, lat);
		}
	}
	
	/**
	 * 给返回的map添加其他信息
	 * @Title: putMap
	 * @Description: TODO
	 * @param @param map
	 * @param @param lon
	 * @param @param lat
	 * @return void
	 * @throws
	 */
	public void putMap(Map<String, Object> map,Integer lookId,String lon,String lat) {
			String dynamicId = map.get("id").toString();
			//保存年龄
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			//保存头像图片URL
			map.put("portraitId", PictureUtils.getPictureUrl(map.get("portraitId")));
			//动态图片
			map.put("picture", PictureUtils.getPictureUrlList(map.get("picture")));
			//评论数量
			map.put("dynamicCount",commentMapper.dynamicIdCount(dynamicId));
			//点赞人数
			map.put("praise", praiseService.selectByCount(dynamicId));
			//是否点赞
			map.put("isPraise", praiseService.checkPraise(lookId, dynamicId));
			Integer userId = Integer.parseInt(map.get("userId").toString());
			//vip等级
			map.put("vip", vipService.selectVipLevel(userId));
			//等级
			map.put("level", experienceService.selectLevel(userId));
			//距离
			if(lon != null && lat != null) {
				double distance =
						LocationUtils.getDistance(Double.parseDouble(lon), Double.parseDouble(lat), Double.parseDouble(map.get("lon").toString()), Double.parseDouble(map.get("lat").toString()));
				map.put("distance", distance);
			}
	}
	
	@Override
	public Map<String, Object> attentionList(Integer userId,String lat,String lon,Integer pageNum,Integer pageSize) {
		int totalRecord = dynamicMapper.attentionCount(userId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList = dynamicMapper.attentionList(userId, pageNum, page.getPageSize());
		addList(resultList, userId, lon, lat);
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> nearbyList(Integer lookId,String lat,String lon, Integer pageNum, Integer pageSize) {
		double lons = Double.parseDouble(lon);
		double lats = Double.parseDouble(lat);
		int totalRecord = dynamicMapper.nearbyCount(lons, lats);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList =  dynamicMapper.nearbyList(lons, lats, pageNum, page.getPageSize());
		addList(resultList, lookId, lon, lat);
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> dynamicList(Integer userId,Integer lookId,String lat,String lon, Integer pageNum, Integer pageSize) {
		int totalRecord = dynamicMapper.selectMyCount(userId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList =  dynamicMapper.selectMyList(userId, pageNum, page.getPageSize());
		addList(resultList, lookId, lon, lat);
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectById(String dynamicId,Integer lookId,String lat,String lon) {
		Map<String, Object> resultMap = dynamicMapper.selectByDynamicId(dynamicId);
		if(resultMap == null) {
			return null;
		}
		putMap(resultMap, lookId, lon, lat);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByCommentId(String commentId) {
		Map<String, Object> resultMap = commentMapper.selectByCommentId(commentId);
		if(resultMap == null) {
			return null;
		}
		//保存头像图片URL
		resultMap.put("portraitId", PictureUtils.getPictureUrl(resultMap.get("portraitId")));
		//回复数量
		resultMap.put("count", commentMapper.commentsCount(resultMap.get("id").toString()));
		//保存年龄
		resultMap.put("age", AgeUtils.getAgeFromBirthTime(resultMap.get("age").toString()));
		return resultMap;
	}
	
	@Override
	public Map<String, Object> commentList(String dynamicId, Integer pageNum, Integer pageSize) {
		int totalRecord = commentMapper.commentCount(dynamicId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList = commentMapper.oneCommentIdList(dynamicId, pageNum, page.getPageSize());
		for (Map<String, Object> map : resultList) {
			//保存头像图片URL
			map.put("portraitId", PictureUtils.getPictureUrl(map.get("portraitId")));
			//回复数量
			map.put("count", commentMapper.commentsCount(map.get("id").toString()));
			//保存年龄
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			//3条2级回复
			List<Map<String, Object>> resultLists = commentMapper.twoCommentIdList(map.get("id").toString(), 0, 3);
			map.put("comments", resultLists);
		}
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

	@Override
	public Map<String, Object> commentsList(String commentId, Integer pageNum, Integer pageSize) {
		int totalRecord = commentMapper.commentsCount(commentId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList = commentMapper.twoCommentIdList(commentId, pageNum, page.getPageSize());
		for (Map<String, Object> map : resultList) {
			//保存头像图片URL
			map.put("portraitId", PictureUtils.getPictureUrl(map.get("portraitId")));
		}
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public int saveDynamicComment(Integer userId, String dynamicId, String comment, String typeId, String commentId,String replyCommentId) {
		String id = UUIDGenerator.getUUID();
		DynamicComment comments = new DynamicComment();
		comments.setId(id);
		comments.setUserId(userId);
		comments.setDynamicId(dynamicId);
		comments.setComment(SensitiveWord.filterInfo(comment));
		comments.setReplyType(typeId);
		if("2".equals(typeId)) {
			comments.setCommentId(commentId);
			comments.setReplyCommentId(replyCommentId);
		}
		int i = commentMapper.insertSelective(comments);
		if(i < 1) {
			log.error("评论失败");
			throw new RuntimeException();
		}
		//推送
		if("1".equals(typeId)) {
			ShanduoDynamic dynamic = dynamicMapper.selectByPrimaryKey(dynamicId);
			XGHighUtils.getInstance().pushSingleAccount("ShanDuo", "有人评论了你的动态", dynamic.getUserId(), 5, null);
		}else {
			DynamicComment commentTui = commentMapper.selectByPrimaryKey(replyCommentId);
			XGHighUtils.getInstance().pushSingleAccount("ShanDuo", "有人回复了你的评论", commentTui.getUserId(), 5, null);
		}
		return 1;
	}
	
	@Override
	public int hideDynamic(String dynamicId, Integer userId) {
		String[] dynamicIds = dynamicId.split(",");
		for (int i = 0; i < dynamicIds.length; i++) {
			if(StringUtils.isNull(dynamicIds[i])) {
				continue;
			}
			int n = dynamicMapper.updateByDelFlag(dynamicIds[i], userId);
			if(n < 1) {
				log.error("软删除动态失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	@Override
	public int hideComment(String commentId, Integer userId) {
		int i = commentMapper.updateByDelFlag(commentId, userId);
		if(i < 1) {
			log.error("软删除评论失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int dynamicCount(Integer userId) {
		return dynamicMapper.selectMyCount(userId);
	}

	@Override
	public Map<String, Object> myMessage(Integer userId, Integer pageNum, Integer pageSize) {
		int totalRecord = commentMapper.myMessageCount(userId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList = commentMapper.myMessage(userId, pageNum, page.getPageSize());
		for(Map<String, Object> map : resultList) {
			//头像
			map.put("portraitId", PictureUtils.getPictureUrl(map.get("portraitId")));
			//vip
			map.put("vip", vipService.selectVipLevel(Integer.valueOf(map.get("replyUserId").toString())));
			//保存年龄
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			//动态图片
			map.put("picture", PictureUtils.getPictureUrlList(map.get("picture")));
		}
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

}
