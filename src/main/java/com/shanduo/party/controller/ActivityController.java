package com.shanduo.party.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.common.ErrorCodeConsts;
import com.shanduo.party.entity.ActivityRequirement;
import com.shanduo.party.entity.ShanduoActivity;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.entity.service.ActivityInfo;
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
	 * @param @param activityName 活动标题
	 * @param @param activityStartTime 活动开始时间
	 * @param @param activityAddress 活动地址
	 * @param @param mode 消费方式
	 * @param @param manNumber 男生人数
 	 * @param @param womanNumber 女生人数
	 * @param @param remarks 活动内容
	 * @param @param activityCutoffTime 活动报名截止时间
	 * @param @param lon 经度
	 * @param @param lat 纬度
	 * @param @param detailedAddress 活动详细地址
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
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		if (StringUtils.isNull(activityName)) {
			log.error("标题不能为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"主题不能为空");
		}
		if (StringUtils.isNull(activityStartTime)) {
			log.error("活动开始时间不能为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动开始时间不能为空");
		}
//		if (StringUtils.isNull(manNumber) && StringUtils.isNull(womanNumber)) {
//			log.error("人数不能为空");
//			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"人数不能为空");
//		}
		if(StringUtils.isNull(manNumber)) {
			manNumber = "0";
		}
		if(StringUtils.isNull(womanNumber)) {
			womanNumber = "0";
		}
		if ("0".equals(manNumber) && "0".equals(womanNumber)) {
			log.error("人数不能为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"人数不能为空");
		}
		if(!manNumber.matches("^\\d+$") || !womanNumber.matches("^\\d+$")) {
			log.error("人数必须为正整数");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"人数必须为正整数");
		}
		if (StringUtils.isNull(activityAddress)) {
			log.error("活动地址不能为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动地址不能为空");
		}
		if (StringUtils.isNull(mode)) {
			log.error("方式不能为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"方式不能为空");
		}
		if (StringUtils.isNull(activityCutoffTime)) {
			log.error("活动截止时间不能为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动截止时间不能为空");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度格式错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度格式错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"纬度格式错误");
		}
		activityStartTime = activityStartTime.replace("T", " ");
		if(System.currentTimeMillis() > convertTimeToLong(activityStartTime)) {
			log.error("活动开始时间不能小于系统当前时间");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动开始时间不能小于系统当前时间");
		}
		activityCutoffTime = activityCutoffTime.replace("T", " ");
		if (System.currentTimeMillis() > convertTimeToLong(activityCutoffTime)) {
			log.error("活动报名截止时间不能小于系统当前时间");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动报名截止时间不能小于系统当前时间");
		}
		if (convertTimeToLong(activityStartTime) < convertTimeToLong(activityCutoffTime)) {
			log.error("活动报名截止时间不能大于活动开始时间");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动报名截止时间不能大于活动开始时间");
		}
		if(activityService.selectByTwoAll(userToken, activityStartTime)) {
			log.error("只能举办上一活动之后的活动");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"只能举办上一活动之后的活动");
		}
		String activityId = "";
		try {
			activityId = activityService.saveActivity(userToken, activityName, activityStartTime,
					activityAddress, mode, manNumber, womanNumber, remarks, activityCutoffTime, lon, lat, detailedAddress);
		} catch (Exception e) {
			log.error("活动添加失败");
			return new ErrorBean(ErrorCodeConsts.BACKSTAGE_ERROR,"添加失败");
		}
