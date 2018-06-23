package com.shanduo.party.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ReportRecord;
import com.shanduo.party.mapper.ReportRecordMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.service.ActivityService;
import com.shanduo.party.service.ReportService;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 举报操作实现类
 * @ClassName: ReportServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年6月23日 下午4:46:56
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReportServiceImpl implements ReportService {
	
	private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

	@Autowired
	private ReportRecordMapper recordMapper;

	@Autowired
	private ShanduoReputationMapper shanduoReputationMapper;
	
	@Autowired
	private ShanduoActivityMapper shanduoActivityMapper;
	
	@Autowired
	private ActivityService activityService;
	
	@Override
	public List<Map<String, Object>> reportRecord(String type, Integer pageNum, Integer pageSize) {
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		int totalrecord = 0;
		if("1".equals(type)) {
			totalrecord = recordMapper.activityCount();
			Page page = new Page(totalrecord, pageSize, pageNum);
			pageNum = (page.getPageNum()-1)*page.getPageSize();
			resultMap = recordMapper.selectActivityInfo(pageNum, page.getPageSize());
		} else {
			totalrecord = recordMapper.dynamicCount();
			Page page = new Page(totalrecord, pageSize, pageNum);
			pageNum = (page.getPageNum()-1)*page.getPageSize();
			resultMap = recordMapper.selectDynamicInfo(pageNum, page.getPageSize());
		}
		return resultMap;
	}
	
	@Override
	public List<Map<String, Object>> selectInfo(String activityId, String dynamicId) {
		List<Map<String, Object>> list = recordMapper.selectInfo(activityId,dynamicId); //举报的活动或动态
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list;
	}
	
	@Override
	public int updateReputation(String activityId, String type, String dynamicId) {
		List<Map<String, Object>> list = recordMapper.selectReportId(activityId, dynamicId);
		int reputation = 0;
		int reportId = 0;
		for (Map<String, Object> map : list) {
			reportId = Integer.parseInt(map.get("user_id").toString());
			reputation = shanduoReputationMapper.selectByUserId(reportId);
			int i = 0;
			if("1".equals(type)) {
				i = shanduoReputationMapper.updateByUserId(reportId, reputation+1);
			} else {
				i = shanduoReputationMapper.updateByUserId(reportId, reputation-1);
			}
			if(i < 1) {
				log.error("信誉等级修改失败");
				throw new RuntimeException();
			}
		}
		if("1".equals(type)) {
			int userIds = 0;
			if(StringUtils.isNull(activityId)) {
				userIds = recordMapper.selectUserId(dynamicId);
			} else {
				userIds = shanduoActivityMapper.selectUserId(activityId);
			}
			int deduction = shanduoReputationMapper.selectDeduction(userIds);
			int i = shanduoReputationMapper.updateDeduction(userIds, deduction+5);
			if(i < 1) {
				log.error("扣分失败");
				throw new RuntimeException();
			}
		}
		int i = recordMapper.deleteCount(activityId, dynamicId);
		if(i < 1) {
			log.error("删除举报记录失败");
			throw new RuntimeException();
		}
		int n = 0;
		if("1".equals(type)) {
			n = activityService.deleteActivity(activityId);
			if(n < 1) {
				log.error("删除活动失败");
				throw new RuntimeException();
			}
		} else{
			
		}
		return 1;
	}

	@Override
	public int report(Integer userId, String activityId, String dynamicId, String typeId, String remarks) {
		ReportRecord reportrecord = new ReportRecord();
		reportrecord.setId(UUIDGenerator.getUUID());
		reportrecord.setUserId(userId);
		if("1".equals(typeId)) {
			reportrecord.setActivityId(activityId);
		} else {
			reportrecord.setDynamicId(dynamicId);
		}
		reportrecord.setTypeId(typeId);
		reportrecord.setRemarks(remarks);
		int i = recordMapper.insertSelective(reportrecord);
		if(i < 1) {
			log.error("举报记录添加失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public String selectId(String activityId, String dynamicId, String typeId, Integer userId) {
		return recordMapper.selectId(activityId, dynamicId, typeId, userId);
	}

}
