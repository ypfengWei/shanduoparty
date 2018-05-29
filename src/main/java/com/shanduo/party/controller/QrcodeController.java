package com.shanduo.party.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;

/**
 * 扫码控制层
 * @ClassName: QrcodeController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月5日 下午2:25:03
 *
 */
@Controller
@RequestMapping(value = "jqrcode")
public class QrcodeController {
	
	private static final Logger log = LoggerFactory.getLogger(QrcodeController.class);
	
	//str=userId=11111Z&typeId=2
	/**
	 * 扫码接口
	 * @Title: scan
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param str
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "scan")
	@ResponseBody
	public ResultBean scan(HttpServletRequest request,String token,String str) {
		str = "userId=11111Z&typeId=1";
		log.info("扫码结束");
		return new ErrorBean(10002,"aaa");
	}

}
