package com.shanduo.party.service;

/**
 * 群管理业务层
 * @ClassName: GroupService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月31日 下午6:23:25
 *
 */
public interface GroupService {

	/**
	 * 检查是否可以创建群
	 * @Title: checkGroup
	 * @Description: TODO
	 * @param @param userId
	 * @param @param groupType
	 * @param @return
	 * @return int
	 * @throws
	 */
	int checkGroup(Integer userId,String groupType);
	
	/**
	 * 创建群组
	 * @Title: saveGroup
	 * @Description: TODO
	 * @param @param userId
	 * @param @param groupType
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveGroup(Integer userId,String groupType);
	
	/**
	 * 加入群组
	 * @Title: addGroup
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int addGroup(Integer userId);
	
	/**
	 * 删除群组
	 * @Title: delGroup
	 * @Description: TODO
	 * @param @param userId
	 * @param @param groupId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int delGroup(Integer userId,String groupId);
}
