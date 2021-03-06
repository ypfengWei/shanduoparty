package com.shanduo.party.service;

/**
 * 图片业务层
 * @ClassName: PictureService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月23日 上午9:42:56
 *
 */
public interface PictureService {

	/**
	 * 保存多张图片记录
	 * @Title: savePicture
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param images 图片名称集合
	 * @param @return
	 * @return String
	 * @throws
	 */
	String savePicture(Integer userId,String images);
	
	/**
	 * 查找单张图片
	 * @Title: selectByPictureId
	 * @Description: TODO
	 * @param @param pictureId 图片ID
	 * @param @return
	 * @return String
	 * @throws
	 */
	String selectByPictureId(String pictureId);
}
