package com.shanduo.party.service.impl;

import java.util.List;

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
	private ShanduoPictureMapper shanduoPictureMapper;
	
	@Override
	public String savePicture(Integer userId,List<String> urlList) {
		String pictureList = "";
		for (int i = 0; i < urlList.size(); i++) {
			String uuid = UUIDGenerator.getUUID();
			ShanduoPicture picture = new ShanduoPicture();
			picture.setId(uuid);
			picture.setUserId(userId);
			picture.setShanduoUrl(urlList.get(i));
			int n = shanduoPictureMapper.insertSelective(picture);
			if(n <= 0) {
				log.error("图片记录插入失败");
				throw new RuntimeException();
			}
			pictureList += uuid+",";
		}
		return pictureList.substring(0, pictureList.length()-1);
	}

}
