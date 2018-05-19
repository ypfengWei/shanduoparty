package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

/**
 * 好友业务层
 * @ClassName: AttentionService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月27日 上午9:19:36
 *
 */
public interface AttentionService {

	/**
	 * 检查是否已经添加好友或者拉黑
	 * @Title: checkAttention
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param attention 被添加人ID
	 * @param @param typeId 类型:1,好友;2,拉黑
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkAttention(Integer userId,Integer attention,String typeId);
	
	/**
	 * 检查是否已经申请添加好友
	 * @Title: checkAttentionApply
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param attention 被添加人ID
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkAttentionApply(Integer userId,Integer attention);
	
	/**
	 * 申请添加好友
	 * @Title: saveAttentionApply
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param attention 被添加人ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveAttentionApply(Integer userId,Integer attention);
	
	/**
	 * 查询所有申请添加我为好友的记录
	 * @Description: TODO
	 * @param @param attention 用户ID
	 * @param @param pageNum 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> attentionApplyList(Integer attention,Integer pageNum,Integer pageSize);
	
	/**
	 * 同意或拒绝添加好友
	 * @Title: isAttentionApply
	 * @Description: TODO
	 * @param @param applyId 申请记录ID
	 * @param @param attention 用户ID
	 * @param @param typeId 类型:1,同意;2,拒绝
	 * @param @return
	 * @return int
	 * @throws
	 */
	int isAttentionApply(String applyId,Integer attention,String typeId);
	
	/**
	 * 批量软删除申请添加我为好友的记录
	 * @Title: hideAttentionApply
	 * @Description: TODO
	 * @param @param applyIds 申请记录ID集合
	 * @param @param attention 用户ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int hideAttentionApply(String applyIds,Integer attention);
	
	/**
	 * 查询我的好友或黑名单
	 * @Title: attentionList
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param typeId 类型:1,好友;2,拉黑
	 * @param @return
	 * @return List<Map<String, Object>>
	 * @throws
	 */
	List<Map<String, Object>> attentionList(Integer userId,String typeId);
	
	/**
	 * 双向删除好友或删除黑名单
	 * @Title: delAttention
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param attention 好友ID或拉黑ID
	 * @param @param typeId 类型:1,删除好友;2,拉黑
	 * @param @return
	 * @return int
	 * @throws
	 */
	int delAttention(Integer userId,Integer attention,String typeId);
	
	/**
	 * 拉入黑名单
	 * @Title: blacklistAttention
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param attention 拉黑ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int blacklistAttention(Integer userId,Integer attention);
}
