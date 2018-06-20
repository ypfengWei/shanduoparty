package com.shanduo.party.xg;

import org.json.JSONException;

public class Test {

	public static void main(String[] args) throws JSONException {
//		System.out.println(XGHighUtils.getInstance().pushSingleDeviceNotification("标题", "测试", "97d2740d1ee608d24d0e84ea54f41f6149690335",0));
//		System.out.println(XGUtils.pushTokenAndroid("标题", "测试", "97d2740d1ee608d24d0e84ea54f41f6149690335"));
//		System.out.println(XGUtils.pushAccountAndroid("你好", "尊敬的VIP你好", "10003"));
		System.out.println(XGHighUtils.getInstance().pushSingleAccount("你好", "尊敬的VIP你好666", "10003",3));
	}
}
