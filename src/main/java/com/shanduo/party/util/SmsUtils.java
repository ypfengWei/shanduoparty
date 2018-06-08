package com.shanduo.party.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 短信验证码工具类
 * @ClassName: SmsUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月12日 下午5:12:41
 *
 */
public class SmsUtils {
	/**
	 * 产品名称:云通信短信API产品,开发者无需替换
	 */
    static final String PRODUCT = "Dysmsapi";
    /**
     * 产品域名,开发者无需替换
     */
    static final String DOMAIN = "dysmsapi.aliyuncs.com";

    /**
     * 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
     */
    static final String ACCESS_KEY_ID = "LTAIZn1pl4XmYPGG";
    static final String ACCESS_KEY_SECRET = "3zyatcwOr2j6iuGUollzPzTJ4ERvSp";

    /**
     * 阿里云发送短信验证码
     * @Title: sendSms
     * @Description: TODO
     * @param @param tel 手机号
     * @param @param type 短信类型 1.注册,2.换手机号,3.修改密码,4.修改支付密码
     * @param @param code 验证码
     * @param @return
     * @param @throws ClientException
     * @return SendSmsResponse
     * @throws
     */
    public static SendSmsResponse sendSms(String tel,String code,String typeId) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(tel);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("闪多");
        //必填:短信模板-可在短信控制台中找到
        switch (typeId) {
		case "1":
			request.setTemplateCode("SMS_136855850");
			break;
		case "2":
			request.setTemplateCode("SMS_136855851");
			break;
		case "3":
			request.setTemplateCode("SMS_136870630");
			break;
		case "4":
			request.setTemplateCode("SMS_136865652");
			break;
		default:
			break;
		}
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\""+code+"\"}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }
    
}
