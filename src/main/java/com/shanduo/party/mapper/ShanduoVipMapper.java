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
     * 根据用户Id查询vip信息
     * @Title: selectByVipType
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @return 设定文件
     * @return ShanduoVip 返回类型
     * @throws
     */
    ShanduoVip selectByVipType(Integer userId, String vipType);
    
    /**
     * 查询vip
     * @Title: selectByUserId
     * @Description: TODO
     * @param @param userId
     * @param @return
     * @return List<ShanduoVip>
     * @throws
     */
    List<ShanduoVip> selectByUserId(Integer userId);
 
    
}