package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.UserAttention;

public interface UserAttentionMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserAttention record);

    int insertSelective(UserAttention record);

    UserAttention selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserAttention record);

    int updateByPrimaryKey(UserAttention record);
    
    /**
     * 检查是否已经添加好友
     * @Title: checkAttention
     * @Description: TODO
     * @param @param userId
     * @param @param attention
     * @param @return
     * @return UserAttention
     * @throws
     */
    UserAttention checkAttention(Integer userId, Integer attention);
    
    /**
     * 好友数量
     * @Title: attentionCount
     * @Description: TODO
     * @param @param userId
     * @param @param typeId
     * @param @return
     * @return int
     * @throws
     */
    int attentionCount(Integer userId);
    
    /**
     * 好友或黑名单所有记录
     * @Title: selectAttentionList
     * @Description: TODO
     * @param @param userId
     * @param @param typeId
     * @param @return
     * @return List<Map<String,Object>>
     * @throws
     */
    List<Map<String, Object>> selectAttentionList(Integer userId,String typeId);
    
    /**
     * 删除好友或黑名单
     * @Title: deleteAttention
     * @Description: TODO
     * @param @param userId
     * @param @param attention
     * @param @param typeId
     * @param @return
     * @return int
     * @throws
     */
    int deleteAttention(Integer userId,Integer attention, String typeId);
}