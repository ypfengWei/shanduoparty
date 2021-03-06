package com.shanduo.party.service;

import java.util.Map;

/**
 * 动态业务层
 * @ClassName: DynamicService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月16日 下午3:25:56
 *
 */
public interface DynamicService {

	/**
	 * 发表动态
	 * @Title: saveDynamic
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param content 动态内容
	 * @param @param picture 图片或视频
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @param location 位置
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveDynamic(Integer userId,String content,String picture,String lat,String lon,String location);

	/**
	 * 检查动态是否已存在
	 * @Title: checkDynamic
	 * @Description: TODO
	 * @param @param dynamicId
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkDynamic(String dynamicId);
	
	/**
	 * 查看好友的动态
	 * @Title: attentionList
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param lookId 查看的人的ID
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @param pageNum 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> attentionList(Integer userId,String lat,String lon,Integer pageNum,Integer pageSize);
	
	/**
	 * 查看附件的人的动态
	 * @Title: nearbyList
	 * @Description: TODO
	 * @param @param lookId 查看的人的ID
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @param pageNum 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> nearbyList(Integer lookId,String lat,String lon,Integer pageNum,Integer pageSize);
	
	/**
	 * 个人动态
	 * @Title: dynamicList
	 * @Description: TODO
	 * @param @param userId 被查看用户Id
	 * @param @param lookId 查看的人的ID
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @param pageNum 页数
	 * @param @param pageSize 记录数
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> dynamicList(Integer userId,Integer lookId,String lat,String lon,Integer pageNum,Integer pageSize);
	
	/**
	 * 查看单个动态详情
	 * @Title: selectById
	 * @Description: TODO
	 * @param @param dynamicId 动态ID
	 * @param @param lookId 查看的人的ID
	 * @param @param lat 纬度
	 * @param @param lon 经度
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> selectById(String dynamicId,Integer lookId,String lat,String lon);
	
	/**
	 * 查看单个一级评论
	 * @Title: selectByCommentId
	 * @Description: TODO
	 * @param @param commentId
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> selectByCommentId(String commentId);
	
	/**
	 * 查看动态的1级评论
	 * @Title: commentList
	 * @Description: TODO
	 * @param @param dynamicId 动态ID
	 * @param @param pageNum 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> commentList(String dynamicId,Integer pageNum,Integer pageSize);
	
	/**
	 * 查看1级评论的2级回复
	 * @Title: commentsList
	 * @Description: TODO
	 * @param @param commentId
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> commentsList(String commentId,Integer pageNum,Integer pageSize);

	/**
	 * 评论动态和回复评论
	 * @Title: saveDynamicComment
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param dynamicId 动态ID
	 * @param @param comment 评论内容
	 * @param @param typeId 评论类型
	 * @param @param commentId 1级评论ID
	 * @param @param replyCommentId 回复的评论ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveDynamicComment(Integer userId,String dynamicId,String comment,String typeId,String commentId,String replyCommentId);
	
	
	/**
	 * 删除动态
	 * @Title: hideDynamic
	 * @Description: TODO
	 * @param @param dynamicId 动态ID
	 * @param @param userId 用户ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int hideDynamic(String dynamicId,Integer userId);
	
	/**
	 * 删除评论
	 * @Title: hideDynamicComment
	 * @Description: TODO
	 * @param @param commentId 评论ID
	 * @param @param userId 用户ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int hideComment(String commentId,Integer userId);
	
	/**
	 * 查询用户的动态数量
	 * @Title: dynamicCount
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int dynamicCount(Integer userId);
	
	/**
	 * 我的消息
	 * @Title: myMessage
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param pageNum 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> myMessage(Integer userId,Integer pageNum,Integer pageSize);
}
