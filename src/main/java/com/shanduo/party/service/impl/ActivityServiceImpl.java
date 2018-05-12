package com.shanduo.party.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
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

import com.shanduo.party.controller.ActivityController;
import com.shanduo.party.entity.ActivityRequirement;
import com.shanduo.party.entity.ActivityScore;
import com.shanduo.party.entity.ShanduoActivity;
import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.service.ActivityInfo;
import com.shanduo.party.mapper.ActivityRequirementMapper;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.mapper.ShanduoUserMapper;
import com.shanduo.party.service.ActivityService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.LocationUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;
import com.shanduo.party.util.WeekUtils;

/**
 * 活动操作实现类
 * @ClassName: ActivityServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年4月14日 下午3:17:47
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ActivityServiceImpl implements ActivityService {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ShanduoActivityMapper shanduoActivityMapper;

	@Autowired
	private ActivityRequirementMapper activityRequirementMapper;
	
	@Autowired
	private ActivityScoreMapper activityScoreMapper;
	
	@Autowired
	private ShanduoUserMapper shanduoUserMapper;
	
	@Override
	public List<ShanduoActivity> selectByActivityUserId(Integer userId, String time) {
		List<ShanduoActivity> shanduoActivities = shanduoActivityMapper.selectByActivityUserId(userId,time);
		if(shanduoActivities == null || shanduoActivities.isEmpty()) {
			return null;
		}
		return shanduoActivities;
	}

	@Override
	public boolean selectByAll(Integer userId, String activityId){
		ShanduoActivity shanduoActivity = shanduoActivityMapper.selectByPrimaryKey(activityId);
		long starttime = shanduoActivity.getActivityStartTime().getTime()-1*60*60*1000;
		long endtime = shanduoActivity.getActivityStartTime().getTime()+1*60*60*1000;
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String mintime = format.format(starttime);
		String maxtime = format.format(endtime);
		List<ShanduoActivity> shanduoActivities = shanduoActivityMapper.selectByAll(userId, mintime, maxtime);
		if(shanduoActivities.isEmpty() || shanduoActivities == null) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> selectByScoreActivity(String activityId, Integer pageNum, Integer pageSize) {
		int totalrecord = shanduoActivityMapper.selectByScoreActivityCount(activityId);
		if (totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<ActivityInfo> activityInfos = shanduoActivityMapper.selectByScoreActivity(activityId, pageNum,page.getTotalPage());
		if (activityInfos == null || activityInfos.isEmpty()) {
			log.error("参与人为空");
			return null;
		}
		for (ActivityInfo activityInfo : activityInfos) {
			if(!activityInfo.getBirthday().isEmpty()) {
				activityInfo.setAge(AgeUtils.getAgeFromBirthTime(activityInfo.getBirthday()));
			}
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityInfos);
		return resultMap;
	}
	
	@Override
	public boolean selectByTwoAll(Integer userId, String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long starttime = date.getTime()-1*60*60*1000;
		long endtime = date.getTime()+1*60*60*1000;
		String mintime = format.format(starttime);
		String maxtime = format.format(endtime);
		List<ShanduoActivity> shanduoActivities = shanduoActivityMapper.selectByAll(userId, mintime, maxtime);
		List<ShanduoActivity> list = shanduoActivityMapper.selectByActivityUserId(userId,time);
		if(shanduoActivities.isEmpty() || list.isEmpty() || shanduoActivities == null || list == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public int saveActivity(Integer userId, String activityType, String activityStartTime,
			String activityAddress, String mode, String manNumber,
			String womanNumber, String remarks, String activityCutoffTime, String lon, String lat) {
		ShanduoActivity activity = new ShanduoActivity();
		activity.setId(UUIDGenerator.getUUID());
		activity.setUserId(userId);
		activity.setActivityType(activityType);
		activity.setRemarks(remarks);
		activity.setLon(new BigDecimal(lon));
		activity.setLat(new BigDecimal(lat));
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm");
		try {  
            Date date = formatter.parse(activityStartTime);  
            activity.setActivityStartTime(date);
            Date datetime = formatter.parse(activityCutoffTime);
            activity.setActivityCutoffTime(datetime);
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
		activity.setActivityAddress(activityAddress);
		int i = shanduoActivityMapper.insertSelective(activity);
		if (i < 1) {
			log.error("活动添加失败");
			throw new RuntimeException();
		}
		ActivityRequirement requirement = new ActivityRequirement();
		requirement.setId(UUIDGenerator.getUUID());
		requirement.setActivityId(activity.getId());
		requirement.setMode(mode);
		if(!StringUtils.isNull(manNumber)) {
			requirement.setManNumber(Integer.parseInt(manNumber));
		} 
		if(!StringUtils.isNull(womanNumber)) {
			requirement.setWomanNumber(Integer.parseInt(womanNumber));
		} 
		int count = activityRequirementMapper.insertSelective(requirement);
		if (count < 1) {
			log.error("需求添加失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int deleteActivity(String activityId) {
		int i = shanduoActivityMapper.deleteByActivity(activityId);
//		long longtime = System.currentTimeMillis();
//		if(shanduoActivityMapper.selectByPrimaryKey(activityId).getActivityStartTime().getTime() < longtime) {
//			log.error("此活动不允许删除");
//			throw new RuntimeException();
//		}
		if(i < 1) {
			log.error("删除活动失败");
			throw new RuntimeException();
		}
		int count = activityRequirementMapper.deleteByActivityId(activityId);
		if(count < 1) {
			log.error("删除需求失败");
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public Map<String, Object> selectByScore(Integer pageNum, Integer pageSize, String lon,String lat) {
		int totalrecord = shanduoActivityMapper.selectByScoreCount();
		if(totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> activityInfos = shanduoActivityMapper.selectByScore(pageNum,page.getPageSize());
		if(activityInfos == null || activityInfos.isEmpty()) {
			return null;
		}
		activity(activityInfos, lon, lat);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityInfos);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByFriendsUserId(Integer userId, Integer pageNum, Integer pageSize, String lon,String lat) {
		int totalrecord = shanduoActivityMapper.selectByFriendsUserIdCount(userId);
		if(totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> activityInfos = shanduoActivityMapper.selectByFriendsUserId(userId, pageNum, page.getPageSize());
		if(activityInfos == null || activityInfos.isEmpty()) {
			log.error("活动为空");
			return null;
		}
		activity(activityInfos, lon, lat);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityInfos);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByNearbyUserId(String lon,String lat, Integer pageNum, Integer pageSize) {
		long longtime = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String createDate = format.format(longtime);
		Double[] doubles = LocationUtils.getDoubles(lon, lat);
		int totalrecord = shanduoActivityMapper.selectByNearbyUserIdCount(doubles[0], doubles[1], doubles[2], doubles[3], createDate);
		if(totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> activityInfos = shanduoActivityMapper.selectByNearbyUserId(doubles[0], doubles[1], 
				doubles[2], doubles[3], createDate, pageNum, page.getPageSize());
		if(activityInfos == null || activityInfos.isEmpty()) {
			log.error("活动为空");
			return null;
		}
		activity(activityInfos, lon, lat);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityInfos);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByUserId(Integer userId, Integer pageNum, Integer pageSize, String lon, String lat) {
		int totalrecord = shanduoActivityMapper.selectByUserIdCount(userId);
		if(totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> activityInfos = shanduoActivityMapper.selectByUserId(userId, pageNum, page.getPageSize());
		if(activityInfos == null || activityInfos.isEmpty()) {
			log.error("活动为空");
			return null;
		}
		activity(activityInfos, lon, lat);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityInfos);
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> selectByGender(String activityId) {
		List<Map<String, Object>> labelList= shanduoUserMapper.selectByGender(activityId);
		if(labelList == null || labelList.isEmpty()) {
			return null;
		}
		return labelList;
	}

	@Override
	public ShanduoUser selectById(Integer id) {
		ShanduoUser shanduoUser = shanduoUserMapper.selectByPrimaryKey(id);
		if(shanduoUser == null) {
			log.error("用户为空");
			return null;
		}
		return shanduoUser;
	}

	@Override
	public int insertSelective(Integer userId,String activityId) {
		ActivityScore activityScore = new ActivityScore();
		activityScore.setId(UUIDGenerator.getUUID());
		activityScore.setUserId(userId);
		activityScore.setActivityId(activityId);
		int i = activityScoreMapper.insertSelective(activityScore);
		if(i < 1) {
			log.error("参加活动失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public ActivityRequirement selectByNumber(String activityId) {
		ActivityRequirement activityRequirement = activityRequirementMapper.selectByNumber(activityId);
		if(activityRequirement == null) {
			log.error("要求为空");
			return null;
		}
		return activityRequirement;
	}

	@Override
	public ShanduoActivity selectByPrimaryKey(String id) {
		ShanduoActivity shanduoActivity = shanduoActivityMapper.selectByPrimaryKey(id);
		if(shanduoActivity == null) {
			log.error("活动为空");
			return null;
		}
		return shanduoActivity;
	}

	@Override
	public Map<String, Object> selectByUserIdTime(Integer userId, Integer pageNum, Integer pageSize, String lon, String lat) {
		long longtime = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String createDate = format.format(longtime);
		int totalrecord = shanduoActivityMapper.selectByUserIdTimeCount(userId, createDate);
		if(totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> activityInfos = shanduoActivityMapper.selectByUserIdTime(userId, createDate, pageNum, page.getPageSize());
		if(activityInfos == null || activityInfos.isEmpty()) {
			log.error("活动为空");
			return null;
		}
		activity(activityInfos, lon, lat);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityInfos);
		return resultMap;
	}

	@Override
	public Map<String, Object> selectByUserIdInTime(Integer userId, Integer pageNum, Integer pageSize, String lon, String lat) {
		long longtime = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String createDate = format.format(longtime);
		int totalrecord = shanduoActivityMapper.selectByUserIdInTimeCount(userId, createDate);
		if(totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> activityInfos = shanduoActivityMapper.selectByUserIdInTime(userId, createDate, pageNum, page.getPageSize());
		if(activityInfos == null || activityInfos.isEmpty()) {
			log.error("活动为空");
			return null;
		}
		activity(activityInfos, lon, lat);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", activityInfos);
		return resultMap;
	}

	@Override
	public Map<String, Object> selectByHistorical(Integer userId, Integer pageNum, Integer pageSize) {
		long longtime = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String createDate = format.format(longtime);
		int totalrecord =  shanduoActivityMapper.selectByHistoricalCount(userId, createDate);
		if(totalrecord == 0) {
			return null;
		}
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> list = shanduoActivityMapper.selectByHistorical(userId, createDate, pageNum, page.getPageSize());
		if(list == null || list.isEmpty()) {
			return null;
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", list);
		return resultMap;
	}
	
	@Override
	public int activityRefresh(String activityId) {
		int i = shanduoActivityMapper.activityRefresh(activityId);
		if(i < 1) {
			log.error("活动刷新失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updateBysetTop(String activityId) {
		int i = shanduoActivityMapper.updateBysetTop(activityId);
		if(i < 1) {
			log.error("活动置顶失败");
			throw new RuntimeException();
		}
		return 1;
	}
	
	public List<ActivityInfo> activity(List<ActivityInfo> activityInfos, String lon, String lat){
		for (ActivityInfo activityInfo : activityInfos) {
			if(!activityInfo.getBirthday().isEmpty()) {
				activityInfo.setAge(AgeUtils.getAgeFromBirthTime(activityInfo.getBirthday()));
			}
			double location = LocationUtils.getDistance(Double.parseDouble(lon), Double.parseDouble(lat), activityInfo.getLon(), activityInfo.getLat());
        	activityInfo.setLocation(location);
			if(0<Integer.parseInt(activityInfo.getWomanNumber()) && Integer.parseInt(activityInfo.getWomanNumber()) < 10) {
				DecimalFormat df = new DecimalFormat("00");
			    String str = df.format(Integer.parseInt(activityInfo.getWomanNumber()));
	    		activityInfo.setWomanNumber(str);
			}
			if(0<Integer.parseInt(activityInfo.getManNumber()) && Integer.parseInt(activityInfo.getManNumber()) < 10) {
				DecimalFormat df = new DecimalFormat("00");
			    String str = df.format(Integer.parseInt(activityInfo.getManNumber()));
	    		activityInfo.setManNumber(str);
			}
        	List<Map<String, Object>> shanduoUsers = shanduoUserMapper.selectByGender(activityInfo.getId());
    		if(shanduoUsers != null) {
    			for (Map<String, Object> map : shanduoUsers) {
    				int count = Integer.parseInt(map.get("count").toString());
	    			if(count < 10) {
	    			    if(map.get("gender").toString().equals("1")) {
	    			    	activityInfo.setManSum("0"+count);
	     				}else {
	     					activityInfo.setWomenSum("0"+count);
	     				}
	    			} else {
		    			if(map.get("gender").toString().equals("1")) {
					    	activityInfo.setManSum(count + "");
		 				}
		 				if(map.get("gender").toString().equals("0")) {
		 					activityInfo.setWomenSum(count + "");
		    			} 
	    			}
    			}
    		}
    		if(activityInfo.getManSum() == null) {
    			activityInfo.setManSum("0");
    		}
    		if(activityInfo.getWomenSum() == null) {
    			activityInfo.setWomenSum("0");
    		}
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
            String startString = activityInfo.getActivityStartTime();  
            String cutoffString = activityInfo.getActivityCutoffTime(); 
            StringBuffer buffer = new StringBuffer();
            String startTime = buffer.append(startString).substring(0,19);
            String cutoffTime = buffer.append(cutoffString).substring(0,19);
            activityInfo.setActivityStartTime(startTime);
            activityInfo.setActivityCutoffTime(cutoffTime);
            try {  
                Date startDate = formatter.parse(startString);  
                activityInfo.setActivityStartTime(startTime+"——"+WeekUtils.getWeek(startDate));
                Date cutoffDate = formatter.parse(cutoffString);  
                activityInfo.setActivityCutoffTime(cutoffString+"——"+WeekUtils.getWeek(cutoffDate));
            } catch (ParseException e) {  
                e.printStackTrace();  
            }  
            
		}
		return activityInfos;
	}
}
