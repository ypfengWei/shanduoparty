package com.shanduo.party.service;

import java.util.Map;

public interface TaskService {
	
	/**
	 * 任务完成数量
	 * @Title: releaseRecord
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param userId
	 * @param @return    设定文件
	 * @return Map<String,Object>    返回类型
	 * @throws
	 */
	Map<String, Object> releaseRecord(Integer userId);
}
