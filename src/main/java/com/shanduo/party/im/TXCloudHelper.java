package com.shanduo.party.im;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class TXCloudHelper {

	/**
     * @see 获取url
     * @param servicename
     * @return
     */  
    public static String getUrl(String servicename) {
        String url = "https://console.tim.qq.com/v4/"+servicename+"?"+"usersig="+tls_sigUtils.getSig(ImConfig.IDENTIFIER)
        			 +"&identifier="+ImConfig.IDENTIFIER+"&sdkappid="+ImConfig.APP_ID+"&random="+randomInt()+"&contenttype=json";
        return url;
    }
    
    /**
     * @see 发送post请求
     * @param url
     * @param headers
     * @return
     */
    public static String executePost(String url, String parameters) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost method = new HttpPost(url);
        String body = null;
        if(method != null & parameters != null && !"".equals(parameters.trim())) {
            try{
                //建立一个NameValuePair数组，用于存储欲传送的参数 
                method.addHeader("Content-type","application/json; charset=utf-8");
                method.setHeader("Accept", "application/json");
                method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));
                HttpResponse response = closeableHttpClient.execute(method);
                int statusCode = response.getStatusLine().getStatusCode();
                if(statusCode != HttpStatus.SC_OK){
                    System.out.println(response.getStatusLine());
                }
                //获取响应数据 
                body = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return body;
    }    
      
    /**
     * @see 随机数
     */
    public static int randomInt() {
        Random random = new Random();
        return random.nextInt();
    }
      
    /**
     * @see 取值范围[0,upper)
     */
    public static int randomInt(int upper) {  
        Random random = new Random();
        return random.nextInt(upper);
    }
}
