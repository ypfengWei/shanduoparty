package com.shanduo.party.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 微信支付工具类
 * @ClassName: WxPayUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月22日 下午5:24:36
 *
 */
public class WxPayUtils {

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @Title: createLinkString
	 * @Description: TODO
	 * @param @param params 需要排序并参与字符拼接的参数组
	 * @param @return
	 * @return String 拼接后字符串 
	 * @throws
	 */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }
    
    /**
     * 将字符串转换成签名
     * @Title: getContentBytes
     * @Description: TODO
     * @param @param content 字符串
     * @param @param charset 字符类型
     * @param @return
     * @return byte[]
     * @throws
     */
    public static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
    
    /**
     * 签名字符串
     * @Title: sign
     * @Description: TODO
     * @param @param text 需要签名的字符串
     * @param @param key 密钥
     * @param @param input_charset 编码格式
     * @param @return
     * @return String
     * @throws
     */
    public static String sign(String text, String key, String input_charset) {
        text = text + "&key=" + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset)); 
    }
    
    /**
     * 将map转换成xml
     * @Title: map2Xmlstring
     * @Description: TODO
     * @param @param map
     * @param @return
     * @return String
     * @throws
     */
    public static String map2Xmlstring(Map<String,String> params){
    	List<String> keys = new ArrayList<String>(params.keySet());
    	Collections.sort(keys);
        StringBuffer sb = new StringBuffer("");
        sb.append("<xml>");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            sb.append("<").append(key).append(">");
            sb.append(value);
            sb.append("</").append(key).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    /**
     * 向指定接口指定提交方式提交参数
     * @Title: httpRequest
     * @Description: TODO
     * @param @param requestUrl 请求地址  
     * @param @param requestMethod 请求方法 
     * @param @param outputStr 参数 
     * @param @return
     * @return String
     * @throws
     */
    public static String httpRequest(String requestUrl,String requestMethod,String outputStr){
        // 创建SSLContext
        StringBuffer buffer = null;
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //往服务器端写内容     
            if(null !=outputStr){
                OutputStream os=conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }
            // 读取服务器端返回的内容     
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
    
    /**
     * 将xml String 装换成Map
     * @Title: Str2Map
     * @Description: TODO
     * @param @param str
     * @param @return
     * @return Map<String,Object>
     * @throws
     */
    public static Map<String, Object> Str2Map(String str){
        try {
            Document doc = DocumentHelper.parseText(str);
            Map<String, Object> map = Dom2Map(doc);
            return map;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Map<String, Object> Dom2Map(Document doc){
        Map<String, Object> map = new HashMap<String, Object>();
        if(doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
            Element e = (Element) iterator.next();
            List list = e.elements();
            if(list.size() > 0){
                map.put(e.getName(), Dom2Map(e));
            }else
                map.put(e.getName(), e.getText());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Map Dom2Map(Element e){
        Map map = new HashMap();
        List list = e.elements();
        if(list.size() > 0){
            for (int i = 0;i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if(iter.elements().size() > 0){
                    Map m = Dom2Map(iter);
                    if(map.get(iter.getName()) != null){
                        Object obj = map.get(iter.getName());
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        } 
                        if(obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = (List) obj;
                            mapList.add(m);
                        } 
                        map.put(iter.getName(), mapList);
                    }else 
                        map.put(iter.getName(), m);
                } 
                else{ 
                    if(map.get(iter.getName()) != null){
                        Object obj = map.get(iter.getName());
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if(obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    }else
                        map.put(iter.getName(), iter.getText());
                }
            }
        }else
            map.put(e.getName(), e.getText());
        return map;
    }
    
    /**
     * 效验签名
     * @Title: isWechatSigns
     * @Description: TODO
     * @param @param map xml装换的map
     * @param @param apiKey 商户秘钥
     * @param @param input_charset 编码方式(utf-8)
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isWechatSigns(Map<String, Object> map,String apiKey,String input_charset) {
    	Object sign = map.get("sign");
    	if(sign == null) {
    		return false;
    	}
    	List<String> keys = new ArrayList<String>(map.keySet());
    	Map<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = map.get(key);
            if ("sign".equals(key)) {
                continue;
            }
            resultMap.put(key, value.toString());
        }
    	//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String response = WxPayUtils.createLinkString(resultMap);
		//MD5运算生成签名
		String isSign = WxPayUtils.sign(response, apiKey, input_charset).toUpperCase();
		return sign.equals(isSign);
    }
    
}
