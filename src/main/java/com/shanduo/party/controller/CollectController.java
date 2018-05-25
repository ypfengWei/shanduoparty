package com.shanduo.party.controller;

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
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.CollectServic;
import com.shanduo.party.util.StringUtils;

/**
 * 收藏控制层
 * @ClassName: CollectController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月26日 下午3:14:25
 *
 */
@Controller
@RequestMapping(value = "jcollect")
public class CollectController {

	private static final Logger log = LoggerFactory.getLogger(CollectController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private CollectServic collectServic;
	
	/**
	 * 添加收藏
	 * @Title: saveCollect
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param fileUrl 文件URL
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "savecollect",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean saveCollect(HttpServletRequest request,String token,String fileUrl) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(fileUrl)) {
			log.error("收藏文件为空");
			return new ErrorBean("收藏文件为空");
		}
		if(collectServic.checkCollect(isUserId, fileUrl)) {
			log.error("已收藏");
			return new ErrorBean("已收藏");
		}
		try {
			collectServic.saveCollect(isUserId, fileUrl);
		} catch (Exception e) {
			log.error("收藏失败");
			return new ErrorBean("收藏失败");
		}
		return new SuccessBean("收藏成功");
	}
	
	/**
	 * 批量删除收藏
	 * @Title: delCollect
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param collectIds 收藏ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "delcollects",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean delCollects(HttpServletRequest request,String token,String collectIds) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConstants.USER_TOKEN_PASTDUR);
			return new ErrorBean(ErrorCodeConstants.USER_TOKEN_PASTDUR);
		}
		if(StringUtils.isNull(collectIds)) {
			log.error("收藏ID为空");
			return new ErrorBean("收藏ID为空");
		}
		try {
			collectServic.deleteCollect(isUserId, collectIds);
		} catch (Exception e) {
			log.error("取消失败");
			return new ErrorBean("取消失败");
		}
		return new SuccessBean("取消成功");
	}
	
	/**
	 * 分页查询所有收藏
	 * @Title: collectList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param page 页数
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "collectList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean collectList(HttpServletRequest request,String token,String page,String pageSize) {
		Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
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
		Map<String, Object> resultMap = 
				collectServic.selectByUserList(isUserId, pages, pageSizes);
		return new SuccessBean(resultMap);
	}
}
