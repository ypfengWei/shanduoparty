package com.shanduo.party.xg;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.shanduo.party.common.ActivityConfig;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.Style;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;

/**
 * 腾讯信鸽高级推送工具类
 * @ClassName: XGHighUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月7日 下午3:20:43
 *
 */
public class XGHighUtils {

	private long ACCESS_ID = 2100283798;
	private String SECRET_KEY = "55cb37c288b6e1abea3de1efcc166a1a";
	private XingeApp xinge;
	
	private static XGHighUtils xGHighUtils;
	
	public XGHighUtils() {
		xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
	}
	
	public static XGHighUtils getInstance(){
		xGHighUtils = new XGHighUtils();
		return xGHighUtils;
	}
	
	 /**
     * 根据类型生成Message
     * @Title: getMessage
     * @Description: TODO
     * @param @param title 标题
     * @param @param content 通知内容
     * @param @param typeId 类型:0.透传消息;1.打开vip页面;2.打开钱包页面;
     * @param @return
     * @return Message
     * @throws
     */
    public Message getMessage(String title,String content,Integer typeId,String activityId) {
    	Message message = new Message();
    	message.setTitle(title);
        message.setContent(content);
        if(typeId == 0) {
        	message.setType(Message.TYPE_MESSAGE);
            message.setExpireTime(86400);
            return message;
        }
        //表示一个允许推送的时间闭区间(起始小时，起始分钟，截止小时，截止分钟)
        message.addAcceptTime(new TimeInterval(0, 0, 23, 59));
        //消息类型必填
        //TYPE_NOTIFICATION:通知;TYPE_MESSAGE:透传消息。
        //注意：TYPE_MESSAGE类型消息默认在终端是不展示的,不会弹出通知
        message.setType(Message.TYPE_NOTIFICATION);
        //定义通知消息如何展现
        //通知样式,响铃,不震动,通知栏可清除,展示本条通知且不影响其他通知
        Style style = new Style(4,1,0,1,0);
        message.setStyle(style);
        ClickAction action = new ClickAction();
        switch (typeId) {
		case 1:
			action.setActionType(ClickAction.TYPE_ACTIVITY);
	        action.setActivity(ActivityConfig.VIP);
			break;
		case 2:
			action.setActionType(ClickAction.TYPE_ACTIVITY);
			action.setActivity(ActivityConfig.MONEY);
			break;
		case 3:
			action.setActionType(ClickAction.TYPE_INTENT);
			action.setIntent(ActivityConfig.INTENT_URI + ActivityConfig.ACT_DETAIL +"?actId="+activityId+"&type=1");
			break;
		case 4:
			action.setActionType(ClickAction.TYPE_ACTIVITY);
			break;
		default:
//			action.setActionType(ClickAction.TYPE_INTENT);
//			action.setIntent("intent:10086#Intent;scheme=tel;action=android.intent.action.DIAL;S.key=value;end");
			break;
		}
        message.setAction(action);
        return message;
    }
    
	/**
	 * Android单个账号下发
	 * @Title: pushSingleAccount
	 * @Description: TODO
	 * @param @param title
	 * @param @param content
	 * @param @param account
	 * @param @param typeId 1:透传消息,2.打开app
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
    public String pushSingleAccount(String title,String content,Integer account,Integer typeId,String activityId) {
    	Message message = getMessage(title, content, typeId, activityId);
        JSONObject resultJson = xinge.pushSingleAccount(0, account+"", message);
		return isError(resultJson);
    }
    
    /**
     * Android多个账号下发
     * @Title: pushAccountList
     * @Description: TODO
     * @param @param title 标题内容
     * @param @param content 通知内容
     * @param @param account 账号
     * @param @param typeId 类型
     * @param @return
     * @param @throws JSONException
     * @return String
     * @throws
     */
    public String pushAccountList(String title,String content,List<String> accountList,Integer typeId, String activityId) {
    	Message message = getMessage(title, content, typeId, activityId);
        JSONObject resultJson = xinge.pushAccountList(0, accountList, message);
        return isError(resultJson);
    }
    
    /**
     * Android所有设备下发
     * @Title: pushAllDevice
     * @Description: TODO
     * @param @param title
     * @param @param content
     * @param @param typeId
     * @param @return
     * @param @throws JSONException
     * @return String
     * @throws
     */
    public String pushAllDevice(String title,String content,Integer typeId,String activityId) {
    	Message message = getMessage(title, content, typeId,activityId);
        JSONObject resultJson = xinge.pushAllDevice(0, message);
        return isError(resultJson);
    }
    
    /**
     * Android选中标签设备下发
     * @Title: pushTags
     * @Description: TODO
     * @param @param title
     * @param @param content
     * @param @param typeId
     * @param @param tagList
     * @param @return
     * @param @throws JSONException
     * @return String
     * @throws
     */
    public String pushTags(String title,String content,Integer typeId,List<String> tagList) {
    	Message message = getMessage(title, content, typeId, null);
        JSONObject resultJson = xinge.pushTags(0, tagList, "OR", message);
        return isError(resultJson);
    }
    
    /**
     * 大批量下发给帐号
     * iOS请构造MessageIOS消息
     * @Title: pushAccountListMultiple
     * @Description: TODO
     * @param @param title
     * @param @param content
     * @param @param typeId
     * @param @param accountList
     * @param @return
     * @param @throws JSONException
     * @return String
     * @throws
     */
    public String pushAccountListMultiple(String title,String content,Integer typeId,List<String> accountList) throws JSONException {
        Message message = getMessage(title, content, typeId, null);
        JSONObject ret = xinge.createMultipush(message);
        if (ret.getInt("ret_code") != 0)
            return isError(ret);
        else {
            JSONObject result = xinge.pushAccountListMultiple(ret.getJSONObject("result").getLong("push_id"), accountList);
            return isError(result);
        }
    }
    
    /**
     * 大批量下发给设备 
     * iOS请构造MessageIOS消息
     * @Title: demoPushDeviceListMultiple
     * @Description: TODO
     * @param @param title
     * @param @param content
     * @param @param typeId
     * @param @param deviceList
     * @param @return
     * @param @throws JSONException
     * @return String
     * @throws
     */
    public String demoPushDeviceListMultiple(String title,String content,Integer typeId,List<String> deviceList) throws JSONException {
        Message message = getMessage(title, content, typeId, null);
        JSONObject ret = xinge.createMultipush(message);
        if (ret.getInt("ret_code") != 0)
        	return isError(ret);
        else {
            JSONObject result = xinge.pushDeviceListMultiple(ret.getJSONObject("result").getLong("push_id"), deviceList);
            return isError(result);
        }
    }
    
    /**
	 * 判断返回
	 * @Title: getError
	 * @Description: TODO
	 * @param @param resultJson
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public String isError(JSONObject resultJson){
		try {
			if(resultJson.getInt("ret_code") == 0) {
				return "ok";
			}
			return resultJson.getString("err_msg").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
