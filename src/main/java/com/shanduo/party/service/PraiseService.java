package com.shanduo.party.service;

/**
 * 点赞业务层
 * @ClassName: PraiseService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月24日 下午4:49:25
 *
 */
public interface PraiseService {
	
	/**
	 * 添加点赞记录
	 * @Title: savePraise
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param dynamicId 动态ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int savePraise(Integer userId,String dynamicId);
	
	/**
	 * 检查用户是否已经点赞
	 * @Title: checkPraise
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param dynamicId 动态ID
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkPraise(Integer userId,String dynamicId);
	
	/**
	 * 取消点赞
	 * @Title: deletePraise
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param dynamicId 动态ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int deletePraise(Integer userId,String dynamicId);
	
	/**
	 * 查询动态或活动点赞人数
	 * @Title: selectByCount
	 * @Description: TODO
	 * @param @param dynamicId 动态ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int selectByCount(String dynamicId);
}
