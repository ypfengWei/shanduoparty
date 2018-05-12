package com.shanduo.party.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.shanduo.party.common.ErrorCodeConstants;
import com.shanduo.party.entity.common.ErrorBean;

import net.sf.json.JSONObject;

/**
 * 过滤token
 * @ClassName: TokenInterceptor
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fanshixin
 * @date 2018年4月21日 下午3:22:26
 *
 */
public class TokenInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(TokenInterceptor.class);

	/***
	 *  检查Token 是否有效   需要拦截的路径 就在 spring 的配置文件中配置
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean check = false;
		log.info("token拦截中");
		ErrorBean errorBean = new ErrorBean();
		String token = request.getParameter("token");
		if ("".equals(token) || token == null) {
			errorBean = new ErrorBean(ErrorCodeConstants.TOKEN_TEXT_ISNULL);
			check = true;
		}
		//如果为空就直接出去了
		if(check){
			request.setAttribute("errorBean", errorBean);
			JSONObject responseJSONObject = JSONObject.fromObject(errorBean);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			PrintWriter out = null;
			out = response.getWriter();
			out.append(responseJSONObject.toString());
			out.close();
			return false;
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
