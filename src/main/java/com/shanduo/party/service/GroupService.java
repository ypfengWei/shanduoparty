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
	 * @Title: checkGroupType
	 * @Description: TODO
	 * @param @param userId
	 * @param @param groupType
	 * @param @return
	 * @return int
	 * @throws
	 */
	boolean checkGroupType(Integer userId,String groupType);
	
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
	int saveGroup(Integer userId,String groupId,String groupType);
	
	/**
	 * 检查群人数是否上限
	 * @Title: checkGroupCount
	 * @Description: TODO
	 * @param @param groupId
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkGroupCount(String groupId);
	
	/**
	 * 加入群组修改群人数
	 * @Title: updateGroup
	 * @Description: TODO
	 * @param @param groupId
	 * @param @param count
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateGroup(String groupId,Integer count);
	
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
