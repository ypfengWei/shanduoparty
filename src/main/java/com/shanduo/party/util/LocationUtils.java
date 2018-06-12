package com.shanduo.party.util;

import java.text.DecimalFormat;

/**
 * 根据两个经纬度计算距离
 * @ClassName: LocationUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年4月28日 上午11:08:30
 *
 */
public class LocationUtils {
	
	// 地球平均半径  
    private static final double EARTH_RADIUS = 6378137;  
    // 把经纬度转为度（°）  
    private static double rad(double d) {  
        return d * Math.PI / 180.0;  
    }  
    
//    public static Double[] getDoubles(String lon,String lat) {
//    	double r = 6371;//地球半径千米
//        double dis = 10;//10千米距离
//        double dlng = 2*Math.asin(Math.sin(dis/(2*r))/Math.cos(Double.parseDouble(lat)*Math.PI/180));
//        dlng = dlng*180/Math.PI;//角度转为弧度
//        double dlat = dis/r;
//        dlat = dlat*180/Math.PI;        
//        Double[] doubles = new Double[4];
//        doubles[0] = Double.parseDouble(lon) - dlng;
//        doubles[1] = Double.parseDouble(lon) + dlng;
//        doubles[2] = Double.parseDouble(lat) - dlat;
//        doubles[3] = Double.parseDouble(lat) + dlat;
//		return doubles;
//    }
    
    /** 
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位：千米 
     * @Title: getDistance
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param lng1
     * @param @param lat1
     * @param @param lng2
     * @param @param lat2
     * @param @return    设定文件
     * @return double    返回类型
     * @throws
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {  
        double radLat1 = rad(lat1);  
        double radLat2 = rad(lat2);  
        double a = radLat1 - radLat2;  
        double b = rad(lng1) - rad(lng2);  
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));  
        s = s * EARTH_RADIUS;  
        s = s / 1000;  
        DecimalFormat df = new DecimalFormat("#.00");  
        s = Double.parseDouble(df.format(s)); 
        return s;  
    }  
}  
