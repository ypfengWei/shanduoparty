package com.shanduo.party.mapper;

import java.math.BigDecimal;

import com.shanduo.party.entity.UserOrder;

public interface UserOrderMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);
    
    UserOrder selectById(String id);
    
    /**
     * 查询区域代理时间内的提成
     * @Title: regionCount
     * @Description: TODO
     * @param @param address
     * @param @param startDate
     * @param @param endDate
     * @param @return
     * @return Double
     * @throws
     */
    BigDecimal regionCount(String address,String startDate,String endDate);
}