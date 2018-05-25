package com.shanduo.party.service;

/**
 * 小程序业务层
 * @ClassName: WechatService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月23日 上午10:56:24
 */
public interface WechatService {
	
	/**
	 * 小程序绑定
	 * @Title: insertSelective
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param record
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	int insertSelective(String code);
	
	Integer selectByUserId(String code); 
	
	boolean selectByPrimaryKey(String code);
	
}
