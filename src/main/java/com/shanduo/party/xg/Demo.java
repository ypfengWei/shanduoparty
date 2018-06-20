package com.shanduo.party.xg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.TagTokenPair;

public class Demo {
	
	private long ACCESS_ID = 2100283798;
	private String SECRET_KEY = "55cb37c288b6e1abea3de1efcc166a1a";
    private XingeApp xinge;
    private Message message1;
    private Message message2;
    private MessageIOS messageIOS;
    
    public Demo() {
        xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        buildMesssges();
    }
    
    public static void main(String[] args) throws JSONException {
    	Demo t = new Demo();
        //Android单推
        System.out.println(t.demoPushSingleDeviceMessage("标题", "测试", "97d2740d1ee608d24d0e84ea54f41f6149690335"));
        System.out.println(t.demoPushSingleDeviceNotification("97d2740d1ee608d24d0e84ea54f41f6149690335"));
        System.out.println(t.demoPushSingleDeviceNotificationIntent());
        System.out.println(t.demoPushSingleAccount("标题", "测试", "bd034a7a00349f984afab7ee5ab86d14890c2b21"));

        //iOS推送
        /*System.out.println(t.demoPushSingleDeviceMessageIOS());
        System.out.println(t.demoPushSingleDeviceNotificationIOS());
        System.out.println(t.demoPushSingleAccountIOS());
        System.out.println(t.demoPushAccountListIOS());*/

        //Android批量推送
//        System.out.println(t.demoPushAccountList());
//        System.out.println(t.demoPushAllDevice());
//        System.out.println(t.demoPushTags());
//        System.out.println(t.demoPushAccountListMultiple());
//        System.out.println(t.demoPushDeviceListMultiple());

        //查询接口
//        System.out.println(t.demoQueryPushStatus());
//        System.out.println(t.demoQueryDeviceCount());
//        System.out.println(t.demoQueryTags());
//        System.out.println(t.demoQueryTagTokenNum());
//        System.out.println(t.demoQueryTokenTags());
        System.out.println(t.demoQueryInfoOfToken());
//        System.out.println(t.demoQueryTokensOfAccount());

        //变更接口
        /*System.out.println(t.demoCancelTimingPush());
        System.out.println(t.demoBatchSetTag());
        System.out.println(t.demoBatchDelTag());
        System.out.println(t.demoDeleteTokenOfAccount());
        System.out.println(t.demoDeleteAllTokensOfAccount());*/
    }

    //单个设备下发透传消息
    protected JSONObject demoPushSingleDeviceMessage(String title,String content,String token) {
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setType(Message.TYPE_MESSAGE);
        message.setExpireTime(86400);
        JSONObject ret = xinge.pushSingleDevice(token, message);

        return ret;
    }
    
    //单个设备下发通知消息
    protected JSONObject demoPushSingleDeviceNotification(String token) {
    	Message message = new Message();
    	//标题
    	message.setTitle("title");
    	//内容
        message.setContent("大小");
        //消息离线存储多久，单位为秒，最长存储时间3天。选填，默认为3天，设置为0等同于使用默认值
//        message.setExpireTime(0);
        //消息定时推送的时间，格式为year-mon-day  hour:min:sec，若小于服务器当前时间则立即推送。选填，默认为空字符串，代表立即推送
//        message.setSendTime("");
        //元素为TimeInterval类型，表示允许推送的时间段
        TimeInterval acceptTime1 = new TimeInterval(0, 0, 23, 59);
        message.addAcceptTime(acceptTime1);
        //消息类型。
//        TYPE_NOTIFICATION:通知；
//        TYPE_MESSAGE:透传消息。必填。注意：
//        TYPE_MESSAGE类型消息默认在终端是不展示的，不会弹出通 知
        message.setType(Message.TYPE_NOTIFICATION);
        message.setMultiPkg(0);
        //通知样式，透传消息可不填
        /**
         * #含义：样式编号0，响铃，震动，不可从通知栏清除，不影响先前通知
		 * Mess.setStyle(new Style(0,1,1,0,0));
         */
        Style style = new Style(1);
        style = new Style(3, 1, 0, 1, 0);
        message.setStyle(style);
        
        //通知被点击的动作，默认为打开app。透传消息可不填
        ClickAction action = new ClickAction();
        action.setActionType(ClickAction.TYPE_URL);
        action.setUrl("http://xg.qq.com");
        message.setAction(action);
        
        //选填。用户自定义的key-value，key必须为string
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("key1", "value1");
        custom.put("key2", 2);
        message.setCustom(custom);
        JSONObject ret = xinge.pushSingleDevice(token, message);
        return ret;
    }

    //单个设备下发通知Intent
    //setIntent()的内容需要使用intent.toUri(Intent.URI_INTENT_SCHEME)方法来得到序列化后的Intent(自定义参数也包含在Intent内）
    //终端收到后可通过intent.parseUri()来反序列化得到Intent
    protected JSONObject demoPushSingleDeviceNotificationIntent() {
        JSONObject ret = xinge.pushSingleDevice("token", message2);
        return ret;
    }

