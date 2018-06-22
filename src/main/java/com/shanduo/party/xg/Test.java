package com.shanduo.party.xg;

import org.json.JSONException;

public class Test {

	public static void main(String[] args) throws JSONException {
		System.out.println(XGUtils.pushAccountAndroid("ShanDuo", "尊敬的VIP你好", 10003));
		System.out.println(XGHighUtils.getInstance().pushSingleAccount("ShanDuo", "尊敬的SVIP你好", 10003,1,null));
	}
}
