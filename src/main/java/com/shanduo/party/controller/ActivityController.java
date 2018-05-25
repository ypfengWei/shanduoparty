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

import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.ActivityRequirement;
import com.shanduo.party.entity.ShanduoActivity;
import com.shanduo.party.entity.ShanduoUser;
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
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if (StringUtils.isNull(activityName)) {
			log.error("标题不能为空");
			return new ErrorBean("标题不能为空");
		}
		if (StringUtils.isNull(activityStartTime)) {
			log.error("活动开始时间不能为空");
			return new ErrorBean("活动开始时间不能为空");
		}
		if (StringUtils.isNull(manNumber) && StringUtils.isNull(womanNumber)) {
			log.error("人数不能为空");
			return new ErrorBean("人数不能为空");
		}
		if ("0".equals(manNumber) && "0".equals(womanNumber)) {
			log.error("人数不能为空");
			return new ErrorBean("人数不能为空");
		}
		if(StringUtils.isNull(manNumber)) {
			womanNumber = "0";
		}
		if(StringUtils.isNull(womanNumber)) {
			womanNumber = "0";
		}
		if(!manNumber.matches("^\\d+$") || !womanNumber.matches("^\\d+$")) {
			log.error("人数必须为正整数");
			return new ErrorBean("人数必须为正整数");
		}
		if (StringUtils.isNull(activityAddress)) {
			log.error("活动地址不能为空");
			return new ErrorBean("活动地址不能为空");
		}
		if (StringUtils.isNull(mode)) {
			log.error("方式不能为空");
			return new ErrorBean("方式不能为空");
		}
		if (StringUtils.isNull(activityCutoffTime)) {
			log.error("活动截止时间不能为空");
			return new ErrorBean("活动截止时间不能为空");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度格式错误");
			return new ErrorBean("经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度格式错误");
			return new ErrorBean("纬度格式错误");
		}
		if(System.currentTimeMillis() > convertTimeToLong(activityStartTime)) {
			log.error("活动开始时间不能小于系统当前时间");
			return new ErrorBean("活动开始时间不能小于系统当前时间");
		}
		if (System.currentTimeMillis() > convertTimeToLong(activityCutoffTime)) {
			log.error("活动报名截止时间不能小于系统当前时间");
			return new ErrorBean("活动报名截止时间不能小于系统当前时间");
		}
		if (convertTimeToLong(activityStartTime) < convertTimeToLong(activityCutoffTime)) {
			log.error("活动报名截止时间不能大于活动开始时间");
			return new ErrorBean("活动报名截止时间不能大于活动开始时间");
		}
		if(activityService.selectByTwoAll(userToken, activityStartTime)) {
			log.error("只能举办上一活动之后的活动");
			return new ErrorBean("只能举办上一活动之后的活动");
		}
		try {
			activityService.saveActivity(userToken, activityName, activityStartTime,
					activityAddress, mode, manNumber, womanNumber, remarks, activityCutoffTime, lon, lat, detailedAddress);
		} catch (Exception e) {
			log.error("活动添加失败");
			return new ErrorBean("添加失败");
		}
