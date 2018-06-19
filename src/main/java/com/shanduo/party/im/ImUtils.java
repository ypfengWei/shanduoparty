package com.shanduo.party.im;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanduo.party.common.ShanduoConstants;
import com.shanduo.party.util.JsonStringUtils;

/**
 * IM操作工具类
 * @ClassName: TXCloudUtil
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月7日 下午2:37:36
 *
 */
public class ImUtils {
	
	private static final Logger log = LoggerFactory.getLogger(ImUtils.class);
	
	
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
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.ACCOUNT_IMPORT), paramsJson));
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
		if(name != null) {
			Map<String, String> paramsName = new HashMap<>();
			paramsName.put("Tag", "Tag_Profile_IM_Nick");
			paramsName.put("Value", name);
			friendList.add(paramsName);
		}
		if(image != null) {
			Map<String, String> paramsImage = new HashMap<>();
			paramsImage.put("Tag", "Tag_Profile_IM_Image");
			paramsImage.put("Value", ShanduoConstants.PICTURE+image);
			friendList.add(paramsImage);
		}
		paramsMap.put("ProfileItem", friendList);
		String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.PORTRAIT_SET), paramsJson));
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
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.FRIEND_ADD), paramsJson));
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
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.FRIEND_DELETE), paramsJson)); 
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
    public static Map<String, Object> getGroupList(String userId) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	Map<String, Object> responseFilter = new HashMap<String, Object>();
    	LinkedList<String> groupBaseInfoFilter = new LinkedList<String>();
    	groupBaseInfoFilter.add("Name");
    	groupBaseInfoFilter.add("FaceUrl");
    	groupBaseInfoFilter.add("LastMsgTime");
    	responseFilter.put("GroupBaseInfoFilter", groupBaseInfoFilter);
    	paramsMap.put("ResponseFilter", responseFilter);
    	paramsMap.put("Member_Account", userId);
    	String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.GROUP_LIST), paramsJson));
        Map<String, Object> resultMap = JsonStringUtils.getMap(resultJson.toString());
    	return resultMap;
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
    public static Map<String, Object> getGroupnfo(List<String> list) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	paramsMap.put("GroupIdList", list);
    	Map<String, Object> responseFilter = new HashMap<String, Object>();
    	LinkedList<String> groupBaseInfoFilter = new LinkedList<String>();
    	groupBaseInfoFilter.add("Name");
    	groupBaseInfoFilter.add("FaceUrl");
    	groupBaseInfoFilter.add("LastMsgTime");
    	responseFilter.put("GroupBaseInfoFilter", groupBaseInfoFilter);
    	paramsMap.put("ResponseFilter", responseFilter);
    	String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.GROUP_INFO), paramsJson));
        String result = resultJson.toString();
        String [] res = result.split("GroupInfo");
        result = res[0]+"GroupIdList"+res[1];
        Map<String, Object> resultMap = JsonStringUtils.getMap(result);
    	return resultMap;
    }
    
    /**
     * 修改群组资料
     * @Title: setGroup
     * @Description: TODO
     * @param @param groupId
     * @param @param name
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean setGroup(String groupId, String name) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	paramsMap.put("GroupId", groupId);
    	paramsMap.put("Name", name);
    	String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.MODIFY_GROUP_BASE_INFO), paramsJson));
        if(resultJson.get("ActionStatus").toString().equals("OK")){
        	return false;
        }
        log.error(resultJson.toJSONString());
        return true;
    }
    
    /**
     * 解散群组
     * @Title: delGroup
     * @Description: TODO
     * @param @param groupId
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean delGroup(String groupId) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	paramsMap.put("GroupId", groupId);
    	String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.DESTROY_GROUP), paramsJson));
        if(resultJson.get("ActionStatus").toString().equals("OK")){
        	return false;
        }
        if(resultJson.get("ErrorCode").toString().equals("10010")) {
        	return false;
        }
        log.error(resultJson.toJSONString());
        return true;
    }
    
    /**
     * 获取群组成员详细资料
     * @Title: getGroupUser
     * @Description: TODO
     * @param @param groupId
     * @param @param page
     * @param @return
     * @return Map<String, Object>
     * @throws
     */
    public static Map<String, Object> getGroupUser(String groupId,int pageNum) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	paramsMap.put("GroupId", groupId);
    	paramsMap.put("Limit", 20);//最多获取多少个成员的资料
    	paramsMap.put("Offset", pageNum*20-20); //从第多少个成员开始获取资料
    	LinkedList<String> memberInfoFilter = new LinkedList<String>();
    	memberInfoFilter.add("Role");// 成员身份
//    	memberInfoFilter.add("JoinTime");//成员加入时间
//    	memberInfoFilter.add("MsgSeq");//成员已读消息seq
//    	memberInfoFilter.add("MsgFlag");//成员消息屏蔽类型
//    	memberInfoFilter.add("LastSendMsgTime");//成员最后发消息时间
//    	memberInfoFilter.add("ShutUpUntil");//0表示未被禁言，否则为禁言的截止时间
    	memberInfoFilter.add("NameCard");//成员名片
    	paramsMap.put("MemberInfoFilter", memberInfoFilter);
    	String paramsJson = JSON.toJSONString(paramsMap);//拼装json数据
        JSONObject resultJson = JSON.parseObject(ImHelper.executePost(ImHelper.getUrl(ImConfig.GROUP_MEMBER_INFO), paramsJson));
        Map<String, Object> resultMap = JsonStringUtils.getMap(resultJson.toString());
    	return resultMap;
    }
}
