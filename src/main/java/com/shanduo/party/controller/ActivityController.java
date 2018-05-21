package com.shanduo.party.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.ActivityRequirement;
import com.shanduo.party.entity.ShanduoActivity;
import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.UserToken;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.ActivityService;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.ExperienceService;
import com.shanduo.party.util.PatternUtils;
import com.shanduo.party.util.StringUtils;

/**
 * 活动控制层
 * @ClassName: ActivityController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年4月16日 下午3:12:29
 */
@Controller
@RequestMapping(value = "activity")
public class ActivityController {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ActivityService activityService;

	@Autowired
	private BaseService baseService;

	@Autowired
	private ExperienceService experienceService;
	
	/**
	 * 发布活动
	 * @Title: saveActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityType
	 * @param @param activityStartTime
	 * @param @param activityAddress
	 * @param @param mode
	 * @param @param manNumber
	 * @param @param womanNumber
	 * @param @param remarks
	 * @param @param activityCutoffTime
	 * @param @param lon
	 * @param @param lat
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "saveactivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean saveActivity(HttpServletRequest request, String token, String activityName,
			String activityStartTime, String activityAddress, String mode, String manNumber, String womanNumber,
			String remarks, String activityCutoffTime, String lon, String lat, String detailedAddress) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if (StringUtils.isNull(activityName)) {
			log.error("标题为空");
			return new ErrorBean("标题为空");
		}
		if (StringUtils.isNull(activityStartTime)) {
			log.error("活动开始时间为空");
			return new ErrorBean("活动开始时间为空");
		}
		if (StringUtils.isNull(manNumber) && StringUtils.isNull(womanNumber)) {
			log.error("人数为空");
			return new ErrorBean("人数为空");
		}
		if ("0".equals(manNumber) && "0".equals(womanNumber)) {
			log.error("人数为空");
			return new ErrorBean("人数为空");
		}
		if(!manNumber.matches("^\\d+$") || !womanNumber.matches("^\\d+$")) {
			log.error("人数必须为正整数");
			return new ErrorBean("人数必须为正整数");
		}
		if (StringUtils.isNull(activityAddress)) {
			log.error("活动地址为空");
			return new ErrorBean("活动地址为空");
		}
		if (StringUtils.isNull(mode)) {
			log.error("方式为空");
			return new ErrorBean("方式为空");
		}
		if (StringUtils.isNull(activityCutoffTime)) {
			log.error("活动截止时间为空");
			return new ErrorBean("活动截止时间为空");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度格式错误");
			return new ErrorBean("经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度格式错误");
			return new ErrorBean("纬度格式错误");
		}
		if(System.currentTimeMillis() < convertTimeToLong(activityStartTime)) {
			log.error("活动开始时间不能小于系统当前时间");
			return new ErrorBean("活动开始时间不能小于系统当前时间");
		}
		if (convertTimeToLong(activityStartTime) < convertTimeToLong(activityCutoffTime)) {
			log.error("活动报名截止时间不能大于活动开始时间");
			return new ErrorBean("活动报名截止时间不能大于活动开始时间");
		}
		if (System.currentTimeMillis() < convertTimeToLong(activityCutoffTime)) {
			log.error("活动报名截止时间不能小于系统当前时间");
			return new ErrorBean("活动报名截止时间不能小于系统当前时间");
		}
		if(activityService.selectByTwoAll(userToken.getUserId(), activityStartTime)) {
			log.error("您在本时间段有其他的活动");
			return new ErrorBean("您在本时间段有其他的活动");
		}
		try {
			activityService.saveActivity(userToken.getUserId(), activityName, activityStartTime,
					activityAddress, mode, manNumber, womanNumber, remarks, activityCutoffTime, lon, lat, detailedAddress);
		} catch (Exception e) {
			log.error("活动添加失败");
			return new ErrorBean("添加失败");
		}
//		添加每日发起活动经验值，日限制2次/20点经验
		if(!experienceService.checkCount(userToken.getUserId(), "5")) {
			try {
				experienceService.addExperience(userToken.getUserId(), "5");
			} catch (Exception e) {
				log.error("发起活动获得经验失败");
			}
		}
		return new SuccessBean("添加成功");
	}
	
	/**
	 * 删除活动
	 * @Title: deleteActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param avtivityId
	 * @param @param token
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "deleteActivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean deleteActivity(HttpServletRequest request, String avtivityId, String token) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if(StringUtils.isNull(avtivityId)) {
			log.error("活动Id为空");
			return new ErrorBean("活动Id为空");
		}
		try {
			activityService.deleteActivity(avtivityId);
		} catch (Exception e) {
			log.error("活动删除失败");
			return new ErrorBean("删除失败");
		}
		return new SuccessBean("删除成功");
	}

	/**
	 * type=1 展示热门活动
	 * type=2 根据经纬度查询附近活动
	 * type=3 展示好友活动
	 * @Title: queryHotActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param type
	 * @param @param page
	 * @param @param pageSize
	 * @param @param lon
	 * @param @param lat
	 * @param @param token
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "showHotActivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean queryHotActivity(HttpServletRequest request,String type, String page, String pageSize, String lon, String lat, String token) {
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度格式错误");
			return new ErrorBean("经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度格式错误");
			return new ErrorBean("纬度格式错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean("页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean("记录错误");
		}
		if(StringUtils.isNull(type) || !type.matches("^[123]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if("1".equals(type)) {
			resultMap = activityService.selectByScore(pages, pageSizes, lon, lat);
		}else if("2".equals(type)) {
			resultMap = activityService.selectByNearbyUserId(lon, lat, pages, pageSizes);
		}else {
			UserToken userToken = baseService.checkUserToken(token);
			if (userToken == null) {
				log.error("请重新登录");
				return new ErrorBean("请重新登录");
			}
			resultMap = activityService.selectByFriendsUserId(userToken.getUserId(), pages, pageSizes, lon, lat);
		}
		return new SuccessBean(resultMap);
	}

	/**
	 * type = 1 查看用户参加的已经结束的活动
	 * type = 2 查看用户报名的活动
	 * type = 3 查看单个用户举办的活动
	 * @Title: showOneActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param page
	 * @param @param pageSize
	 * @param @param lon
	 * @param @param lat
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "showOneActivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean showOneActivity(HttpServletRequest request, String token, String page, String pageSize, String lon, String lat, String type) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度格式错误");
			return new ErrorBean("经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度格式错误");
			return new ErrorBean("纬度格式错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean("页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean("记录错误");
		}
		if(StringUtils.isNull(type) || !type.matches("^[123]$")) {
			log.error("类型错误");
			return new ErrorBean("类型错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<>();
		if("1".equals(type)) {
			resultMap = activityService.selectByUserIdInTime(userToken.getUserId(),pages,pageSizes,lon,lat);
		} else if("2".equals(type)){
			resultMap = activityService.selectByUserIdTime(userToken.getUserId(),pages,pageSizes,lon,lat);
		} else{
			resultMap = activityService.selectByUserId(userToken.getUserId(),pages,pageSizes,lon,lat);
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 根据活动ID查询该活动的参与人
	 * @Title: selectByUserId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId
	 * @param @param page
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "participant", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean selectByUserId(HttpServletRequest request, String token, String activityId, String page, String pageSize) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean("活动ID为空");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean("页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean("记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = activityService.selectByScoreActivity(activityId, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 参加活动
	 * @Title: participateActivities
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "participateActivities", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean participateActivities(HttpServletRequest request, String token, String activityId){
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean("活动ID为空");
		}
		if(activityService.selectByAll(userToken.getUserId(), activityId)) {
			log.error("您在本时间段有其他的活动");
			return new ErrorBean("您在本时间段有其他的活动");
		}
		List<Map<String, Object>> resultMap = activityService.selectByGender(activityId);
		if(resultMap != null) {
			ShanduoUser shanduoUser = activityService.selectById(userToken.getUserId());
			for (Map<String, Object> map : resultMap) {
				if (shanduoUser.getGender().equals(map.get("gender").toString())) {
					ActivityRequirement activityRequirement = activityService.selectByNumber(activityId);
					int count = Integer.parseInt(map.get("count").toString());
					if (shanduoUser.getGender().equals("0") && activityRequirement.getWomanNumber() <= count) {
						log.error("该性别人数已满");
						return new ErrorBean("该性别人数已满");
					}
					if (shanduoUser.getGender().equals("1") && activityRequirement.getManNumber() <= count) {
						log.error("该性别人数已满");
						return new ErrorBean("该性别人数已满");
					}
				}
			}
		} 
		try {
			activityService.insertSelective(userToken.getUserId(), activityId);
		} catch (Exception e) {
			log.error("参加活动失败");
			return new ErrorBean("参加活动失败");
		}
//		添加每日参加活动经验值，日限制2次/10点经验
		if(!experienceService.checkCount(userToken.getUserId(), "8")) {
			try {
				experienceService.addExperience(userToken.getUserId(), "8");
			} catch (Exception e) {
				log.error("参加活动获得经验失败");
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" ); 
		ShanduoActivity shanduoActivity = activityService.selectByPrimaryKey(activityId);
        String str = sdf.format(shanduoActivity.getActivityStartTime());
		return new SuccessBean("参加活动成功,活动开始时间" + str + ",活动地址为" + shanduoActivity.getActivityAddress());
	}

	/**
	 * 查询发起者与参与者评分
	 * @Title: selectByHistorical
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param page
	 * @param @param pageSize
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "selectByHistorical", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean selectByHistorical(HttpServletRequest request, String token, String page, String pageSize) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean("页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean("记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = activityService.selectByHistorical(userToken.getUserId(), pages, pageSizes);
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 刷新活动
	 * @Title: activityRefresh
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "activityRefresh", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean activityRefresh(HttpServletRequest request, String token, String activityId){
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean("活动ID为空");
		}
		try {
			activityService.activityRefresh(activityId);
		} catch (Exception e) {
			log.error("刷新失败");
			return new ErrorBean("刷新失败");
		}
		return new SuccessBean("刷新成功");
	}
	
	/**
	 * 活动置顶
	 * @Title: updateBysetTop
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "updateBysetTop", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean updateBysetTop(HttpServletRequest request, String token, String activityId) {
		UserToken userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error("请重新登录");
			return new ErrorBean("请重新登录");
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean("活动ID为空");
		}
		try {
			activityService.updateBysetTop(activityId);
		} catch (Exception e) {
			log.error("活动置顶失败");
			return new ErrorBean("活动置顶失败");
		}
		return new SuccessBean("活动置顶成功");
	}
	
	public static Long convertTimeToLong(String time) {
		Date date = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			date = sdf.parse(time);
			return date.getTime() / 1000;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
}