//		添加每日发起活动经验值，日限制2次/20点经验
		if(!experienceService.checkCount(userToken, "5")) {
			try {
				experienceService.addExperience(userToken, "5");
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
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
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
	 * type=4查看用户参加的已经结束的活动
	 * type=5 查看用户报名的活动
	 * type=6 查看单个用户举办的活动
	 * type=7 根据userId查询别人举报的活动
	 * @Title: queryHotActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param type
	 * @param @param page
	 * @param @param pageSize
	 * @param @param lon
	 * @param @param lat
	 * @param @param token
	 * @param @param userId
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "showHotActivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean queryHotActivity(HttpServletRequest request,String type, String page, String pageSize, String lon, String lat, String token, String userId) {
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
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		if("1".equals(type)) {
			resultMap = activityService.selectByScore(pages, pageSizes, lon, lat);
		}else if("2".equals(type)) {
			resultMap = activityService.selectByNearbyUserId(lon, lat, pages, pageSizes);
		}else {
			Integer userToken = baseService.checkUserToken(token);
			if (userToken == null) {
				log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
				return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			}
			if("3".equals(type)) {
				resultMap = activityService.selectByFriendsUserId(userToken, pages, pageSizes, lon, lat);
			} else if("4".equals(type)) {
				resultMap = activityService.selectByUserIdInTime(userToken,pages,pageSizes,lon,lat);
			} else if("5".equals(type)){
				resultMap = activityService.selectByUserIdTime(userToken,pages,pageSizes,lon,lat);
			} else if("6".equals(type)) {
				resultMap = activityService.selectByUserId(userToken,pages,pageSizes,lon,lat);
			} else {
				if(StringUtils.isNull(userId)) {
					log.error("userId为空");
					return new ErrorBean("userId为空");
				}
				resultMap = activityService.selectByUserId(Integer.parseInt(userId),pages,pageSizes,lon,lat);
			}
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
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
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
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean("活动ID为空");
		}
		if(activityService.selectByAll(userToken, activityId)) {
			log.error("您在本时间段有其他的活动");
			return new ErrorBean("您在本时间段有其他的活动");
		}
		ShanduoActivity shanduoActivity = activityService.selectByPrimaryKey(activityId);
		if(shanduoActivity.getActivityCutoffTime().getTime() < System.currentTimeMillis()) {
			log.error("此活动报名时间已过");
			return new ErrorBean("此活动报名时间已过");
		}
		//查询参加活动的男女生人数
		List<Map<String, Object>> resultMap = activityService.selectByGender(activityId);
		if(resultMap != null) {
			//查询当前用户的性别
			ShanduoUser shanduoUser = activityService.selectById(userToken);
			for (Map<String, Object> map : resultMap) {
				if (shanduoUser.getGender().equals(map.get("gender").toString())) {
					//根据活动id查询活动要求人数
					ActivityRequirement activityRequirement = activityService.selectByNumber(activityId);
					int count = Integer.parseInt(map.get("count").toString());
					//如果女生人数大于要求女生人数，参加失败
					if ("0".equals(shanduoUser.getGender()) && activityRequirement.getWomanNumber() <= count) {
						log.error("该性别人数已满");
						return new ErrorBean("该性别人数已满");
					}
					//如果男生人数大于要求男生人数，参加失败
					if ("1".equals(shanduoUser.getGender()) && activityRequirement.getManNumber() <= count) {
						log.error("该性别人数已满");
						return new ErrorBean("该性别人数已满");
					}
				}
			}
		} 
		try {
			activityService.insertSelective(userToken, activityId);
		} catch (Exception e) {
			log.error("参加活动失败");
			return new ErrorBean("参加活动失败");
		}
//		添加每日参加活动经验值，日限制2次/10点经验
		if(!experienceService.checkCount(userToken, "8")) {
			try {
				experienceService.addExperience(userToken, "8");
			} catch (Exception e) {
				log.error("参加活动获得经验失败");
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" ); 
        String str = sdf.format(shanduoActivity.getActivityStartTime());
		return new SuccessBean("参加活动成功,活动开始时间" + str + ",活动地址为" + shanduoActivity.getActivityAddress());
	}

	/**
	 * 取消活动
	 * @Title: activityCancellation
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "activityCancellation", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean activityCancellation(HttpServletRequest request, String token, String activityId) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动id为空");
			return new ErrorBean("活动id为空");
		}
		try {
			activityService.deleteByUserId(activityId, userToken);
		} catch (Exception e) {
			log.error("活动取消失败");
			return new ErrorBean("活动取消失败");
		}
		return new SuccessBean("活动取消成功");
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
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
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
		Map<String, Object> resultMap = activityService.selectByHistorical(userToken, pages, pageSizes);
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
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
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
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
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
			return date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
}
