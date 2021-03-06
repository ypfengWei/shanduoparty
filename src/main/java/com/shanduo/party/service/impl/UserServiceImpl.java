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

import com.shanduo.party.common.ShanduoConsts;
import com.shanduo.party.entity.ShanduoReputation;
import com.shanduo.party.entity.ShanduoUser;
import com.shanduo.party.entity.UserMoney;
import com.shanduo.party.entity.service.TokenInfo;
import com.shanduo.party.im.ImUtils;
import com.shanduo.party.mapper.ShanduoActivityMapper;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.mapper.ShanduoUserMapper;
import com.shanduo.party.mapper.UserMoneyMapper;
import com.shanduo.party.mapper.UserTokenMapper;
import com.shanduo.party.service.AttentionService;
import com.shanduo.party.service.DynamicService;
import com.shanduo.party.service.ExperienceService;
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
	private ShanduoReputationMapper shanduoReputationMapper;
	@Autowired
	private VipService vipService;
	@Autowired
	private AttentionService attentionService;
	@Autowired
	private ExperienceService experienceService;
	@Autowired
	private DynamicService dynamicService;
	@Autowired
	private ShanduoActivityMapper activityMapper;
	
	/**
	 * 添加用户其他记录
	 * @Title: saveUser
	 * @Description: TODO
	 * @param @param userId
	 * @param @param name
	 * @return void
	 * @throws
	 */
	public void saveUser(Integer userId,String name) {
		UserMoney money = new UserMoney();
		money.setUserId(userId);
		money.setPassword(MD5Utils.getInstance().getMD5("111111"));
		int n = moneyMapper.insertSelective(money);
		if(n < 1) {
			log.error("用户钱包插入失败");
			throw new RuntimeException();
		}
		ShanduoReputation shanduoReputation = new ShanduoReputation();
		shanduoReputation.setUserId(userId);
		int count = shanduoReputationMapper.insertSelective(shanduoReputation);
		if(count < 1) {
			log.error("信誉添加失败");
			throw new RuntimeException();
		}
		//向IM导入账号
		ImUtils.accountImport(userId+"", name);
		//添加客服IM好友关系
		List<Map<String, Object>> listService = userMapper.listUser(ShanduoConsts.ROLE_SERVICE);
		for (Map<String, Object> map : listService) {
			String service = map.get("userId").toString();
			boolean flag = ImUtils.addFriend(userId+"", service);
			if(flag) {
				log.warn("IM添加客服好友失败 userId:{} and service:{}",userId,service);
			}
		}
	}
	
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
		saveUser(user.getId(), name);
		return 1;
	}

	@Override
	public int saveUser(String phone,String password,String name,String gender) {
		password = MD5Utils.getInstance().getMD5(password);
		ShanduoUser user = new ShanduoUser();
		user.setUserName(name);
		user.setPhoneNumber(phone);
		user.setPassWord(password);
		user.setGender(gender);
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		user.setBirthday(format.format(time));
		user.setSignature("这个人很懒,什么也不留下");
		int i = userMapper.insertUser(user);
		if(i < 1) {
			log.error("用户记录插入失败");
			throw new RuntimeException();
		}
		saveUser(user.getId(), name);
		return user.getId();
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
	public String loginUser(String username, String password) {
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
		return savaToken(user.getId());
	}

	@Override
	public String loginUser(Integer userId) {
		ShanduoUser user = userMapper.selectByPrimaryKey(userId);
		if(user == null) {
			return null;
		}
		return savaToken(user.getId());
	}
	
	@Override
	public TokenInfo selectById(String token,Integer userId) {
		ShanduoUser user = userMapper.selectByPrimaryKey(userId);
		if(user == null) {
			return null;
		}
		TokenInfo tokens = new TokenInfo(user);
		tokens.setToken(token);
		tokens.setVip(vipService.selectVipLevel(userId));
		tokens.setLevel(experienceService.selectLevel(userId));
		tokens.setAttention(attentionService.attentionCount(userId));
		tokens.setDynamic(dynamicService.dynamicCount(userId));
		tokens.setActivity(activityMapper.selectByUserIdCount(userId));
		return tokens;
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
	public boolean checkPassword(Integer userId, String password) {
		password = MD5Utils.getInstance().getMD5(password);
		ShanduoUser user = userMapper.loginById(userId, password);
		if(user == null) {
			return true;
		}
		return false;
	}
	
	@Override
	public int updatePassword(Integer userId,String newPassword) {
		newPassword = MD5Utils.getInstance().getMD5(newPassword);
		ShanduoUser user = new ShanduoUser();
		user.setId(userId);
		user.setPassWord(newPassword);
		int i = userMapper.updateByPrimaryKeySelective(user);
		if(i < 1) {
			log.error("修改密码失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updatePasswordByPhone(String phone, String password) {
		password = MD5Utils.getInstance().getMD5(password);
		int i = userMapper.updateByPhone(phone, password);
		if(i < 1) {
			log.error("修改密码失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public int updateUser(String token,Integer userId,String name,String picture,String birthday,String gender,
			String emotion,String signature,String background,String hometown,String occupation,String school) {
		ShanduoUser user = new ShanduoUser();
		user.setId(userId);
		if(!StringUtils.isNull(name)) {
			user.setUserName(name);
		}
		if(!StringUtils.isNull(picture)) {
			user.setHeadPortraitId(picture);
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
			user.setBackground(background);
		}
		if(!StringUtils.isNull(hometown)) {
			user.setHometown(hometown);
		}
		if(!StringUtils.isNull(occupation)) {
			user.setOccupation(occupation);
		}
		if(!StringUtils.isNull(school)) {
			user.setSchool(school);
		}
		int i = userMapper.updateByPrimaryKeySelective(user);
		if(i < 1) {
			log.error("修改个人资料失败");
			throw new RuntimeException();
		}
		//向IM修改用户资料
		if(!StringUtils.isNull(name) || !StringUtils.isNull(picture)) {
			ImUtils.setPortrait(user.getId()+"",user.getUserName(),PictureUtils.getPictureUrl(user.getHeadPortraitId()));
		}
		return 1;
	}

	@Override
	public String savaToken(Integer userId) {
		String token = UUIDGenerator.getUUID();
		int i = tokenMapper.insertOrUpdate(token, token, userId);
		if (i <= 0) {
			log.error("token生成错误");
			return null;
		}
		return token;
	}

	@Override
	public ShanduoUser selectByUserId(Integer userId) {
		return userMapper.selectByPrimaryKey(userId);
	}

	@Override
	public List<Map<String, Object>> seekUser(Integer userId,String query) {
		List<Map<String, Object>> resultList = userMapper.seekUser(query);
		for (Map<String, Object> map : resultList) {
			map.put("picture", PictureUtils.getPictureUrl(map.get("picture")));
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			map.put("vip", vipService.selectVipLevel(Integer.parseInt(map.get("userId").toString())));
			int i = attentionService.checkAttention(userId, Integer.parseInt(map.get("userId").toString()));
			boolean flag = i==1?true:false;
			map.put("isAttention", flag);
		}
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> seekAttention(Integer userId,String query) {
		List<Map<String, Object>> resultList = userMapper.seekAttention(userId,query);
		for (Map<String, Object> map : resultList) {
			map.put("picture", PictureUtils.getPictureUrl(map.get("picture")));
			map.put("age", AgeUtils.getAgeFromBirthTime(map.get("age").toString()));
			map.put("vip", vipService.selectVipLevel(Integer.parseInt(map.get("userId").toString())));
		}
		return resultList;
	}

	@Override
	public Map<String, Object> selectById(Integer userId,Integer attention) {
		Map<String, Object> resultMap = userMapper.selectById(attention);
		if(resultMap == null || resultMap.isEmpty()) {
			return null;
		}
		resultMap.put("age", AgeUtils.getAgeFromBirthTime(resultMap.get("age").toString()));
		resultMap.put("vip", vipService.selectVipLevel(attention));
		resultMap.put("level", experienceService.selectLevel(attention));
		resultMap.put("picture", PictureUtils.getPictureUrl(resultMap.get("picture")));
		int i = attentionService.checkAttention(userId, attention);
		boolean flag = i==1?true:false;
		resultMap.put("isAttention", flag);
		//好友人数，动态数量,活动次数
		resultMap.put("attention",attentionService.attentionCount(attention));
		resultMap.put("dynamic",dynamicService.dynamicCount(attention));
		resultMap.put("activity",activityMapper.selectByUserIdCount(attention));
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> listService() {
		List<Map<String, Object>> list = userMapper.listUser(ShanduoConsts.ROLE_SERVICE);
		for (Map<String, Object> map : list) {
			map.put("picture", PictureUtils.getPictureUrl(map.get("picture")));
		}
		return list;
	}

	@Override
	public int addService() {
		List<Map<String, Object>> list1 = userMapper.listUser(ShanduoConsts.ROLE_SERVICE);
		List<Map<String, Object>> list2 = userMapper.listUser(ShanduoConsts.ROLE_USER);
		for (Map<String, Object> map : list1) {
			String userId = map.get("userId").toString();
			for (Map<String, Object> map2 : list2) {
				String service = map2.get("userId").toString();
				boolean flag = ImUtils.addFriend(userId, service);
				if(flag) {
					log.warn("IM添加客服好友失败 userId:{} and service:{}",userId,service);
				}
			}
		}
		return 1;
	}
	
}
