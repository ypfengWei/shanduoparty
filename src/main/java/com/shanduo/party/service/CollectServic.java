package com.shanduo.party.service;

import java.util.Map;

/**
 * 收藏业务层
 * @ClassName: CollectServic
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月26日 下午2:49:24
 *
 */
public interface CollectServic {

	/**
	 * 添加收藏记录
	 * @Title: saveCollect
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param fileUrl 文件URL
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveCollect(Integer userId,String fileUrl);
	
	/**
	 * 检查是否收藏
	 * @Title: checkCollect
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param collectId 收藏ID集合
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkCollect(Integer userId,String collectId);
	
	/**
	 * 批量删除收藏记录
	 * @Title: deleteCollect
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param collectId 收藏ID集合
	 * @param @return
	 * @return int
	 * @throws
	 */
	int deleteCollect(Integer userId,String collectId);
	
	/**
	 * 分页查看收藏
	 * @Title: selectByUserList
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param pageNum 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	Map<String, Object> selectByUserList(Integer userId,Integer pageNum,Integer pageSize);
}
