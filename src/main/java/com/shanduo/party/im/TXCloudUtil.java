package com.shanduo.party.im;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * IM操作工具类
 * @ClassName: TXCloudUtil
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月7日 下午2:37:36
 *
 */
public class TXCloudUtil {
	
	private static final Logger log = LoggerFactory.getLogger(TXCloudUtil.class);
	
	
	/**
	 * IM导入账号
	 * @Title: accountImport
	 * @Description: TODO
	 * @param @param userId
	 * @param @param name
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean accountImport(String userId,String name) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("Identifier", userId);
		paramsMap.put("Nick", name);
		String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据  
        JSONObject resultJson = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.accountImport), paramsJson));
        if(resultJson.get("ActionStatus").toString().equals("OK")){
            return false;
        }
        log.error(resultJson.toString());
		return true;
	}
	
	/**
	 * 设置IM用户资料
	 * @Title: setPortrait
	 * @Description: TODO
	 * @param @param userId
	 * @param @param name
	 * @param @param image
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean setPortrait(String userId,String name,String image) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("From_Account", userId);
		LinkedList<Map<String, String>> friendList = new LinkedList<Map<String, String>>();
		Map<String, String> paramsName = new HashMap<>();
		paramsName.put("Tag", "Tag_Profile_IM_Nick");
		paramsName.put("Value", name);
		friendList.add(paramsName);
		if(image != null) {
			Map<String, String> paramsImage = new HashMap<>();
			paramsImage.put("Tag", "Tag_Profile_IM_Image");
			paramsImage.put("Value", "https://yapinkeji.com/shanduoparty/picture/"+image);
			friendList.add(paramsImage);
		}
		paramsMap.put("ProfileItem", friendList);
		String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.setPortrait), paramsJson));
        if(resultJson.get("ActionStatus").toString().equals("OK")){
            return false;
        }
        log.error(resultJson.toJSONString());
		return true;
	}
	
	/**
	 * 添加好友
	 * @Title: addFriend
	 * @Description: TODO
	 * @param @param userId 用户uid
	 * @param @param friendId 好友uid 
	 * @param @return
	 * @return byte
	 * @throws
	 */
    public static boolean addFriend(String userId, String friendId) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        LinkedList<Map<String, String>> friendList = new LinkedList<Map<String, String>>();
        Map<String, String> friendMap = new HashMap<String, String>();
        friendMap.put("To_Account", friendId);//好友的Identifier
        friendMap.put("AddSource", "AddSource_Type_App");//加好友来源 如:AddSource_Type_Android
        friendList.add(friendMap);
        paramsMap.put("From_Account", userId);//需要为该Identifier添加好友
        paramsMap.put("AddFriendItem", friendList);//好友结构体对象
        paramsMap.put("AddType", "Add_Type_Both");//表示双向加好友
        paramsMap.put("ForceAddFlags", 1);//管理员强制加好友
        String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据  
        JSONObject resultJson = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.addFriend), paramsJson));
        if(resultJson.get("ActionStatus").toString().equals("OK")){
        	String ResultCode =  resultJson.getJSONArray("ResultItem").getJSONObject(0).getString("ResultCode");
        	if("0".equals(ResultCode)) {
        		return false;
        	}
            if("30520".equals(ResultCode)) {
            	log.info("已经是好友");
            	return false;
            }
        }
        log.error(resultJson.toJSONString());
        return true;
    }
    
    /**
     * 删除好友 
     * @Title: deleteFriend
     * @Description: TODO
     * @param @param userId 用户uid 
     * @param @param friendId 好友uid 
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean deleteFriend(String userId, String friendId) {  
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        LinkedList<String> friendList = new LinkedList<String>();
        friendList.add(friendId);
        paramsMap.put("From_Account", userId);//需要为该Identifier删除好友
        paramsMap.put("To_Account", friendList);//待删除的好友的Identifier
        paramsMap.put("DeleteType", "Delete_Type_Both");//双向删除好友
        String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.deleteFriend), paramsJson)); 
        if(resultJson.get("ActionStatus").toString().equals("OK")){
        	String ResultCode =  resultJson.getJSONArray("ResultItem").getJSONObject(0).getString("ResultCode");
        	if("0".equals(ResultCode)) {
        		return false;
        	}
        	if("31704".equals(ResultCode)) {
        		log.info("不存在好友关系");
        		return false;
        	}
        }
        log.error(resultJson.toJSONString());
        return true;
    }  
    
    /**
     * 获取用户所加入的群组
     * @Title: getGroupList
     * @Description: TODO
     * @param @param userId 用户uid
     * @param @return
     * @return String
     * @throws
     */
    public static String getGroupList(String userId) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	Map<String, Object> responseFilter = new HashMap<String, Object>();
    	LinkedList<String> groupBaseInfoFilter = new LinkedList<String>();
    	groupBaseInfoFilter.add("Type");
    	groupBaseInfoFilter.add("Name");
    	groupBaseInfoFilter.add("Introduction");
    	groupBaseInfoFilter.add("Notification");
    	groupBaseInfoFilter.add("FaceUrl");
    	groupBaseInfoFilter.add("CreateTime");
    	groupBaseInfoFilter.add("Owner_Account");
    	groupBaseInfoFilter.add("LastInfoTime");
    	groupBaseInfoFilter.add("LastMsgTime");
    	groupBaseInfoFilter.add("NextMsgSeq");
    	groupBaseInfoFilter.add("MemberNum");
    	groupBaseInfoFilter.add("MaxMemberNum");
    	groupBaseInfoFilter.add("ApplyJoinOption");
    	groupBaseInfoFilter.add("ShutUpAllMember");
    	responseFilter.put("GroupBaseInfoFilter", groupBaseInfoFilter);
    	LinkedList<String> selfInfoFilter = new LinkedList<String>();
    	selfInfoFilter.add("SelfInfoFilter");
    	selfInfoFilter.add("Role");
    	selfInfoFilter.add("JoinTime");
    	selfInfoFilter.add("MsgFlag");
    	selfInfoFilter.add("UnreadMsgNum");
    	responseFilter.put("SelfInfoFilter", selfInfoFilter);
    	paramsMap.put("ResponseFilter", responseFilter);
    	paramsMap.put("Member_Account", userId);
    	String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.getGroupList), paramsJson));
    	return resultJson.toString();
    }
    
    /**
     * 获取群组详细资料
     * @Title: getGroupnfo
     * @Description: TODO
     * @param @param list
     * @param @return
     * @return String
     * @throws
     */
    public static String getGroupnfo(List<String> list) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	paramsMap.put("GroupIdList", list);
    	String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.getGroupInfo), paramsJson));
    	return resultJson.toString();
    }
    
}
