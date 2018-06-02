package com.shanduo.party.service;

import com.shanduo.party.entity.UserGroup;

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
	 * @param @param userId
	 * @param @param groupType 群类型:1.200;2.500;3.1000
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveGroup(Integer userId,String groupId,String groupType);
	
	/**
	 * 查询单个群组详情
	 * @Title: selectByGroupId
	 * @Description: TODO
	 * @param @param groupId
	 * @param @return
	 * @return UserGroup
	 * @throws
	 */
	UserGroup selectByGroupId(String groupId);
	
	/**
	 * 检查群人数是否上限
	 * @Title: checkGroupCount
	 * @Description: TODO
	 * @param @param groupId 群组ID
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkGroupCount(String groupId);
	
	/**
	 * 修改群组人数
	 * @Title: updateGroup
	 * @Description: TODO
	 * @param @param groupId 群组ID
	 * @param @param count 人数
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
	 * @param @param groupId 群组ID
	 * @param @return
	 * @return int
	 * @throws
	 */
	int delGroup(Integer userId,String groupId);

}
