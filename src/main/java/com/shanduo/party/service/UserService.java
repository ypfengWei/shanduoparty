package com.shanduo.party.service;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.service.TokenInfo;

/**
 * 用户业务层
 * @ClassName: UserService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月14日 下午3:07:20
 *
 */
public interface UserService {
	
	/**
	 * 用户注册
	 * @Title: saveUser
	 * @Description: TODO
	 * @param @param phone 手机号
	 * @param @param password 密码
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveUser(String phone,String password);
	
	/**
	 * 用户注册
	 * @Title: saveUser
	 * @Description: TODO
	 * @param @param phone
	 * @param @param password
	 * @param @param name
	 * @param @param gender
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveUser(String phone,String password,String name,String gender);
	
	/**
	 * 检查手机号是否注册
	 * @Title: checkPhone
	 * @Description: TODO
	 * @param @param phone 手机号
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkPhone(String phone);
	
	/**
	 * 用户登录
	 * @Title: loginUser
	 * @Description: TODO
	 * @param @param username 手机号或者闪多号
	 * @param @param password 密码
	 * @param @return
	 * @return ShanduoUser
	 * @throws
	 */
	TokenInfo loginUser(String username,String password);
	
	/**
	 * 用户登录
	 * @Title: loginUser
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return TokenInfo
	 * @throws
	 */
	TokenInfo loginUser(Integer userId);
	
	/**
	 * 修改绑定手机号
	 * @Title: updatePhone
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param phone 新手机号
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updatePhone(Integer userId,String phone);
	
	/**
	 * 检查密码是否错误
	 * @Title: checkPassword
	 * @Description: TODO
	 * @param @param userId
	 * @param @param password
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkPassword(Integer userId,String password);
	
	/**
	 * 原始密码修改密码
	 * @Title: updatePassword
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param newPassword 新密码
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updatePassword(Integer userId,String newPassword);
	
	/**
	 * 手机号修改密码
	 * @Title: updatePasswordByPhone
	 * @Description: TODO
	 * @param @param phone 手机号
	 * @param @param password 新密码
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updatePasswordByPhone(String phone,String password);
	
	/**
	 * 修改个人信息
	 * @Title: updateUser
	 * @Description: TODO
	 * @param @param token 用户ID
	 * @param @param userId 用户ID
	 * @param @param name 昵称
	 * @param @param picture 头像
	 * @param @param birthday 生日
	 * @param @param gender 性别
	 * @param @param emotion 情感状态
	 * @param @param signature 个性签名
	 * @param @param background 背景图片
	 * @param @param hometown 家乡
	 * @param @param occupation 职业
	 * @param @param school 学校
	 * @param @return
	 * @return TokenInfo
	 * @throws
	 */
	TokenInfo updateUser(String token,Integer userId,String name,String picture,String birthday,String gender,
			String emotion,String signature,String background,String hometown,String occupation,String school);
	
	/**
	 * 生成token
	 * @Title: savaToken
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @return
	 * @return String
	 * @throws
	 */
	String savaToken(Integer userId);
	
	/**
	 * 查找用户信息
	 * @Title: selectByUserId
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return ShanduoUser
	 * @throws
	 */
	ShanduoUser selectByUserId(Integer userId);
	
	/**
	 * 搜索用户
	 * @Title: seekUser
	 * @Description: TODO
	 * @param @param query 搜索内容
	 * @param @return
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	List<Map<String, Object>> seekUser(Integer userId,String query);
	
	/**
	 * 查询用户的个人信息
	 * @Title: selectById
	 * @Description: TODO
	 * @param @param userId
	 * @param @param attention
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> selectById(Integer userId,Integer attention);
	
	/**
	 * 查询用户的好友，动态，活动数量
	 * @Title: selectById
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> selectById(Integer userId);
}
