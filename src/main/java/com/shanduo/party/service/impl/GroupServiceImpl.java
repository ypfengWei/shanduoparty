package com.shanduo.party.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.common.ConfigConsts;
import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.UserGroup;
import com.shanduo.party.im.ImUtils;
import com.shanduo.party.mapper.ShanduoUserMapper;
import com.shanduo.party.mapper.UserGroupMapper;
import com.shanduo.party.service.GroupService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;

/**
 * 
 * @ClassName: GroupServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月1日 上午9:07:53
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupServiceImpl implements GroupService {

	private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);
	
	@Autowired
	private UserGroupMapper groupMapper;
	@Autowired
	private VipService vipService;
	@Autowired
	private ShanduoUserMapper userMapper;
	
	/**
	 * 获得创群的次数
	 * 群类型:1.200群，2.500人群，3.1000人群
	 * 普通用户 2*200
	 * vip 5*200 2*500
	 * svip 5*200 3*500 2*1000
	 * @Title: getGroupCount
	 * @Description: TODO
	 * @param @param vip
	 * @param @param groupType
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int getGroupCount(Integer vip, String groupType) {
		if(vip == 0) {
			switch (groupType) {
				case "1":
					return 2;
				default:
					return 0;
			}
		}else if(vip < 10) {
			switch (groupType) {
				case "1":
					return 5;
				case "2":
					return 2;
				default:
					return 0;
			}
		}else{
			switch (groupType) {
				case "1":
					return 5;
				case "2":
					return 3;
				case "3":
					return 2;
				default:
					return 0;
			}
		}
	}
	
	@Override
	public int checkGroupType(Integer userId, String groupType) {
		int count = groupMapper.userGroupCount(userId, groupType);
		int vip = vipService.selectVipLevel(userId);
		int counts = getGroupCount(vip, groupType);
		if(counts == 0) {
			return 0;
		}
		if(count >= counts) {
			return 1;
		}
		return 2;
	}

	@Override
	public int saveGroup(Integer userId,String name,String groupId,String groupType) {
		UserGroup group = new UserGroup();
		group.setId(groupId);
		group.setUserId(userId);
		group.setName(name);
		group.setGroupType(groupType);
		int i = groupMapper.insertSelective(group);
		if(i < 1) {
			log.error("创建群聊失败");
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public int delGroup(Integer userId, String groupId) {
		int i = groupMapper.deleteGroup(userId, groupId);
		if(i < 1) {
			log.error("删除群聊失败");
			throw new RuntimeException();
		}
		if(ImUtils.delGroup(groupId)) {
			log.error("im删除群聊失败");
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public int updateGroup(String groupId, String name) {
		if(name != null) {
			UserGroup group = new UserGroup();
			group.setId(groupId);
			group.setName(name);
			int i = groupMapper.updateByPrimaryKeySelective(group);
			if(i < 1) {
				log.error("修改群聊失败");
				throw new RuntimeException();
			}
		}
//		if(ImUtils.setGroup(groupId, name)) {
//			log.error("im修改群聊失败");
//			throw new RuntimeException();
//		}
		return 1;
	}
	
	@Override
	public Map<String, Object> queryNameList(String name) {
		List<String> list = groupMapper.nameList(name);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return ImUtils.getGroupnfo(list);
	}

	@Override
	public Map<String, Object> getGroupUser(String groupId, Integer pageNum) {
		Map<String, Object> resultMap = ImUtils.getGroupUser(groupId, pageNum);
		if(resultMap.get("ActionStatus").toString().equals("OK")) {
        	String memberNum = resultMap.get("MemberNum").toString();
        	Page page = new Page(Integer.parseInt(memberNum), 20, pageNum);
        	resultMap.put("totalPage", page.getTotalPage());
        	resultMap.put("page", page.getPageNum());
        	@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("MemberList");
        	for(Map<String, Object> map : list) {
        		String userId = map.get("Member_Account").toString();
        		ShanduoUser user = userMapper.selectByPrimaryKey(Integer.valueOf(userId));
        		map.put("name", user.getUserName());
        		map.put("picture", ConfigConsts.PICTURE+PictureUtils.getPictureUrl(user.getHeadPortraitId()));
        	}
        }
		return resultMap;
	}

}
