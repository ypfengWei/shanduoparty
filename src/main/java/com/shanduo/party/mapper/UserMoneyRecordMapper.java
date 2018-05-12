package com.shanduo.party.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.party.entity.UserMoneyRecord;

public interface UserMoneyRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserMoneyRecord record);

    int insertSelective(UserMoneyRecord record);

    UserMoneyRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserMoneyRecord record);

    int updateByPrimaryKey(UserMoneyRecord record);
    
    int selectByAstrict(Integer userId,String moneyType,String createDate);
    
    /**
     * 查询用户连续签到天数
     * @Title: selectBySignInCount
     * @Description: TODO
     * @param @param userId 用户ID
     * @param @return
     * @return int
     * @throws
     */
    int selectBySignInCount(Integer userId);
    
    int meneyCount(Integer userId);
    
    List<Map<String, Object>> moneyList(Integer userId,Integer pageNum,Integer pageSize);
    
    /**
     * 查询本周签到天数
     * @Title: weekSignInCount
     * @Description: TODO
     * @param @param userId
     * @param @param startDate
     * @param @param endDate
     * @param @return
     * @return int
     * @throws
     */
    int weekSignInCount(Integer userId,String startDate,String endDate);
}