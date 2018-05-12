package com.shanduo.party.mapper;

import java.util.List;

import com.shanduo.party.entity.ShanduoVip;

public interface ShanduoVipMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShanduoVip record);

    int insertSelective(ShanduoVip record);

    ShanduoVip selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShanduoVip record);

    int updateByPrimaryKey(ShanduoVip record);
    
    /**
     * 根据用户id查询vip
     * @Title: selectByUserIdAndTime
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userId
     * @param @param time
     * @param @return    设定文件
     * @return List<Map<String,Object>>    返回类型
     * @throws
     */
    List<ShanduoVip> selectByUserIdAndTime(Integer userId);
    
    /**
     * 重新开通会员
     * @Title: updateByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param startTime
     * @param @param endTime
     * @param @param userId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserId(String startTime, String endTime, Integer userId);
    
    /**
     * 续费会员
     * @Title: updateByUserIdTwo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param endTime
     * @param @param userId
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserIdTwo(String endTime, Integer userId);
}