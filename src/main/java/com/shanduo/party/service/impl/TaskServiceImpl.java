package com.shanduo.party.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.UserTaskRecord;
import com.shanduo.party.mapper.UserTaskRecordMapper;
import com.shanduo.party.service.MoneyService;
import com.shanduo.party.service.TaskService;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 任务操作实现类
 * @ClassName: TaskServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月21日 上午10:19:30
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskServiceImpl implements TaskService {
	
	private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
	
	@Autowired
	private UserTaskRecordMapper taskRecordMapper;
	
	@Autowired
	private MoneyService moneyService;
	
	@Override
	public Map<String, Object> releaseRecord(Integer userId) {
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
	    String timeStr = format.format(new Date());
	    int i = taskRecordMapper.taskRecord(userId, timeStr, "0");//查询任务记录表中发布活动的记录
	    if(i < 2) {
	    	List<String> releaseRecord = taskRecordMapper.releaseRecord(timeStr, userId, 2-i); //查询发布活动成功的活动Id
	    	if(releaseRecord != null) {
	    		for (String id : releaseRecord) {
	    			saveTask(id, userId, "0");
	    			moneyService.payBeans(userId, 188, "3");//添加闪多豆
	    			i++;
	    		}
	    	}
	    }
	    int n = taskRecordMapper.taskRecord(userId, timeStr, "1");//查询任务记录表中参加活动的记录
	    if(n < 3) {
	    	List<String> joinRecord = taskRecordMapper.joinRecord(timeStr, userId, 3-n); //查询参加活动成功的活动Id
	    	if(joinRecord != null) {
	    		for (String id : joinRecord) {
	    			saveTask(id, userId, "1");
	    			moneyService.payBeans(userId, 88, "3");//添加闪多豆
	    			n++;
	    		}
	    	}
	    }
	    Map<String, Object> resultMap = new HashMap<String, Object>(2);
	    resultMap.put("release", i);
	    resultMap.put("join", n);
		return resultMap;
	}
	
	public int saveTask(String id, Integer userId, String type) {
		UserTaskRecord taskRecord = new UserTaskRecord();
		taskRecord.setId(UUIDGenerator.getUUID());
		taskRecord.setActivityId(id);
		taskRecord.setUserId(userId);
		taskRecord.setType(type);
		int a = taskRecordMapper.insertSelective(taskRecord);
		if(a < 1) {
			log.error("添加任务记录失败");
			throw new RuntimeException();
		}
		return 1;
	}
}
