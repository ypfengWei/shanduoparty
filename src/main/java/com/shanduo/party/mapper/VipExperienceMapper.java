package com.shanduo.party.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shanduo.party.entity.VipExperience;

public interface VipExperienceMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(VipExperience record);

    int insertSelective(VipExperience record);

    VipExperience selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(VipExperience record);

    int updateByPrimaryKey(VipExperience record);
    
    /**
     * 查询vip成长值
     * @Title: selectByUserId
     * @Description: TODO
     * @param @param userId
     * @param @return
     * @return int
     * @throws
     */
    Integer selectByUserId(Integer userId);
    
    /**
     * 加svip成长值
     * @Title: updateByUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserId();
    
    /**
     * 加vip成长值
     * @Title: updateByUserIdTwo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserIdTwo();
    
    /**
     * 扣非vip成长值
     * @Title: updateByUserIdThree
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @return    设定文件
     * @return int    返回类型
     * @throws
     */
    int updateByUserIdThree();
    
    /**
     * 查询在一段时间内属于vip的用户
     * @Title: vipList
     * @Description: TODO
     * @param @param startDate
     * @param @param endDate
     * @param @return
     * @return List<VipExperience>
     * @throws
     */
    List<VipExperience> vipList(@Param(value="startDate")String startDate,@Param(value="endDate")String endDate);
}