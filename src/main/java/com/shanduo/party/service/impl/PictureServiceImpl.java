package com.shanduo.party.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ShanduoPicture;
import com.shanduo.party.mapper.ShanduoPictureMapper;
import com.shanduo.party.service.PictureService;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 
 * @ClassName: PictureServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月23日 上午9:49:32
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PictureServiceImpl implements PictureService {

	private static final Logger log = LoggerFactory.getLogger(PictureServiceImpl.class);
	
	@Autowired
	private ShanduoPictureMapper pictureMapper;
	
	@Override
	public String savePicture(Integer userId,String images) {
		String[] imageList = images.split(",");
		StringBuilder pictures = new StringBuilder();
		for (int i = 0; i < imageList.length; i++) {
			String image = imageList[i];
			if(image == null || "".equals(image)) {
				continue;
			}
			String uuid = UUIDGenerator.getUUID();
			ShanduoPicture picture = new ShanduoPicture();
			picture.setId(uuid);
			picture.setUserId(userId);
			picture.setPictureName(image);
			int n = pictureMapper.insertSelective(picture);
			if(n <= 0) {
				log.error("图片记录插入失败");
				throw new RuntimeException();
			}
			if(i == imageList.length - 1) {
				pictures.append(uuid);
			}else {
				pictures.append(uuid+",");
			}
		}
		return pictures.toString();
	}

	@Override
	public String selectByPictureId(String pictureId) {
		ShanduoPicture shanduoPicture = pictureMapper.selectByPrimaryKey(pictureId);
		if(shanduoPicture != null) {
			return shanduoPicture.getPictureName();
		}
		return null;
	}

}
