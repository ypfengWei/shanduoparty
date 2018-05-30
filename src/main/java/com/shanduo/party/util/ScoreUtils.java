package com.shanduo.party.util;

import java.util.Date;
import javax.annotation.PostConstruct;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.mapper.ShanduoReputationRecordMapper;

/**
 * 判断该用户的信誉分是否增加工具类
 * @ClassName: ScoreUtils
 * @Description: TODO
 * @author fangwei
 * @date 2018年5月17日 下午14:42
 *
 */
@Component
public class ScoreUtils {
	
	@Autowired
	private ShanduoReputationMapper shanduoReputationMapper;
	
	@Autowired
	private ShanduoReputationRecordMapper reputationRecordMapper;

	public static ShanduoReputationMapper reputationMapper;
	public static ShanduoReputationRecordMapper reputationRecordsMapper;
	
	@PostConstruct
	public void init() {
		ScoreUtils.reputationMapper = shanduoReputationMapper;
		ScoreUtils.reputationRecordsMapper = reputationRecordMapper;
	} 
	
	
	/**
	 * 两个用户之间一个月的信誉分记录最高加减6分
	 * @Title: getPictureUrl
	 * @Description: TODO
	 * @param @param id
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static boolean getScore(Integer userId,Integer otherUserId) {
		if(userId == 0) {
			return false;
		}
		if(otherUserId == 0) {
			return false;
		}
        int i = reputationRecordsMapper.selectByMany(userId, otherUserId, "1", DateFormatUtils.format(new Date(), "yyyy-MM-01 00:00:00"));
        if(i>=6 || i<=-6){
        	return false;
        }
		return true;
	}
	
	/**
	 * 单个用户一天的信誉分最高加减10分
	 * @Title: getScore
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param otherUserId
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	public static int getScore(Integer otherUserId) {
        Integer a = reputationRecordsMapper.selectByUserId( otherUserId, DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00"), "1");
        if(a>=10 ){
        	return 10;
        }
		if(a<=-10 ){
        	return -10;
        }
		return a;
	}
}
