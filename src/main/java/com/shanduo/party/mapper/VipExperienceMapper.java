package com.shanduo.party.mapper;

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
    int selectByUserId(Integer userId);
    
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
}