    //单个设备静默通知(iOS7以上)
    protected JSONObject demoPushSingleDeviceMessageIOS() {
        MessageIOS remoteMessageIOS = new MessageIOS();
        remoteMessageIOS.setType(MessageIOS.TYPE_REMOTE_NOTIFICATION);
        return xinge.pushSingleDevice("token", messageIOS, XingeApp.IOSENV_DEV);
    }

    //单个设备下发通知消息iOS
    protected JSONObject demoPushSingleDeviceNotificationIOS() {
        TimeInterval acceptTime1 = new TimeInterval(0, 0, 23, 59);
        messageIOS.addAcceptTime(acceptTime1);
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("key1", "value1");
        custom.put("key2", 2);
        messageIOS.setCustom(custom);

        JSONObject ret = xinge.pushSingleDevice("token", messageIOS, XingeApp.IOSENV_DEV);
        return ret;
    }

    //下发单个账号
    protected JSONObject demoPushSingleAccount(String title,String content,String Account) {
        Message message = new Message();
        message.setExpireTime(86400);
        message.setTitle(title);
        message.setContent(content);
        message.setType(Message.TYPE_MESSAGE);
        JSONObject ret = xinge.pushSingleAccount(0, Account, message);
        return ret;
    }

    //下发多个账号
    protected JSONObject demoPushAccountList() {
        Message message = new Message();
        message.setExpireTime(86400);
        message.setTitle("title");
        message.setContent("content");
        message.setType(Message.TYPE_MESSAGE);
        List<String> accountList = new ArrayList<String>();
        accountList.add("joelliu");
        JSONObject ret = xinge.pushAccountList(0, accountList, message);
        return ret;
    }

    //下发IOS单个账号
    protected JSONObject demoPushSingleAccountIOS() {
        MessageIOS message = new MessageIOS();
        message.setExpireTime(86400);
        message.setAlert("ios test");
        message.setBadge(1);
        message.setSound("beep.wav");
        TimeInterval acceptTime1 = new TimeInterval(0, 0, 23, 59);
        message.addAcceptTime(acceptTime1);
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("key1", "value1");
        custom.put("key2", 2);
        message.setCustom(custom);

        JSONObject ret = xinge.pushSingleAccount(0, "joelliu", messageIOS, XingeApp.IOSENV_DEV);
        return ret;
    }

    //下发IOS多个账号
    protected JSONObject demoPushAccountListIOS() {
        List<String> accountList = new ArrayList<String>();
        accountList.add("joelliu");
        JSONObject ret = xinge.pushAccountList(0, accountList, messageIOS, XingeApp.IOSENV_DEV);
        return ret;
    }

    //下发所有设备
    protected JSONObject demoPushAllDevice() {
        JSONObject ret = xinge.pushAllDevice(0, message1);
        return ret;
    }

    //下发标签选中设备
    protected JSONObject demoPushTags() {
        List<String> tagList = new ArrayList<String>();
        tagList.add("joelliu");
        tagList.add("phone");
        JSONObject ret = xinge.pushTags(0, tagList, "OR", message1);
        return ret;
    }

    //大批量下发给帐号 // iOS 请构造MessageIOS消息
    protected JSONObject demoPushAccountListMultiple() throws JSONException {
        Message message = new Message();
        message.setExpireTime(86400);
        message.setTitle("title");
        message.setContent("content");
        message.setType(Message.TYPE_NOTIFICATION);

        JSONObject ret = xinge.createMultipush(message);
        if (ret.getInt("ret_code") != 0)
            return ret;
        else {
            JSONObject result = new JSONObject();

            List<String> accountList1 = new ArrayList<String>();
            accountList1.add("joelliu1");
            accountList1.add("joelliu2");
            // ...
            result.append("all", xinge.pushAccountListMultiple(ret.getJSONObject("result").getLong("push_id"), accountList1));

            List<String> accountList2 = new ArrayList<String>();
            accountList2.add("joelliu3");
            accountList2.add("joelliu4");
            // ...
            result.append("all", xinge.pushAccountListMultiple(ret.getJSONObject("result").getLong("push_id"), accountList2));
            return result;
        }
    }

    //大批量下发给设备 // iOS 请构造MessageIOS消息
    protected JSONObject demoPushDeviceListMultiple() throws JSONException {
        Message message = new Message();
        message.setExpireTime(86400);
        message.setTitle("title");
        message.setContent("content");
        message.setType(Message.TYPE_NOTIFICATION);

        JSONObject ret = xinge.createMultipush(message);
        if (ret.getInt("ret_code") != 0)
            return ret;
        else {
            JSONObject result = new JSONObject();

            List<String> deviceList1 = new ArrayList<String>();
            deviceList1.add("joelliu1");
            deviceList1.add("joelliu2");
            // ...
            result.append("all", xinge.pushDeviceListMultiple(ret.getJSONObject("result").getLong("push_id"), deviceList1));

            List<String> deviceList2 = new ArrayList<String>();
            deviceList2.add("joelliu3");
            deviceList2.add("joelliu4");
            // ...
            result.append("all", xinge.pushDeviceListMultiple(ret.getJSONObject("result").getLong("push_id"), deviceList2));
            return result;
        }
    }

