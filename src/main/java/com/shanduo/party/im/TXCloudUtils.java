package com.shanduo.party.im;

import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.LinkedList;  
import java.util.List;  
import java.util.Map;  
import com.alibaba.fastjson.JSON;  
import com.alibaba.fastjson.JSONArray;  
import com.alibaba.fastjson.JSONObject; 

public class TXCloudUtils {
	public static final String nickname = "Tag_Profile_IM_Nick";//昵称
    public static final String sex = "Tag_Profile_IM_Gender";//性别
    public static final String image = "Tag_Profile_IM_Image";//头像
    public static final String remark = "Tag_SNS_IM_Remark";//备注
    public static final String male = "Gender_Type_Male";//男
    public static final String female = "Gender_Type_Female";//女
      
    /**
     * 注册IM账号
     * @Title: AccountImport
     * @Description: TODO
     * @param @param userId 用户ID
     * @param @param name 昵称
     * @param @param image 头像图片地址
     * @param @return
     * @return String
     * @throws
     */
    public static String AccountImport(String userId,String name,String image) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Identifier", userId);
        map.put("Nick", name);
        map.put("FaceUrl", "https://yapinkeji.com/picture/"+image);
        String json = JSON.toJSONString(map);//拼装json数据
        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.accountImport), json));
        return jObject.get("ActionStatus").toString();
    }  
      
    /** 
     * @see 管理员向其他指定账户发送消息 
     * @param uid 
     * @param MsgType 
     * @param msg 
     * @return 
     */  
    public static byte OpenimSendMsg(long uid,String msg)  
    {  
        Map<Object, Object> map= new HashMap<Object, Object>();//拼装json数据  
        List<Object> list = new ArrayList<Object>();  
        list.add(setMsgBody(msg));//消息体内容  
        map.put("SyncOtherMachine", 1);//1：把消息同步到From_Account在线终端和漫游上；2：消息不同步至From_Account；若不填写默认情况下会将消息存From_Account漫游  
        map.put("To_Account", String.valueOf(uid));//消息接收方账号。  
        map.put("MsgLifeTime", 604800);//消息离线保存时长（秒数），最长为7天（604800s）。若消息只发在线用户，不想保存离线，则该字段填0。若不填，则默认保存7天  
        map.put("MsgRandom", TXCloudHelper.randomInt(Integer.MAX_VALUE));//消息随机数,由随机函数产生。（用作消息去重）  
        map.put("MsgTimeStamp", System.currentTimeMillis()/1000);//消息时间戳，unix时间戳。  
        map.put("MsgBody", list);//设置消息体  
        String json = JSON.toJSONString(map);  
        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.openimSendmsg), json));  
        return jObject.get("ActionStatus").toString().equals("OK") ? (byte)1 : (byte)0;   
    }  
      
    /** 
     * @see 管理员发送推送 
     * @param MsgType 
     * @param msg 
     * @return 
     */  
    public static byte ImPush(String msg)  
    {     
        Map<Object, Object> map= new HashMap<Object, Object>();//拼装json数据  
        List<Object> list = new ArrayList<Object>();  
        list.add(setMsgBody(msg));  
        map.put("MsgRandom", TXCloudHelper.randomInt(Integer.MAX_VALUE));  
        map.put("MsgBody", list);  
        String json = JSON.toJSONString(map);  
        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.openimIm_push), json));  
        return jObject.get("ActionStatus").toString().equals("OK") ? (byte)1 : (byte)0;   
    }  
      
//    /** 
//     * @see 设置用户属性
//     * @param userInfo
//     * @return
//     */  
//    public static boolean setPortrait(Long uid,UserInfo userInfo)  
//    {  
//        List<Map<String, Object>> list = setProfileItem(userInfo);  
//        Map<Object, Object> map= new HashMap<Object, Object>();//拼装json数据  
//        map.put("From_Account", String.valueOf(uid));  
//        map.put("ProfileItem", list);  
//        String json = JSON.toJSONString(map);  
//        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.setPortrait), json));  
//        return jObject.get("ActionStatus").toString().equals("OK") ? true : false;    
//    }  
      
    /** 
     * @see 获取用户属性 
     * @param uid 
     * @return 
     */  
