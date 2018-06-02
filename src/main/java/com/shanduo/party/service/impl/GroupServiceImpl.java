package com.shanduo.party.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserGroup;
import com.shanduo.party.mapper.UserGroupMapper;
import com.shanduo.party.service.GroupService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.UUIDGenerator;

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
	
	/*
	 * 群类型:1.200群，2.500人群，3.1000人群
	 * 普通用户 2*200
	 * vip 5*200 2*500
	 * svip 5*200 3*500 2*1000
	 */
	public int getGroupCount(Integer vip, String groupType) {
		if(vip == 0) {
			switch (groupType) {
				case "1":
					return 2;
			}
		}else if(vip < 10) {
			switch (groupType) {
				case "1":
					return 5;
				case "2":
					return 2;
			}
		}else{
			switch (groupType) {
				case "1":
					return 5;
				case "2":
					return 3;
				case "3":
					return 2;
			}
		}
		return 0;
	}
	
	@Override
	public int checkGroupType(Integer userId, String groupType) {
		int count = groupMapper.checkGroupType(userId, groupType);
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
	public int saveGroup(Integer userId,String groupId,String groupType) {
		UserGroup group = new UserGroup();
		group.setId(UUIDGenerator.getUUID());
		group.setUserId(userId);
		group.setGroupId(groupId);
		group.setGroupType(groupType);
		int i = groupMapper.insertSelective(group);
		if(i < 1) {
			log.error("创建群聊失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public UserGroup selectByGroupId(String groupId) {
		return groupMapper.selectByGroupId(groupId);
	}
	
	@Override
	public boolean checkGroupCount(String groupId) {
		UserGroup group = selectByGroupId(groupId);
		if(group == null) {
			return true;
		}
		int count = group.getCount();
		if(group.getGroupType().equals("1")) {
			if(count >= 200) {
				return true;
			}
		}else if(group.getGroupType().equals("2")) {
			if(count >= 500) {
				return true;
			}
		}else {
			if(count >= 1000) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int updateGroup(String groupId, Integer count) {
		int i = groupMapper.updateGroupId(groupId, count);
		if(i < 1) {
			log.error("修改群聊失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int delGroup(Integer userId, String groupId) {
		int i = groupMapper.deleteGroupId(userId, groupId);
		if(i < 1) {
			log.error("删除群聊失败");
			throw new RuntimeException();
		}
		return 1;
	}

}
