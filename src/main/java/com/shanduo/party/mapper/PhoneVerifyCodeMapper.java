package com.shanduo.party.mapper;

import com.shanduo.party.entity.PhoneVerifyCode;

public interface PhoneVerifyCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PhoneVerifyCode record);

    int insertSelective(PhoneVerifyCode record);

    PhoneVerifyCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PhoneVerifyCode record);

    int updateByPrimaryKey(PhoneVerifyCode record);
    
    /**
     * 删除时间前面的记录
     * @Title: deleteTimer
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param createDate 时间
     * @param @return
     * @return int
     * @throws
     */
    int deleteTimer(String createDate);
    
    /**
     * 检查验证码错误或超时
     * @Title: selectByQuery
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param phone 手机号
     * @param @param code 验证码
     * @param @param sceneTypeId 类型ID
     * @param @param createDate 时间
     * @param @return
     * @return PhoneVerifyCode
     * @throws
     */
    PhoneVerifyCode selectByCode(String phone,String code,String sceneTypeId,String createDate);
    
    /**
     * 检查用户是否1分钟内重复发起发送短信验证码请求
     * @Title: selectByPhone
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param phone
     * @param @param createDate
     * @param @return
     * @return PhoneVerifyCode
     * @throws
     */
    PhoneVerifyCode selectByPhone(String phone,String createDate);
}