//    public static String getPortrait(Long uid)  
//    {  
//        List<String>list = new LinkedList<String>();  
//        list.add(nickname);  
//        list.add(image);  
//        list.add(sex);  
//        Map<Object, Object> map= new HashMap<Object, Object>();//拼装json数据  
//        map.put("TagList", list);  
//        List<String> accList = new LinkedList<String>();  
//        accList.add(String.valueOf(uid));  
//        map.put("To_Account", accList);  
//        String json = JSON.toJSONString(map);  
//        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.getPortrait), json));  
//        if(!jObject.get("ActionStatus").toString().equals("OK"))   
//        {  
//            return null;  
//        }  
//        else   
//        {  
//            return JSON.toJSONString(getUserInfo(jObject));   
//        }  
//    }  
      
    /** 增加好友,设置好友备注 
     * @param uid 用户uid 
     * @param fuid 好友uid 
     * @param addSource 加好友来源平台 
     * @return 
     */  
    public static byte addFriend(String uid, String fuid, String Remark, String addSource)  {
        Map<String, Object> map = new HashMap<String, Object>();
        LinkedList<Map<String, String>> list = new LinkedList<>();
        Map<String, String> fmap = new HashMap<String, String>();
        fmap.put("To_Account", fuid);//好友的Identifier。  
        fmap.put("AddSource", "AddSource_Type_"+addSource);//加好友来源 如:AddSource_Type_Android  
        if(!Remark.isEmpty()) {
            fmap.put("Remark", Remark);
        }  
        list.add(fmap);  
        map.put("From_Account",uid);//需要为该Identifier添加好友。  
        map.put("AddFriendItem", list);//好友结构体对象。
          
        String json = JSON.toJSONString(map);//拼装json数据  
        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.addFriend), json));
        System.out.println(jObject.toString());
        if(jObject.get("ActionStatus").toString().equals("OK")){  
            String relation =  jObject.getJSONArray("ResultItem").getJSONObject(0).getString("ResultInfo");  
            if(!relation.equals("SNS_FRD_ADD_FRD_EXIST"))//已经是好友  
                return 1;
        }
        return 0;
    }  
      
    /** 删除好友 
     * @param uid 用户uid 
     * @param fuid 好友uid 
     * @return 
     */  
    public static byte deleteFriend(String uid, String fuid)  
    {  
        Map<String, Object> map = new HashMap<String, Object>();  
        LinkedList<String> list = new LinkedList<>();  
        list.add(fuid);  
        map.put("From_Account", uid);//需要为该Identifier删除好友。  
        map.put("To_Account", list);//待删除的好友的Identifier。
        map.put("DeleteType", "Delete_Type_Both");//双向删除好友  
        String json = JSON.toJSONString(map);//拼装json数据  
        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.deleteFriend), json));  
        return jObject.get("ActionStatus").toString().equals("OK") ? (byte)1 : (byte)0;  
    }  
      
    /** 检验好友 
     * @param uid 用户uid 
     * @param fuid 好友uid 
     * @return 
     */  
    public static byte checkFriend(String uid, String fuid) {  
        Map<String, Object> map = new HashMap<String, Object>();  
        LinkedList<String> list = new LinkedList<>();  
        list.add(fuid);  
        map.put("From_Account", uid);//需要为该Identifier校验好友。  
        map.put("To_Account", list);//待校验的好友的Identifier。  
        map.put("CheckType", "CheckResult_Type_Both");//双向校验好友关系  
        String json = JSON.toJSONString(map);//拼装json数据  
        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.checkFriend), json));
        System.out.println(jObject.toJSONString());
        if(jObject.get("ActionStatus").toString().equals("OK")){
            String relation =  jObject.getJSONArray("InfoItem").getJSONObject(0).getString("Relation");  
            if(relation.equals("CheckResult_Type_BothWay"))
                return 1;  
        }  
        return 0;  
    }  
      
    /** 获取指定单个好友信息 
     * @param uid 
     * @param fuid 
     * @return 
     */  
//    public static String getOneFriend( long uid, long fuid)  
//    {  
//        Map<String, Object> map = new HashMap<String, Object>();  
//        map.put("From_Account", String.valueOf(uid));//需要拉取该Identifier的好友。  
//          
//        LinkedList<String> To_Account = new LinkedList<String>();  
//        To_Account.add(String.valueOf(fuid));  
//        map.put("To_Account", To_Account);//请求拉取的好友的Identifier列表  
//          
//        LinkedList<String> flist = new LinkedList<String>();  
//        flist.add(nickname);//昵称  
//        flist.add(image);//头像URL  
//        flist.add(remark);//备注  
//        map.put("TagList", flist);//指定要拉取的资料字段及好友字段  
//          
//        String json = JSON.toJSONString(map);//拼装json数据  
//        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.getFriend), json));  
//          
//        String ResultInfo = jObject.getJSONArray("InfoItem").getJSONObject(0).getString("ResultInfo");  
//          
//        if(ResultInfo.equals("SNS_FRD_GET_LIST_FRD_NO_EXIST"))//不是好友  
//        {  
//            return null;  
//        }  
//        else  
//        {  
//            FriendInfo friendInfo = new FriendInfo();  
//            friendInfo.setFuid(fuid);  
//            return JSON.toJSONString(getFriendInfo(friendInfo, jObject.getJSONArray("InfoItem").getJSONObject(0).getJSONArray("SnsProfileItem")));  
//        }  
//    }  
      
