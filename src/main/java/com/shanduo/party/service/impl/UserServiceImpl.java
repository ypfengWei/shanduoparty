package com.shanduo.party.service.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.entity.ShanduoReputation;
import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.UserMoney;
import com.shanduo.party.entity.service.TokenInfo;
import com.shanduo.party.mapper.ShanduoLabelMapper;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.mapper.ShanduoUserMapper;
import com.shanduo.party.mapper.UserMoneyMapper;
import com.shanduo.party.mapper.UserTokenMapper;
import com.shanduo.party.service.UserService;
import com.shanduo.party.service.VipService;
import com.shanduo.party.util.AgeUtils;
import com.shanduo.party.util.MD5Utils;
import com.shanduo.party.util.PictureUtils;
import com.shanduo.party.util.StringUtils;
import com.shanduo.party.util.UUIDGenerator;


/**
 * 
 * @ClassName: UserServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月14日 下午3:17:47
 * 
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private ShanduoUserMapper userMapper;
	@Autowired
	private UserTokenMapper tokenMapper;
	@Autowired
	private UserMoneyMapper moneyMapper;
	@Autowired
	private ShanduoLabelMapper labelMapper;
	@Autowired
	private ShanduoReputationMapper shanduoReputationMapper;
	@Autowired
	private VipService vipService;
	
	
	@Override
	public int saveUser(String phone,String password) {
		password = MD5Utils.getInstance().getMD5(password);
		ShanduoUser user = new ShanduoUser();
		String name = phone.substring(0,3)+"****"+phone.substring(7,11);
		user.setUserName(name);
		user.setPhoneNumber(phone);
		user.setPassWord(password);
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		user.setBirthday(format.format(time));
		user.setSignature("这个人很懒,什么也不留下");
		int i = userMapper.insertUser(user);
		if(i < 1) {
			log.error("用户记录插入失败");
			throw new RuntimeException();
		}
		UserMoney money = new UserMoney();
		money.setUserId(user.getId());
		int n = moneyMapper.insertSelective(money);
		if(n < 1) {
			log.error("用户币种插入失败");
			throw new RuntimeException();
		}
		ShanduoReputation shanduoReputation = new ShanduoReputation();
		shanduoReputation.setUserId(user.getId());
		int count = shanduoReputationMapper.insertSelective(shanduoReputation);
		if(count < 1) {
			log.error("信誉添加失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public boolean checkPhone(String phone) {
		ShanduoUser user = userMapper.selectByPhone(phone);
		if(user != null) {
			return true;
		}
		return false;
	}

	@Override
	public TokenInfo loginUser(String username, String password) {
		password = MD5Utils.getInstance().getMD5(password);
		ShanduoUser user = null;
		if(username.length() != 11) {
			user = userMapper.loginById(Integer.parseInt(username), password);
		}else {
			user = userMapper.loginByPhone(username, password);
		}
		if(user == null) {
			return null;
		}
		String token = savaToken(user.getId());
		if(StringUtils.isNull(token)) {
			return null;
		}
		return new TokenInfo(user,token,vipService.selectVipExperience(user.getId()));
	}

	@Override
	public int updatePhone(Integer userId, String phone) {
		ShanduoUser user = new ShanduoUser();
		user.setId(userId);
		user.setPhoneNumber(phone);
		int i = userMapper.updateByPrimaryKeySelective(user);
		if(i < 1) {
			log.error("修改手机号失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updatePassword(Integer userId,String password, String newPassword) {
		password = MD5Utils.getInstance().getMD5(password);
		newPassword = MD5Utils.getInstance().getMD5(newPassword);
		int i = userMapper.updateByPassword(userId, password, newPassword);
		if(i < 1) {
			log.error("修改密码失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updatePasswordByPhone(Integer userId, String password) {
		password = MD5Utils.getInstance().getMD5(password);
		int i = userMapper.updateByPhone(userId, password);
		if(i < 1) {
			log.error("修改密码失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public TokenInfo updateUser(String token,Integer userId,String name,String headPortraitId,String birthday,String gender,
			String emotion,String signature,String background,String hometown,String occupation,String school) {
		ShanduoUser user = new ShanduoUser();
		user.setId(userId);
		if(!StringUtils.isNull(name)) {
			user.setUserName(name);
		}
		if(!StringUtils.isNull(headPortraitId)) {
			user.setHeadPortraitId(headPortraitId);
		}
		if(!StringUtils.isNull(birthday)) {
			user.setBirthday(birthday);
		}
		if(!StringUtils.isNull(gender)) {
			user.setGender(gender);
		}
		if(!StringUtils.isNull(emotion)) {
			user.setEmotion(emotion);
		}
		if(!StringUtils.isNull(signature)) {
			user.setSignature(signature);
		}
		if(!StringUtils.isNull(background)) {
			user.setBackgroundPicture(background);
		}
		if(!StringUtils.isNull(hometown)) {
			user.setHometown(hometown);
		}
		if(!StringUtils.isNull(occupation)) {
			user.setOccupation(occupation);
		}
		if(!StringUtils.isNull(occupation)) {
			user.setSchool(occupation);
		}
		int i = userMapper.updateByPrimaryKeySelective(user);
		if(i < 1) {
			log.error("修改个人资料失败");
			throw new RuntimeException();
		}
		user = userMapper.selectByPrimaryKey(userId);
		return new TokenInfo(user,token,vipService.selectVipExperience(user.getId()));
	}

	@Override
	public String savaToken(Integer userId) {
		String token = UUIDGenerator.getUUID();
		int i = tokenMapper.insertOrUpdate(token, token, userId);
		if (i <= 0) {
			log.error("token生成错误");
			return "";
		}
		return token;
	}

	@Override
	public List<Map<String, Object>> labelList() {
		List<Map<String, Object>> labelList= labelMapper.selectList();
		if(labelList == null || labelList.isEmpty()) {
			return null;
		}
		return labelList;
	}

	@Override
	public String selectByPhone(Integer userId) {
		ShanduoUser user = userMapper.selectByPrimaryKey(userId);
		if(user == null) {
			return "";
		}
		return user.getPhoneNumber();
	}

	@Override
	public List<Map<String, Object>> seekUser(String query) {
		List<Map<String, Object>> resultList = userMapper.seekUser(query);
		for (Map<String, Object> map : resultList) {
			map.put("portraitId", PictureUtils.getPictureUrl(map.get("portraitId").toString()));
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			map.put("vip", vipService.selectVipExperience(Integer.parseInt(map.get("id").toString())));
		}
		return resultList;
	}

}