//		添加每日发起活动经验值，日限制2次/20点经验
		if(!experienceService.checkCount(userToken, "5")) {
			try {
				experienceService.addExperience(userToken, "5");
			} catch (Exception e) {
				log.error("发起活动获得经验失败");
			}
		}
		return new SuccessBean(activityId);
	}
	
	/**
	 * 删除活动
	 * @Title: deleteActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param avtivityId 活动id
	 * @param @param token
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "deleteActivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean deleteActivity(HttpServletRequest request, String activityId, String token) {
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动Id为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动Id为空");
		}
		try {
			activityService.deleteByActivity(activityId,userToken);
		} catch (Exception e) {
			log.error("活动删除失败");
			return new ErrorBean(ErrorCodeConsts.BACKSTAGE_ERROR,"删除失败");
		}
		return new SuccessBean("删除成功");
	}

	/**
	 * type=1 展示热门活动
	 * type=2 根据经纬度查询附近活动
	 * type=3 展示好友活动
	 * type=4 查看用户参加的已经结束的活动
	 * type=5 查看用户报名的活动
	 * type=6 查看单个用户举办的活动
	 * type=7 根据userId查询别人举办的活动
	 * @Title: showHotActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param type
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @param lon 经度
	 * @param @param lat 纬度
	 * @param @param token 
	 * @param @param userId 用户id看别人活动才传
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "showHotActivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean showHotActivity(HttpServletRequest request,String type, String page, String pageSize, String lon, String lat, 
			String token, String userId) {
		if(StringUtils.isNull(type) || !type.matches("^[1234567]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"类型错误");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度格式错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度格式错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"纬度格式错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		if("1".equals(type)) {
			resultMap = activityService.selectByScore(pages, pageSizes, lon, lat);
			return new SuccessBean(resultMap);
		}else if("2".equals(type)) {
			resultMap = activityService.selectByNearbyUserId(lon, lat, pages, pageSizes);
			return new SuccessBean(resultMap);
		}
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
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
				return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"userId为空");
			}
			resultMap = activityService.selectByUserId(Integer.parseInt(userId),pages,pageSizes,lon,lat);
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 查看单个活动下的参与人及参加活动状态(app)
	 * @Title: participant
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId 活动Id
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "participant", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean participant(HttpServletRequest request, String token, String activityId, String page, String pageSize) {
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动ID为空");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		int joinActivity = 0;
		if(StringUtils.isNull(token)) {
			joinActivity = 2;
 			resultMap = activityService.selectByActivityId(activityId, pages, pageSizes,null,joinActivity);
		} else {
			Integer userToken = baseService.checkUserToken(token);
			if (userToken == null) {
				log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
				return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
			}
			resultMap = activityService.selectByActivityId(activityId, pages, pageSizes,userToken,joinActivity);
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 查看单个活动详情
	 * @Title: activityDetails
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param activityId
	 * @param @param lon
	 * @param @param lat
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "details", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean details(HttpServletRequest request, String activityId, String lon, String lat) {
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动ID为空");
		}
		ActivityInfo resultMap = activityService.activityDetails(activityId, lon, lat);
		if(resultMap == null) {
			log.error("没有查到记录");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"该活动已被删除");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 根据活动ID查询该活动的参与人
	 * @Title: selectByActivityId
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId 活动id
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
//	@RequestMapping(value = "selectByActivityId", method = { RequestMethod.POST, RequestMethod.GET })
//	@ResponseBody
//	public ResultBean selectByActivityId(HttpServletRequest request, String token, String activityId, String page, String pageSize) {
//		Integer userToken = baseService.checkUserToken(token);
//		if (userToken == null) {
//			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
//			return new ErrorBean(ErrorCodeConstants.TOKEN_INVALID,ErrorCodeConstants.USER_TOKEN_PASTDUR);
//		}
//		if(StringUtils.isNull(activityId)) {
//			log.error("活动ID为空");
//			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"活动ID为空");
//		}
//		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
//			log.error("页码错误");
//			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"页码错误");
//		}
//		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
//			log.error("记录错误");
//			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"记录错误");
//		}
//		Integer pages = Integer.valueOf(page);
//		Integer pageSizes = Integer.valueOf(pageSize);
//		Map<String, Object> resultMap = activityService.selectByScoreActivity(activityId, pages, pageSizes);
//		return new SuccessBean(resultMap);
//	}
	
	/**
	 * 参加或取消活动
	 * @Title: joinActivities
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId 活动id
	 * @param @param type 1：参加活动  2：取消活动 3：踢人
	 * @param @param userIds 用户Id,我的活动才传
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "joinActivities", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean joinActivities(HttpServletRequest request, String token, String activityId, String type, String userIds){
		Integer userToken = baseService.checkUserToken(token);
		if (userToken == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动ID为空");
		}
		if(StringUtils.isNull(type) || !type.matches("^[123]$")) {
			log.error("类型错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"类型错误");
		}
		if("1".equals(type)) {
			if(activityService.selectByAll(userToken, activityId)) {
				log.error("您在本时间段有其他的活动");
				return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"您在本时间段有其他的活动");
			}
			ShanduoActivity shanduoActivity = activityService.selectByPrimaryKey(activityId);
			if(shanduoActivity.getActivityCutoffTime().getTime() < System.currentTimeMillis()) {
				log.error("此活动报名时间已过");
				return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"此活动报名时间已过");
			}
			String gender = activityService.selectByUserId(userToken); //查询当前用户的性别
			ActivityRequirement activityRequirement = activityService.selectByNumber(activityId); //查询活动要求的男女生人数
			int count = activityService.selectByGenders(activityId, gender); //根据活动id和用户性别查询参加总人数
			if("1".equals(gender)) { //当前用户为男
				int manNumber = activityRequirement.getManNumber();
				if(manNumber < 1) {
					log.error("不需要男生");
					return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"不需要男生");
				} 
				if(count >= manNumber) {
					log.error("男生人数已满");
					return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"男生人数已满");
				}
			} else { //当前用户为女
				int womanNumber = activityRequirement.getWomanNumber();
				if(womanNumber < 1) {
					log.error("不需要女生");
					return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"不需要女生");
				}
				if(count >= womanNumber) {
					log.error("女生人数已满");
					return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"女生人数已满");
				}
			}
			try {
				activityService.insertSelective(userToken, activityId,shanduoActivity.getActivityName(), shanduoActivity.getUserId());
			} catch (Exception e) {
				log.error("参加活动失败");
				return new ErrorBean(ErrorCodeConsts.BACKSTAGE_ERROR,"参加活动失败");
			}
			//添加每日参加活动经验值，日限制2次/10点经验
			if(!experienceService.checkCount(userToken, "8")) {
				try {
					experienceService.addExperience(userToken, "8");
				} catch (Exception e) {
					log.error("参加活动获得经验失败");
				}
			}
			return new SuccessBean("报名成功");
		} 
		ShanduoActivity activity = activityService.selectByPrimaryKey(activityId);
		if(activity.getActivityStartTime().getTime() < System.currentTimeMillis()) {
			log.error("该活动已过期");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"该活动已过期");
		}
		if("2".equals(type)){
			try {
				activityService.deleteByUserId(activityId, userToken, activity.getActivityName(), activity.getUserId());
			} catch (Exception e) {
				log.error("活动取消失败");
				return new ErrorBean(ErrorCodeConsts.BACKSTAGE_ERROR,"活动取消失败");
			}
		} else {
			if(StringUtils.isNull(userIds)) {
				log.error("想踢的用户不能为空");
				return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"想踢的用户不能为空");
			}
			if(!userToken.equals(activity.getUserId())) {
				log.error("不是活动发起者禁止踢人");
				return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"不是活动发起者禁止踢人");
			}
			try {
				activityService.deleteByUserIds(activityId, userIds, activity.getActivityName(), activity.getUserId());
			} catch (Exception e) {
				log.error("踢人失败");
				return new ErrorBean(ErrorCodeConsts.BACKSTAGE_ERROR,"踢人失败");
			}
		}
		return new SuccessBean("操作成功");
	}

	/**
	 * 查询发起者与参与者评分
	 * @Title: selectByHistorical
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param page 页码
	 * @param @param pageSize 记录
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
//	@RequestMapping(value = "selectByHistorical", method = { RequestMethod.POST, RequestMethod.GET })
//	@ResponseBody
//	public ResultBean selectByHistorical(HttpServletRequest request, String token, String page, String pageSize) {
//		Integer userToken = baseService.checkUserToken(token);
//		if (userToken == null) {
//			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
//			return new ErrorBean(ErrorCodeConstants.TOKEN_INVALID,ErrorCodeConstants.USER_TOKEN_PASTDUR);
//		}
//		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
//			log.error("页码错误");
//			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"页码错误");
//		}
//		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
//			log.error("记录错误");
//			return new ErrorBean(ErrorCodeConstants.PARAMETER_ERROR,"记录错误");
//		}
//		Integer pages = Integer.valueOf(page);
//		Integer pageSizes = Integer.valueOf(pageSize);
//		Map<String, Object> resultMap = activityService.selectByHistorical(userToken, pages, pageSizes);
//		return new SuccessBean(resultMap);
//	}
	
	/**
	 * 查看单个活动详情及参与人及参加活动状态(小程序)
	 * @Title: oneActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param token
	 * @param @param activityId 活动id
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "oneActivity", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean oneActivity(HttpServletRequest request, String token, String activityId) {
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动ID为空");
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		if(StringUtils.isNull(token)) {
			resultMap = activityService.selectByActivityIds(activityId, null);
		} else {
			Integer userToken = baseService.checkUserToken(token);
			if (userToken == null) {
				log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
				return new ErrorBean(ErrorCodeConsts.TOKEN_INVALID,ErrorCodeConsts.USER_TOKEN_PASTDUR);
			}
			resultMap = activityService.selectByActivityIds(activityId, userToken);
		}
		if(resultMap == null || resultMap.isEmpty()) {
			log.error("活动已被取消");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"活动已被取消");
		}
		return new SuccessBean(resultMap);
	}
	
	/**
	 * 搜索活动
	 * @Title: selectQuery
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param query 查询条件
	 * @param @param lon 经度
	 * @param @param lat 纬度
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "selectQuery", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean selectQuery(HttpServletRequest request, String query, String lon, String lat, String page, String pageSize) {
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.error("经度格式错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			log.error("纬度格式错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"纬度格式错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d+$")) {
			log.error("页码错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d+$")) {
			log.error("记录错误");
			return new ErrorBean(ErrorCodeConsts.PARAMETER_ERROR,"记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = activityService.selectQuery(query, lon, lat, pages, pageSizes);
		return new SuccessBean(resultMap);
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