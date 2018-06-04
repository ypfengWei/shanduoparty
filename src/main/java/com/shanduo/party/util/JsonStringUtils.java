package com.shanduo.party.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * json字符串转换对象
 * @ClassName: JsonStringUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fanshixin
 * @date 2018年4月16日 下午4:01:55
 *
 */
public class JsonStringUtils {

	/**
	 * json字符串转换List<Object>
	 * @Title: getList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param json
	 * @param @return
	 * @return List<Object>
	 * @throws
	 */
	public static List<Map<String, Object>> getList(String json){
		if(json == null || json.equals("")) {
			return new ArrayList<>(0);
		}
		JSONArray jsonArray = JSONArray.fromObject(json);
		List<Map<String, Object>> list = (List<Map<String, Object>>) JSONArray.toCollection(jsonArray, Map.class);
		return list;
	}
	
	/**
	 * json字符串转换Map<String,Object>
	 * @Title: getMap
	 * @Description: TODO
	 * @param @param json
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	public static Map<String,Object> getMap(String json){
		if(json == null || json.equals("")) {
			return new HashMap<>(0);
		}
		JSONObject jsonObject = JSONObject.fromObject(json);
		Map<String,Object> map = JSONObject.fromObject(jsonObject);
		return map;
	}
}
