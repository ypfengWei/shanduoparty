package com.shanduo.party.service;

import java.util.Map;

/**
 * 群组管理业务层
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
	 * @param @param groupType 群类型:1.200;2.500;3.1000
	 * @param @return
	 * @return int
	 * @throws
	 */
	int checkGroupType(Integer userId,String groupType);
	
	/**
	 * 创建群组
	 * @Title: saveGroup
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param name 群组名称
	 * @param @param groupId 群组ID
	 * @param @param groupType 群类型:1.200;2.500;3.1000
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveGroup(Integer userId,String name,String groupId,String groupType);
	
	/**
	 * 删除群组
	 * @Title: delGroup
	 * @Description: TODO
	 * @param @param userId
	 * @param @param groupId 群组ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int delGroup(Integer userId,String groupId);
	
	/**
	 * 修改群资料
	 * @Title: updateGroup
	 * @Description: TODO
	 * @param @param groupId
	 * @param @param name
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateGroup(String groupId,String name);
	
	/**
	 * 模糊查找群
	 * @Title: queryNameList
	 * @Description: TODO
	 * @param @param name
	 * @param @return
	 * @return Map<String, Object>
	 * @throws
	 */
	Map<String, Object> queryNameList(String name);
}
