package com.shanduo.party.util;

/**
 * 计算等级工具类
 * @ClassName: GradeUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月5日 下午3:52:07
 *
 */
public class LevelUtils {

	/**
	 * 计算经验等级
	 * @Title: getLevel
	 * @Description: TODO
	 * @param @param experience
	 * @param @return
	 * @return Integer
	 * @throws
	 */
	public static Integer getLevel(Integer experience) {
		if(experience <= 100) {
			return 1;
		}
		Integer grade = 1;
		experience = experience -100;
		for(;;grade++) {
			experience = experience-(300*grade);
			if(experience <= 0) {
				break;
			}
		}
		return grade+1;
	}
	
}
