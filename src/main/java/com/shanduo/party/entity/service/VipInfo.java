package com.shanduo.party.entity.service;

import com.shanduo.party.util.PictureUtils;

public class VipInfo {
	private String userName; //用户昵称
	
	private Integer userId;  //用户id
	
    private String headPortraitId; //头像
    
    private String birthday;//生日
    
    private Integer age; //年龄
    
    private String gender; //性别：0:女 1:男
    
    private Integer vipGrade; //会员等级
    
    private Integer experience; //会员经验

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getHeadPortraitId() {
		return headPortraitId;
	}

	public void setHeadPortraitId(String headPortraitId) {
		this.headPortraitId = PictureUtils.getPictureUrl(headPortraitId);
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getVipGrade() {
		return vipGrade;
	}

	public void setVipGrade(Integer vipGrade) {
		this.vipGrade = vipGrade;
	}

	public Integer getExperience() {
		return experience;
	}

	public void setExperience(Integer experience) {
		this.experience = experience;
	}
    
}
