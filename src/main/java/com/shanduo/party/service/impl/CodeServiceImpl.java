package com.shanduo.party.service.impl;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.PhoneVerifyCode;
import com.shanduo.party.mapper.PhoneVerifyCodeMapper;
import com.shanduo.party.service.CodeService;

/**
 * 
 * @ClassName: CodeServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月14日 上午10:49:50
 * 
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CodeServiceImpl implements CodeService {

	private static final Logger log = LoggerFactory.getLogger(CodeServiceImpl.class);
	
	@Autowired
	private PhoneVerifyCodeMapper phoneVerifyCodeMapper;
	
	@Override
	public int saveCode(String phone,String code,String typeId) {
		PhoneVerifyCode phoneVerifyCode = new PhoneVerifyCode();
		phoneVerifyCode.setPhoneNumber(phone);
		phoneVerifyCode.setPhoneCode(code);
		phoneVerifyCode.setSceneTypeId(typeId);
		int i = phoneVerifyCodeMapper.insertSelective(phoneVerifyCode);
		if(i < 1) {
			log.error("验证码记录插入失败");
			throw new RuntimeException();
		}
		return 1;
	}
	
	@Override
	public boolean checkCode(String phone, String code, String typeId) {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = format.format(time - 1000 * 60 * 10);
		PhoneVerifyCode phoneVerifyCode = 
				phoneVerifyCodeMapper.selectByCode(phone, code, typeId, createDate);
		if(phoneVerifyCode == null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkCodeCount(String phone) {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = format.format(time - 1000 * 60);
		PhoneVerifyCode phoneVerifyCode = 
				phoneVerifyCodeMapper.selectByPhone(phone, createDate);
		if(phoneVerifyCode != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public int deleteTimer(String createDate) {
		int i = phoneVerifyCodeMapper.deleteTimer(createDate);
		if(i > 0) {
			return i;
		}
		return 0;
	}

}