    //查询消息推送状态
    protected JSONObject demoQueryPushStatus() {
        List<String> pushIdList = new ArrayList<String>();
        pushIdList.add("390");
        pushIdList.add("389");
        JSONObject ret = xinge.queryPushStatus(pushIdList);
        return ret;
    }

    //查询设备数量
    protected JSONObject demoQueryDeviceCount() {
    	XingeApp psch = new XingeApp(ACCESS_ID, SECRET_KEY);
        JSONObject ret = psch.queryDeviceCount();
        return ret;
    }

    //查询标签
    protected JSONObject demoQueryTags() {
        JSONObject ret = xinge.queryTags();
        return ret;
    }

    //查询某个tag下token的数量
    protected JSONObject demoQueryTagTokenNum() {
        JSONObject ret = xinge.queryTagTokenNum("tag");
        return ret;
    }

    //查询某个token的标签
    protected JSONObject demoQueryTokenTags() {
        JSONObject ret = xinge.queryTokenTags("token");
        return ret;
    }

    //取消定时任务
    protected JSONObject demoCancelTimingPush() {
        JSONObject ret = xinge.cancelTimingPush("32");
        return ret;
    }

    // 设置标签
    protected JSONObject demoBatchSetTag() {
        List<TagTokenPair> pairs = new ArrayList<TagTokenPair>();

        // 切记把这里的示例tag和示例token修改为你的真实tag和真实token
        pairs.add(new TagTokenPair("tag1", "token00000000000000000000000000000000001"));
        pairs.add(new TagTokenPair("tag2", "token00000000000000000000000000000000001"));

        JSONObject ret = xinge.BatchSetTag(pairs);
        return ret;
    }

    // 删除标签
    protected JSONObject demoBatchDelTag() {
        List<TagTokenPair> pairs = new ArrayList<TagTokenPair>();

        // 切记把这里的示例tag和示例token修改为你的真实tag和真实token
        pairs.add(new TagTokenPair("tag1", "token00000000000000000000000000000000001"));
        pairs.add(new TagTokenPair("tag2", "token00000000000000000000000000000000001"));

        JSONObject ret = xinge.BatchDelTag(pairs);

        return ret;
    }

    //查询某个token的信息
    protected JSONObject demoQueryInfoOfToken() {
        JSONObject ret = xinge.queryInfoOfToken("97d2740d1ee608d24d0e84ea54f41f6149690335");
        return ret;
    }

    //查询某个account绑定的token
    protected JSONObject demoQueryTokensOfAccount() {
        JSONObject ret = xinge.queryTokensOfAccount("nickName");
        return ret;
    }

    //删除某个account绑定的token
    protected JSONObject demoDeleteTokenOfAccount() {
        JSONObject ret = xinge.deleteTokenOfAccount("nickName", "token");
        return ret;
    }

    //删除某个account绑定的所有token
    protected JSONObject demoDeleteAllTokensOfAccount() {
        JSONObject ret = xinge.deleteAllTokensOfAccount("nickName");
        return ret;
    }

    protected void buildMesssges() {
        message1 = new Message();
        message1.setType(Message.TYPE_NOTIFICATION);
        Style style = new Style(1);
        style = new Style(3, 1, 0, 1, 0);
        ClickAction action = new ClickAction();
        action.setActionType(ClickAction.TYPE_URL);
        action.setUrl("http://xg.qq.com");
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("key1", "value1");
        custom.put("key2", 2);
        message1.setTitle("title");
        message1.setContent("大小");
        message1.setStyle(style);
        message1.setAction(action);
        message1.setCustom(custom);
        TimeInterval acceptTime = new TimeInterval(0, 0, 23, 59);
        message1.addAcceptTime(acceptTime);

        message2 = new Message();
        message2.setType(Message.TYPE_NOTIFICATION);
        message2.setTitle("title");
        message2.setContent("通知点击执行Intent测试");
        style = new Style(1);
        action = new ClickAction();
        action.setActionType(ClickAction.TYPE_INTENT);
        action.setIntent("intent:10086#Intent;scheme=tel;action=android.intent.action.DIAL;S.key=value;end");
        message2.setStyle(style);
        message2.setAction(action);

        messageIOS = new MessageIOS();
        messageIOS.setType(MessageIOS.TYPE_APNS_NOTIFICATION);
        messageIOS.setExpireTime(86400);
        messageIOS.setAlert("ios test");
        messageIOS.setBadge(1);
        messageIOS.setCategory("INVITE_CATEGORY");
        messageIOS.setSound("beep.wav");
    }
    
}
