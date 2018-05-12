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
    
    /**
     * 查询动态所有的一级回复
     * @Title: selectByDynamicId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param dynamicId
     * @param @return
     * @return List<Map<String,Object>>
     * @throws
     */
    List<Map<String, Object>> selectByDynamicId(String dynamicId);
    
    /**
     * 查询2级回复的所有2级回复
     * @Title: selectByCommentId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param commentId
     * @param @return
     * @return List<Map<String,Object>>
     * @throws
     */
    List<Map<String, Object>> selectByCommentId(String commentId);
    
    int updateByDelFlag(String id,Integer userId);
}