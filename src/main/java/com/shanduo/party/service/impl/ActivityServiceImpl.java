package com.shanduo.party.service.impl;

import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ActivityRequirement;
import com.shanduo.party.entity.ActivityScore;
import com.shanduo.party.entity.ShanduoActivity;
import com.shanduo.party.entity.service.ActivityInfo;
import com.shanduo.party.mapper.ActivityRequirementMapper;
import com.shanduo.party.mapper.ActivityScoreMapper;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.service.ActivityService;
import com.shanduo.party.service.ExperienceService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.LocationUtils;
import com.shanduo.party.util.Page;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.SensitiveWord;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;
import com.shanduo.party.util.WeekUtils;
import com.shanduo.party.xg.XGHighUtils;

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

	private static final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

	@Autowired
	private ShanduoActivityMapper shanduoActivityMapper;

	@Autowired
	private ActivityRequirementMapper activityRequirementMapper;
	
	@Autowired
	private ActivityScoreMapper activityScoreMapper;
	
	@Autowired
	private VipService vipService;
	
	@Autowired
	private ExperienceService experienceService;
	
	@Override
	public boolean selectByAll(Integer userId, String activityId){
		//根据活动id查询活动信息
		ShanduoActivity shanduoActivity = shanduoActivityMapper.selectByIds(activityId);
		long starttime = shanduoActivity.getActivityStartTime().getTime()-1*60*60*1000;
		long endtime = shanduoActivity.getActivityStartTime().getTime()+1*60*60*1000;
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String mintime = format.format(starttime);
		String maxtime = format.format(endtime);
		//查询此时间段该用户有无参加或举办其他活动
		int i = shanduoActivityMapper.selectByAll(userId, mintime, maxtime);
		if(i == 0) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> selectByScoreActivity(String activityId, Integer pageNum, Integer pageSize) {
		int totalrecord = shanduoActivityMapper.selectByScoreActivityCount(activityId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByScoreActivity(activityId, pageNum, page.getPageSize());
		for (ActivityInfo activityInfo : resultList) {
			if(!activityInfo.getBirthday().isEmpty()) {
				activityInfo.setAge(AgeUtils.getAgeFromBirthTime(activityInfo.getBirthday()));
			}
			if(activityInfo.getOthersScore() == null) {
				activityInfo.setTypeId(2);//发起者未评价
			} else {
				activityInfo.setTypeId(3);//发起者已评价
			}
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByActivityId(String activityId, Integer pageNum, Integer pageSize, Integer userId, int joinActivity) {
		int totalrecord = shanduoActivityMapper.selectByScoreActivityCount(activityId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<Map<String, Object>> resultList = shanduoActivityMapper.selectByActivityId(activityId, pageNum, page.getPageSize());
		for (Map<String, Object> map : resultList) {
			map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
			if(map.get("id").equals(userId)) {
				joinActivity = 1;
				map.put("joinActivity", joinActivity);
				break;
			} else {
				map.put("joinActivity", joinActivity);//joinActivity 0:未参加 1:已参加 2:未登陆
			}
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public boolean selectByTwoAll(Integer userId, String time) {
		int count = shanduoActivityMapper.selectByActivityUserId(userId,time);
		if(count == 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public String saveActivity(Integer userId, String activityName, String activityStartTime,
			String activityAddress, String mode, String manNumber,String womanNumber, String remarks,
			String activityCutoffTime, String lon, String lat, String detailedAddress) {
		ShanduoActivity activity = new ShanduoActivity();
		activity.setId(UUIDGenerator.getUUID());
		activity.setUserId(userId);
		activity.setActivityName(SensitiveWord.filterInfo(activityName));
		activity.setRemarks(SensitiveWord.unicodeInfo(remarks));
		activity.setDetailedAddress(SensitiveWord.filterInfo(detailedAddress));
		activity.setLon(new BigDecimal(lon));
		activity.setLat(new BigDecimal(lat));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {  
            Date date = formatter.parse(activityStartTime);  
            activity.setActivityStartTime(date);
            Date datetime = formatter.parse(activityCutoffTime);
            activity.setActivityCutoffTime(datetime);
        } catch (ParseException e) {  
            e.printStackTrace();  
        }
		activity.setActivityAddress(activityAddress.split(",")[0]);
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
		return activity.getId();
	}

	@Override
	public int deleteActivity(String activityId, Integer userId) {
		int i = shanduoActivityMapper.deleteByActivity(activityId,userId);
		if(i < 1) {
			log.error("删除活动失败");
			throw new RuntimeException();
		}
		int count = activityRequirementMapper.deleteByActivityId(activityId);
		if(count < 1) {
			log.error("删除需求失败");
			throw new RuntimeException();
		}
		List<String> userIds = activityScoreMapper.selectUserId(activityId);
		int score = activityScoreMapper.deleteByActivityId(activityId);
		if(score < 1) {
			log.error("没有用户参加活动");
		}
		ShanduoActivity activity = shanduoActivityMapper.selectActivityName(activityId);
		if(activity.getActivityStartTime().getTime() > System.currentTimeMillis()) {
			XGHighUtils.getInstance().pushAccountList("闪多", activity.getActivityName()+"被组织者解散", userIds, 4);
		}
		return 1;
	}
	
	@Override
	public Map<String, Object> selectByScore(Integer pageNum, Integer pageSize, String lon,String lat) {
		int totalrecord = shanduoActivityMapper.selectByScoreCount();
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByScore(pageNum,page.getPageSize());
		activity(resultList, lon, lat, 0);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByFriendsUserId(Integer userId, Integer pageNum, Integer pageSize, String lon,String lat) {
		int totalrecord = shanduoActivityMapper.selectByFriendsUserIdCount(userId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByFriendsUserId(userId, pageNum, page.getPageSize());
		activity(resultList, lon, lat, 2);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByNearbyUserId(String lon,String lat, Integer pageNum, Integer pageSize) {
		long longtime = System.currentTimeMillis();
		Date time = new Date(longtime - 1000*60*60*12);
		shanduoActivityMapper.updateById(time); // 置顶超过12小时的活动取消置顶
		double lons = Double.parseDouble(lon);
		double lats = Double.parseDouble(lat);
		int totalrecord = shanduoActivityMapper.selectByNearbyUserIdCount(lons, lats);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByNearbyUserId(lons, lats, pageNum, page.getPageSize());
		activity(resultList, lon, lat, 0);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectByUserId(Integer userId, Integer pageNum, Integer pageSize, String lon, String lat) {
		int totalrecord = shanduoActivityMapper.selectByUserIdCount(userId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByUserId(userId, pageNum, page.getPageSize());
		activity(resultList, lon, lat,1);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> selectByGender(String activityId) {
		List<Map<String, Object>> labelList= activityScoreMapper.selectByGender(activityId);
		if(labelList == null || labelList.isEmpty()) {
			return null;
		}
		return labelList;
	}

	@Override
	public int insertSelective(Integer userId,String activityId,String activityName,Integer userIds) {
		ActivityScore activityScore = new ActivityScore();
		activityScore.setId(UUIDGenerator.getUUID());
		activityScore.setUserId(userId);
		activityScore.setActivityId(activityId);
		Map<String, Object> user = shanduoActivityMapper.selectUserName(userId);
		activityScore.setGender(user.get("gender").toString());
		int i = activityScoreMapper.insertSelective(activityScore);
		if(i < 1) {
			log.error("参加活动失败");
			throw new RuntimeException();
		}
		//给组织者推送消息
		XGHighUtils.getInstance().pushSingleAccount("闪多", user.get("user_name")+"参加了您的"+activityName+"活动", userIds, 3, activityId);
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
		ShanduoActivity shanduoActivity = shanduoActivityMapper.selectByIds(id);
		if(shanduoActivity == null) {
			log.error("活动为空");
			return null;
		}
		return shanduoActivity;
	}

	@Override
	public Map<String, Object> selectByUserIdTime(Integer userId, Integer pageNum, Integer pageSize, String lon, String lat) {
		int totalrecord = shanduoActivityMapper.selectByUserIdTimeCount(userId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByUserIdTime(userId, pageNum, page.getPageSize());
		for (ActivityInfo activityInfo : resultList) {
			activityInfo.setTypeId(9);
		}
		activity(resultList, lon, lat, 0);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

	@Override
	public Map<String, Object> selectByUserIdInTime(Integer userId, Integer pageNum, Integer pageSize, String lon, String lat) {
		int totalrecord = shanduoActivityMapper.selectByUserIdInTimeCount(userId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByUserIdInTime(userId, pageNum, page.getPageSize());
		for (ActivityInfo activityInfo : resultList) {
			if(activityInfo.getScore() == null) {
				activityInfo.setTypeId(0); //参与者未评价
			} else {
				activityInfo.setTypeId(1); //参与者已评价
			}
		}
		activity(resultList, lon, lat, 0);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
		return resultMap;
	}

	@Override
	public Map<String, Object> selectByHistorical(Integer userId, Integer pageNum, Integer pageSize) {
		int totalrecord = shanduoActivityMapper.selectByHistoricalCount(userId);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> resultList = shanduoActivityMapper.selectByHistorical(userId, pageNum, page.getPageSize());
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		resultMap.put("list", resultList);
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
	
	@Override
	public int deleteByUserId(String activityId, Integer token, String activityName, Integer userId) {
		int i = activityScoreMapper.deleteByUserId(activityId, token);
		if(i < 1) {
			log.error("取消活动失败");
			throw new RuntimeException();
		} 
		Map<String, Object> user = shanduoActivityMapper.selectUserName(token);
		//给组织者推送消息
		XGHighUtils.getInstance().pushSingleAccount("闪多", user.get("user_name")+"退出了您的"+activityName+"活动", userId, 3, activityId);
		return 1;
	}
	
	@Override
	public int deleteByUserIds(String activityId, String userIds, String activityName, Integer initiatorId) {
//		String[] userId = userIds.split(",");
//		int len = userId.length;
//		for(int i=0;i< len;i++) {
//			int n = activityScoreMapper.deleteByUserId(activityId, Integer.parseInt(userId[i]));
//			if(n < 1) {
//				log.error("踢人失败");
//			}
//		}
		int userId = Integer.parseInt(userIds);
		int n = activityScoreMapper.deleteByUserId(activityId, userId);
		if(n < 1) {
			log.error("踢人失败");
		}
		Map<String, Object> user = shanduoActivityMapper.selectUserName(initiatorId);
		XGHighUtils.getInstance().pushSingleAccount("闪多", "您已被"+user.get("user_name")+"请出了他的"+activityName+"活动", userId, 3, activityId);
		return 1;
	}
	
	public List<ActivityInfo> activity(List<ActivityInfo> resultLists, String lon, String lat,int type){
		List<ActivityInfo>  resultList =  new ArrayList<ActivityInfo>();
		int len = resultLists.size();
		for(int i=0;i<len;i++){
			ActivityInfo activityInfo = resultLists.get(i);
			resultList.add(showActivity(activityInfo, lon, lat,type));
		}
		return resultList;
	}
	
	public ActivityInfo showActivity(ActivityInfo activityInfo, String lon, String lat, int type){
		activityInfo.setAge(AgeUtils.getAgeFromBirthTime(activityInfo.getBirthday()));
		if(!StringUtils.isNull(lat)&&!StringUtils.isNull(lon)) {
			double location = LocationUtils.getDistance(Double.parseDouble(lon), Double.parseDouble(lat), activityInfo.getLon(), activityInfo.getLat());
			activityInfo.setLocation(location);
		}
		int userId = activityInfo.getUserId();
		activityInfo.setLevel(experienceService.selectLevel(userId));
    	activityInfo.setVipGrade(vipService.selectVipLevel(userId));
    	List<Map<String, Object>> resultMap = activityScoreMapper.selectByGender(activityInfo.getId());//获取男女生参与活动的人数
    	String womanNumber = activityInfo.getWomanNumber();
    	String manNumber = activityInfo.getManNumber();
		if(resultMap != null) {
			for (Map<String, Object> map : resultMap) {
				int count = Integer.parseInt(map.get("count").toString());
    			if(count < 10) {
    			    if(map.get("gender").toString().equals("0")) { //参加活动的女生人数
    			    	if(0 < Integer.parseInt(womanNumber) && Integer.parseInt(womanNumber) < 10) {
    			    		//如果参加的女生人数以及活动要求的女生人数小于10则在前面加0
    			    		activityInfo.setWomanNumber("0"+count+"/"+"0"+womanNumber);
    					} else {
    						//如果参加的女生人数小于10，活动要求的女生人数大于10则在参加的女生人数前面加0
     						activityInfo.setWomanNumber("0"+count+"/"+womanNumber);
     					}
     				}else {//参加活动的男生人数
     					if(0 < Integer.parseInt(manNumber) && Integer.parseInt(manNumber) < 10) {
     						//如果参加的男生人数以及活动要求的男生人数小于10则在前面加0
     			    		activityInfo.setManNumber("0"+count+"/"+"0"+manNumber);
     					} else {
     						//如果参加的男生人数小于10，活动要求的男生人数大于10则在参加的女生人数前面加0
     						activityInfo.setManNumber("0"+count+"/"+manNumber);
     					}
     				}
    			} else { //因为参加的人数不能大于活动要求的人数，故不做其他限制
	    			if(map.get("gender").toString().equals("1")) { //参加活动的男生人数
	    				//如果参加的男生人数大于10则不做改变
				    	activityInfo.setManNumber(count+"/"+manNumber);
	 				}
	 				if(map.get("gender").toString().equals("0")) { //参加活动的女生人数
	 					//如果参加的女生人数大于10则不做改变
	 					activityInfo.setWomanNumber(count+"/"+womanNumber);
	    			} 
    			}
			}
		}
		//参加人数为0
		if(activityInfo.getManNumber().matches("^\\d+$")) {
			if(0 < Integer.parseInt(activityInfo.getManNumber()) && Integer.parseInt(activityInfo.getManNumber()) < 10) {
				activityInfo.setManNumber("0/"+"0"+activityInfo.getManNumber());
			} else{
				activityInfo.setManNumber("0/"+activityInfo.getManNumber());
			}
		}
		if(activityInfo.getWomanNumber().matches("^\\d+$")) {
			if(0 < Integer.parseInt(activityInfo.getWomanNumber()) && Integer.parseInt(activityInfo.getWomanNumber()) < 10) {
				activityInfo.setWomanNumber("0/"+"0"+activityInfo.getWomanNumber());
			} else {
				activityInfo.setWomanNumber("0/"+activityInfo.getWomanNumber());
			}
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
        String startString = activityInfo.getActivityStartTime();  
        String cutoffString = activityInfo.getActivityCutoffTime();
        if(type != 0) {
        	Long newstartTime = getLongTime(startString, null, 2);
        	Long nowTime = System.currentTimeMillis();
        	if(type == 1) {
        		Long newcutoffTime = getLongTime(cutoffString, null, 2);
        		Map<String, Object> map = shanduoActivityMapper.numberAndScore(activityInfo.getId());
        		int i = Integer.parseInt(map.get("number").toString()); //参加记录
        		int count = Integer.parseInt(map.get("score").toString()); //评分记录
        		if(nowTime < newcutoffTime) {
        			activityInfo.setTypeId(4);
        		} else if(newcutoffTime < nowTime && nowTime < newstartTime) {
        			activityInfo.setTypeId(5);
        		} else if(i > 0){
        			if(count > 0) {
        				if(i > count) {
        					activityInfo.setTypeId(2);
        				} else {
        					activityInfo.setTypeId(3);
        				} 
        			} else {
        				activityInfo.setTypeId(2);
        			}
        		} else {
        			activityInfo.setTypeId(2);
        		}
        	} else {
        		if(nowTime < newstartTime) {
        			activityInfo.setTypeId(6); //活动未结束
        		} else {
        			activityInfo.setTypeId(7); //活动结束
        		}
        	}
        }
        //转换时间格式，活动开始时间如2018/6/19/9:49星期二，活动报名截止时间如2018/6/18/9:49
        String startTime = startString.substring(0,16).replace("-", "/").replace(" ", "/");
        String cutoffTime = cutoffString.substring(0,16).replace("-", "/").replace(" ", "/");
        Long time = getLongTime(null, "yyyy-MM-dd 00:00", 1);
        Long endTime = getLongTime(null, "yyyy-MM-dd 23:59", 1);
        Long times = getLongTime(cutoffString, null, 2);
        String nowTime = formatter.format(new Date());
        //如果活动报名截止时间与系统时间在同一天，则转换成9:49
        if(time <= times && times <= endTime) {
        	cutoffTime = cutoffTime.substring(cutoffTime.length()-5, cutoffTime.length());
        } else if(nowTime.substring(0, 4).equals(cutoffString.substring(0, 4))) {
        	//如果活动报名截止时间与系统时间在同一年，则转换成6/18/9:49
        	cutoffTime = cutoffTime.substring(cutoffTime.length()-11, cutoffTime.length());
        }
        try {
            Date startDate = formatter.parse(startString);  
            activityInfo.setActivityStartTime(startTime+WeekUtils.getWeek(startDate));
            activityInfo.setActivityCutoffTime(cutoffTime);
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
		return activityInfo;
	}
	
	@Override
	public ActivityInfo activityDetails(String activityId, String lon, String lat) {
		ActivityInfo activityInfo = shanduoActivityMapper.selectByActivityIds(activityId);
		if(activityInfo == null) {
			return null;
		}
		showActivity(activityInfo, lon, lat, 2);
		return activityInfo;
	}
	
	@Override
	public Map<String, Object> selectByActivityIds(String activityId, Integer userId) {
		ActivityInfo activityInfo = shanduoActivityMapper.selectByActivityIds(activityId);
		if(activityInfo == null) {
			return null;
		}
		showActivity(activityInfo, null, null, 0);
		List<Map<String, Object>> resultList = shanduoActivityMapper.selectActivityIds(activityId);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		int joinActivity = 0; //joinActivity 0:未参加 1:已参加
		for (Map<String, Object> map : resultList) {
			if(userId != null && map.get("id").equals(userId)) {
				joinActivity = 1;
			}
			map.put("head_portrait_id", PictureUtils.getPictureUrl(map.get("head_portrait_id").toString()));
		}
		resultMap.put("joinActivity", joinActivity);
		resultMap.put("activityInfo", activityInfo);
		resultMap.put("resultList", resultList);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> selectQuery(String query, String lon, String lat, Integer pageNum, Integer pageSize) {
		int totalrecord = shanduoActivityMapper.queryCount(query);
		Page page = new Page(totalrecord, pageSize, pageNum);
		pageNum = (page.getPageNum()-1)*page.getPageSize();
		List<ActivityInfo> list = shanduoActivityMapper.selectQuery(query, pageNum, page.getPageSize());
		activity(list, lon, lat, 0);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("list", list);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalpage", page.getTotalPage());
		return resultMap;
	}

	@Override
	public int selectByGenders(String activityId, String gender) {
		return activityScoreMapper.selectByGenders(activityId, gender);
	}

	@Override
	public String selectByUserId(Integer userId) {
		return activityScoreMapper.selectByUserId(userId);
	}
	
	@Override
	public boolean selectRecord(Integer userId) {
		int i = shanduoActivityMapper.selectRecord(userId);
		if(i == 0) {
			return false;
		}
		return true;
//		return shanduoActivityMapper.selectRecord(userId);
	}
	
	public Long getLongTime(String time, String timeType, Integer type) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		Date cutoff = new Date();
		try {
			if(type == 1) {
				Format format = new SimpleDateFormat(timeType);
				String createDate = format.format(cutoff);
				cutoff = formatter.parse(createDate);
			} else {
				cutoff = formatter.parse(time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cutoff.getTime();
	}

}
