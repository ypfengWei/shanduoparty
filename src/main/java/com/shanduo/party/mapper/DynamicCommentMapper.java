package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.DynamicComment;

public interface DynamicCommentMapper {
    int deleteByPrimaryKey(String id);

    int insert(DynamicComment record);

    int insertSelective(DynamicComment record);

    DynamicComment selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DynamicComment record);

    int updateByPrimaryKey(DynamicComment record);
    
    int updateByDelFlag(String id,Integer userId);
    
    /**
     * 查询动态的评论数量
     * @Title: dynamicIdCount
     * @Description: TODO
     * @param @param dynamicId
     * @param @return
     * @return int
     * @throws
     */
    int dynamicIdCount(String dynamicId);
    
    /**
     * 查询动态的1级评论数量
     * @Title: commentCount
     * @Description: TODO
     * @param @param dynamicId
     * @param @return
     * @return int
     * @throws
     */
    int commentCount(String dynamicId);
    
    /**
     * 分页查询动态的一级回复
     * @Title: oneCommentIdList
     * @Description: TODO
     * @param @param dynamicId
     * @param @param page
     * @param @param pageSize
     * @param @return
     * @return List<Map<String,Object>>
     * @throws
     */
    List<Map<String, Object>> oneCommentIdList(String dynamicId,Integer page,Integer pageSize);
    
    /**
     * 查询1级评论的回复数量
     * @Title: commentsCount
     * @Description: TODO
     * @param @param commentId
     * @param @return
     * @return int
     * @throws
     */
    int commentsCount(String commentId);
    
    /**
     * 分页查询1级评论的2级回复
     * @Title: twoCommentIdList
     * @Description: TODO
     * @param @param commentId
     * @param @param page
     * @param @param pageSize
     * @param @return
     * @return List<Map<String,Object>>
     * @throws
     */
    List<Map<String, Object>> twoCommentIdList(String commentId,Integer page,Integer pageSize);
    
    /**
     * 查询单个1级评论
     * @Title: selectByCommentId
     * @Description: TODO
     * @param @param commentId
     * @param @return
     * @return Map<String,Object>
     * @throws
     */
    Map<String, Object> selectByCommentId(String commentId);
    
    int myMessageCount(Integer userId);
    
    List<Map<String, Object>> myMessage(Integer userId,Integer pageNum,Integer pageSize);
}