//    /** 获取所有好友列表 
//     * @param uid 用户uid 
//     * @param startIndex 拉取的起始位置 
//     * @return 
//     */  
//    public static String getAllFriend(long uid, int startIndex)  
//    {  
//        Map<String, Object> map = new HashMap<String, Object>();  
//        LinkedList<String> list = new LinkedList<>();  
//        list.add(nickname);//昵称  
//        list.add(image);//头像URL  
//        list.add(remark);//好友备注  
//        map.put("TagList", list);//指定要拉取的字段Tag  
//        map.put("From_Account", String.valueOf(uid));//需要拉取该Identifier的好友。  
//        map.put("StartIndex", startIndex);//拉取的起始位置。  
//        map.put("GetCount", 50);//每页需要拉取的好友数量  
//        String json = JSON.toJSONString(map);//拼装json数据  
//        JSONObject jObject = JSON.parseObject(TXCloudHelper.executePost(TXCloudHelper.getUrl(CloudData.getAllFriend), json));  
//          
//        List<FriendInfo> flist = new ArrayList<>();  
//        if(jObject.get("ActionStatus").toString().equals("OK")){  
//            JSONArray jsona = jObject.getJSONArray("InfoItem");  
//            if (jsona.size() > 0) {  
//                for (Object object : jsona) {  
//                    JSONObject job = (JSONObject) object;  
//                    FriendInfo friendInfo = new FriendInfo();  
//                    friendInfo.setFuid(job.getLongValue("Info_Account")); //好友uid  
//                    flist.add(getFriendInfo(friendInfo, job.getJSONArray("SnsProfileItem")));  
//                }  
//            }  
//        }  
//          
//        return JSON.toJSONString(flist);  
//    }  
      
      
    /** 
     * @see 设置消息体内容 
     * @param MsgType 
     * @param msg 
     * @return 
     */  
    public static Map<String, Object> setMsgBody(String msg)  
    {  
        Map<String, Object> msgMap = new HashMap<String, Object>();  
        msgMap.put("MsgType", "TIMTextElem");  
        HashMap<String, String> map = new HashMap<String, String>();  
        map.put("Text", msg);  
        msgMap.put("MsgContent", map);  
        return msgMap;  
    }  
      
//    /** 
//     * @see 设置资料对象数组(设置用户属性) 
//     * @param userInfo 
//     * @return 
//     */  
//    public static List<Map<String, Object>> setProfileItem(UserInfo userInfo)  
//    {  
//        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();  
//        if(!userInfo.getNickname().isEmpty())   
//        {  
//            Map<String, Object> map = new HashMap<String, Object>();  
//            map.put("Tag", nickname);  
//            map.put("Value", userInfo.getNickname());  
//            list.add(map);  
//        }  
//        if(null != userInfo.getSex())   
//        {  
//            Map<String, Object> map = new HashMap<String, Object>();  
//            map.put("Tag", sex);  
//            if (0 == userInfo.getSex()) {  
//                map.put("Value",female);  
//            }  
//            else if (1 == userInfo.getSex())   
//            {  
//                map.put("Value", male);  
//            }  
//            else {  
//                map.put("Value", "Gender_Type_Unknown");  
//            }  
//            list.add(map);  
//        }  
//          
//        if(!userInfo.getImage().isEmpty())   
//        {  
//            Map<String, Object> map = new HashMap<String, Object>();  
//            map.put("Tag", image);  
//            map.put("Value", userInfo.getImage());  
//            list.add(map);  
//        }  
//        return list;  
//    }  
//      
//    /** 
//     * @see 获取用户信息 
//     * @param json 
//     * @return 
//     */  
//    public static UserInfo getUserInfo(JSONObject json)  
//    {  
//        UserInfo userInfo = new UserInfo();  
//        JSONArray jsonArray =  json.getJSONArray("UserProfileItem").getJSONObject(0).getJSONArray("ProfileItem");  
//        for (Object obj : jsonArray) {  
//            JSONObject ob = (JSONObject)obj;  
//            if(nickname.equals(ob.getString("Tag")))   
//            {  
//                userInfo.setNickname(ob.getString("Value"));  
//            }  
//            else if(sex.equals(ob.getString("Tag")))   
//            {  
//                if(ob.getString("Value").equals("Gender_Type_Unknown"))   
//                {  
//                    userInfo.setSex((byte) -1);//性别位置  
//                }  
//                else if(ob.getString("Value").equals(female))  
//                {  
//                    userInfo.setSex((byte) 0);//女  
//                }  
//                else   
//                {  
//                    userInfo.setSex((byte) 1);//男  
//                }  
//            }  
//            else if(image.equals(ob.getString("Tag")))   
//            {  
//                userInfo.setImage(ob.getString("Value"));  
//            }  
//        }  
//        return userInfo;  
//          
//    }  
      
//    /** 获取好友信息 
//     * @param friendInfo 
//     * @param jsonar 
//     * @return 
//     */  
//    public static FriendInfo getFriendInfo(FriendInfo friendInfo, JSONArray jsonar){  
//        if (jsonar.size() > 0) {  
//            for (Object object : jsonar) {  
//                JSONObject jobo = (JSONObject) object;  
//                if(jobo.getString("Tag").equals(nickname)){  
//                    friendInfo.setNickname(jobo.getString("Value"));//昵称  
//                }  
//                if(jobo.getString("Tag").equals(image)){  
//                    friendInfo.setImage(jobo.getString("Value")); //头像url  
//                }  
//                if(jobo.getString("Tag").equals(remark)){  
//                    friendInfo.setRemark(jobo.getString("Value")); //备注  
//                }  
//            }  
//        }  
//        return friendInfo;  
//    }  
}
