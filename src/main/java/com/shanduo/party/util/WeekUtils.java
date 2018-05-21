package com.shanduo.party.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 把时间转换成星期
 * @ClassName: WeekUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年5月8日 下午7:58:14
 *
 */
public class WeekUtils {
	public static String getWeek(Date time) {
        Calendar cal = Calendar.getInstance();  
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
        String week;
        switch (dayWeek) {
		case 1:
			week = "星期日";
			break;
		case 2:
			week = "星期一";
			break;
		case 3:
			week = "星期二";
			break;
		case 4:
			week = "星期三";
			break;
		case 5:
			week = "星期四";
			break;
		case 6:
			week = "星期五";
			break;
		default:
			week = "星期六";
			break;
		}
		return week;
	}
}
