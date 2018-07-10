package com.shanduo.party.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shanduo.party.common.ConfigConsts;

/**
 * 从微信服务器下载图片资源
 * @ClassName: WxFileUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月2日 下午2:11:56
 *
 */
public class WxFileUtils {

	private static final Logger log = LoggerFactory.getLogger(WxFileUtils.class);
	
	public static String downloadImages(String accessToken, String mediaId) {
		StringBuilder picture = new StringBuilder();
		String[] pictures = mediaId.split(",");
		for (int i = 0; i < pictures.length; i++) {
			if(pictures[i] == null || "".equals(pictures[i])) {
				continue;
			}
			if(i == pictures.length - 1) {
				picture.append(downloadImage(accessToken, pictures[i]));
			}else{
				picture.append(downloadImage(accessToken, pictures[i])+",");
			}
		}
		return picture.toString();
	}
	
	public static String downloadImage(String accessToken, String mediaId) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+accessToken+"&media_id="+mediaId);
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	String type = response.getEntity().getContentType().getValue();
            	if(type.indexOf("image") == -1) {
            		String body = EntityUtils.toString(response.getEntity());
            		log.error(body);
            		return "";
            	}else {
            		InputStream is = response.getEntity().getContent();
            		String fileName = UUIDGenerator.getUUID()+".jpg";
            		BufferedInputStream bis = new BufferedInputStream(is);
            		FileOutputStream fos = new FileOutputStream(new File(ConfigConsts.FILE_PATH, fileName));
            		byte[] buf = new byte[1024];
            		int size = 0;
            		while ((size = bis.read(buf)) != -1) {
            			fos.write(buf, 0, size);
            		}
            		fos.close();
            		bis.close();
            		return fileName;
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
				httpClient.close();
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return "";
    }

}
