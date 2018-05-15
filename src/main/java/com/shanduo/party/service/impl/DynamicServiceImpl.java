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
import com.shanduo.party.service.PraiseService;
import com.shanduo.party.util.SensitiveWord;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.LocationUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.UUIDGenerator;

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
	
	@Override
	public int saveDynamic(Integer userId, String content, String picture, String lat, String lon) {
		ShanduoDynamic dynamic = new ShanduoDynamic();
		dynamic.setId(UUIDGenerator.getUUID());
		dynamic.setUserId(userId);
		dynamic.setContent(SensitiveWord.filterInfo(content));
		dynamic.setPicture(picture);
		dynamic.setLat(new BigDecimal(lat));
		dynamic.setLon(new BigDecimal(lon));
		int i = dynamicMapper.insertSelective(dynamic);
		if(i < 1) {
			log.error("动态发表失败");
			throw new RuntimeException();
		}
		return 1;
	}

	/**
	 * 给返回的map添加其他信息
	 * @Title: putMap
	 * @Description: TODO
	 * @param @param resultList
	 * @param @param userId
	 * @return void
	 * @throws
	 */
	public void putMap(List<Map<String, Object>> resultList,Integer userId) {
		for (Map<String, Object> map : resultList) {
			String dynamicId = map.get("id").toString();
			//保存年龄
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			//保存头像图片URL
			Object portraitId = map.get("portraitId");
			if(portraitId == null) {
				portraitId = "";
			}
			map.put("portraitId", PictureUtils.getPictureUrl(portraitId.toString()));
			//动态图片
			Object picture = map.get("picture");
			if(picture == null) {
				picture = "";
			}
			map.put("picture", PictureUtils.getPictureUrlList(picture.toString()));
			//评论数量
			map.put("dynamicCount",commentMapper.dynamicIdCount(dynamicId));
			//点赞人数
			map.put("praise", praiseService.selectByCount(dynamicId));
			//用户是否点赞
			map.put("isPraise",praiseService.checkPraise(userId, dynamicId));
			//vip等级
			map.put("vip", 0);
		}
	}
	
	@Override
	public Map<String, Object> attentionList(Integer userId,Integer pageNum,Integer pageSize) {
		int totalRecord = dynamicMapper.attentionCount(userId);
		if(totalRecord == 0) {
			return null;
		}
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList =  dynamicMapper.attentionList(userId, pageNum, page.getPageSize());
		if(resultList == null) {
			return null;
		}
		putMap(resultList, userId);
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> nearbyList(Integer userId, String lat, String lon, Integer pageNum, Integer pageSize) {
		Double[] doubles = LocationUtils.getDoubles(lon, lat);
		int totalRecord = dynamicMapper.nearbyCount(doubles[0], doubles[1], doubles[2], doubles[3]);
		if(totalRecord == 0) {
			return null;
		}
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList =  dynamicMapper.nearbyList(doubles[0], doubles[1], doubles[2], doubles[3], pageNum, page.getPageSize());
		if(resultList == null) {
			return null;
		}
		putMap(resultList, userId);
		for (Map<String, Object> map : resultList) {
			double location = 
					LocationUtils.getDistance(Double.parseDouble(lon), Double.parseDouble(lat), Double.parseDouble(map.get("lon").toString()), Double.parseDouble(map.get("lat").toString()));
			map.put("location", location);
		}
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> dynamicList(Integer userId, Integer pageNum, Integer pageSize) {
		int totalRecord = dynamicMapper.selectMyCount(userId);
		if(totalRecord == 0) {
			return null;
		}
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<Map<String, Object>> resultList =  dynamicMapper.selectMyList(userId, pageNum, page.getPageSize());
		if(resultList == null) {
			return null;
		}
		putMap(resultList, userId);
		Map<String, Object> resultMap = new HashMap<>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectById(String dynamicId,Integer userId) {
		Map<String, Object> dynamic = dynamicMapper.selectById(dynamicId);
		if(dynamic == null) {
			log.error("动态为空");
			return null;
		}
		//保存年龄
		dynamic.put("age", AgeUtils.getAgeFromBirthTime(dynamic.get("age").toString()));
		//保存头像图片URL
		Object portraitId = dynamic.get("portraitId");
		if(portraitId == null) {
			portraitId = "";
		}
		dynamic.put("portraitId", PictureUtils.getPictureUrl(portraitId.toString()));
		//动态图片
		Object picture = dynamic.get("picture");
		if(picture == null) {
			picture = "";
		}
		dynamic.put("picture", PictureUtils.getPictureUrlList(picture.toString()));
		//评论数量
		dynamic.put("dynamicCount",commentMapper.dynamicIdCount(dynamicId));
		//点赞人数
		dynamic.put("praise", praiseService.selectByCount(dynamicId));
		//用户是否点赞
		dynamic.put("isPraise",praiseService.checkPraise(userId, dynamicId));
		//vip等级
		dynamic.put("vip", 0);
		//得到所有1级评论
		List<Map<String, Object>> commentList = commentMapper.selectByDynamicId(dynamicId);
		commentList(commentList);
		Map<String, Object> resultMap = new HashMap<>(2);
		resultMap.put("dynamic", dynamic);
		resultMap.put("comment", commentList);
		return resultMap;
	}
	
	/**
	 * 查询1级评论的图片,2级回复,2级回复图片
	 * @Title: commentList
	 * @Description: TODO
	 * @param @param commentList 1级评论集合
	 * @return void
	 * @throws
	 */
	public void commentList(List<Map<String, Object>> commentList) {
		for (int i = commentList.size()-1;i >= 0;i--) {
			Map<String, Object> oneCommentMap = commentList.get(i);
			String id = oneCommentMap.get("id").toString();
			if(oneCommentMap.get("delFlag").toString().equals("0")) {
				//1级评论图片
				Object picture = oneCommentMap.get("picture");
				if(picture == null) {
					picture = "";
				}
				oneCommentMap.put("picture",PictureUtils.getPictureUrlList(picture.toString()));
				oneCommentMap.put("twoComment", twoCommentList(id));
			}else {
				List<Map<String, Object>> twoCommentList = twoCommentList(id);
				if(twoCommentList.isEmpty()) {
					commentList.remove(i);
				}else {
					oneCommentMap.remove("id");
					oneCommentMap.remove("dynamicId");
					oneCommentMap.remove("userId");
					oneCommentMap.remove("name");
					oneCommentMap.remove("comment");
					oneCommentMap.remove("delFlag");
					oneCommentMap.put("twoComment",twoCommentList);
				}
			}
		}
	}

	/**
	 * 查询2级回复,2级回复图片
	 * @Title: twoCommentList
	 * @Description: TODO
	 * @param @param commentId
	 * @param @return
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> twoCommentList(String commentId) {
		List<Map<String, Object>> twoCommentList = commentMapper.selectByCommentId(commentId);
		if(twoCommentList.isEmpty()) {
			return twoCommentList;
		}
		//所有2级回复加入回复图片
		for (Map<String, Object> twoCommentMap : twoCommentList) {
			Object picture = twoCommentMap.get("picture");
			if(picture == null) {
				picture = "";
			}
			twoCommentMap.put("picture",PictureUtils.getPictureUrlList(picture.toString()));
		}
		return twoCommentList;
	}
	
	@Override
	public int saveDynamicComment(Integer userId, String dynamicId, String comment, String typeId, String commentId,String respondent,String picture) {
		String id = UUIDGenerator.getUUID();
		DynamicComment comments = new DynamicComment();
		comments.setId(id);
		comments.setUserId(userId);
		comments.setDynamicId(dynamicId);
		comments.setComment(SensitiveWord.filterInfo(comment));
		comments.setPicture(picture);
		comments.setReplyType(typeId);
		if("2".equals(typeId)) {
			comments.setCommentId(commentId);
			comments.setRespondent(Integer.parseInt(respondent));
		}
		int i = commentMapper.insertSelective(comments);
		if(i < 1) {
			log.error("评论失败");
			throw new RuntimeException();
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
				log.error("删除动态失败");
				throw new RuntimeException();
			}
		}
		return 1;
	}

	@Override
	public int hideComment(String commentId, Integer userId) {
		int i = commentMapper.updateByDelFlag(commentId, userId);
		if(i < 1) {
			log.error("删除评论失败");
			throw new RuntimeException();
		}
		return 1;
	}

}
