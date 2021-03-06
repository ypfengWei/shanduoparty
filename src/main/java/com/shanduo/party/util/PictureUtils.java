package com.shanduo.party.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shanduo.party.service.PictureService;

/**
 * 通过图片ID提取图片URL工具类
 * @ClassName: ShanduoPictureUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月16日 上午10:51:01
 *
 */
@Component
public class PictureUtils {

	@Autowired
	private PictureService pictureServices;
	
	public static PictureService pictureService;
	
	@PostConstruct
	public void init() {
		PictureUtils.pictureService = pictureServices;
	} 
	
	/**
	 * 返回单张图片URL
	 * @Title: getPictureUrl
	 * @Description: TODO
	 * @param @param id
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getPictureUrl(Object id) {
		if(id == null || "".equals(id)) {
			return null;
		}
		return pictureService.selectByPictureId(id.toString());
	}
	
	/**
	 * 返回多张图片URL
	 * @Title: getPictureUrlList
	 * @Description: TODO
	 * @param @param id
	 * @param @return
	 * @return List<String>
	 * @throws
	 */
	public static List<String> getPictureUrlList(Object id) {
		if(id == null || "".equals(id)) {
			return new ArrayList<>(0);
		}
		return getPictureUrlList(id.toString());
	}
	
	/**
	 * 返回单张图片URL
	 * @Title: getPictureUrl
	 * @Description: TODO
	 * @param @param id
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getPictureUrl(String id) {
		if(id == null || "".equals(id)) {
			return null;
		}
		return pictureService.selectByPictureId(id);
	}
	
	/**
	 * 返回多张图片URL
	 * @Title: getPictureUrlList
	 * @Description: TODO
	 * @param @param ids
	 * @param @return
	 * @return List<String>
	 * @throws
	 */
	public static List<String> getPictureUrlList(String id) {
		List<String> urls = new ArrayList<>();
		if(id == null || "".equals(id)) {
			return urls;
		}
		String[] ids = id.split(",");
		for (int i = 0; i < ids.length; i++) {
			if(ids[i] == null || "".equals(ids[i])) {
				continue;
			}
			String url = getPictureUrl(ids[i]);
			if(url != null) {
				urls.add(url);
			}
		}
		return urls;
	}
	
}
