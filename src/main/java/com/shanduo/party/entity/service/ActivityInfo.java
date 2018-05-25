package com.shanduo.party.entity.service;

import com.shanduo.party.util.PictureUtils;

public class ActivityInfo {
	
	private String id; //活动id
	 
	private String userName; //用户昵称
	
	private String activityName; //活动标题
	
	private String mode; //消费方式
	
	private String activityStartTime; //活动开始时间
	
	private String activityAddress; //活动地址
	
	private String detailedAddress; //详细地址
	
	private String remarks; //活动内容
	
	private String manNumber; //男生人数
	
	private String womanNumber; //女生人数
	
	private Integer score; //评分
	
	private Integer userId;  //发起者id
	
	private String activityCutoffTime; //活动截止时间
    
    private String headPortraitId; //头像
    
    private String birthday;//生日
    
    private Integer age; //年龄
    
    private String topFlag; //置顶标记
    
    private double lon; //经度
    
    private double lat; //纬度
    
    private double location; //距离
    
    private String gender; //性别：0:女 1:男
    
    private Integer vipGrade; //会员等级
    
    private Integer evaluationSign; // 参与者评论标识  0:未评价 1:已评价
    
    private Integer beEvaluationSign; // 发起者评论标识 0:未评价 1:已评价
    
    private Integer othersScore; //发起者对参与者评分
    
    private Integer userIds; // 参与者id
    
    private Integer typeId; // 调用接口类型 0:调用参与者评价接口  1:调用发起者评价接口
    
	public ActivityInfo(){
		
	}

	public Integer getEvaluationSign() {
		return evaluationSign;
	}

	public void setEvaluationSign(Integer evaluationSign) {
		this.evaluationSign = evaluationSign;
	}

	public Integer getVipGrade() {
		return vipGrade;
	}

	public void setVipGrade(Integer vipGrade) {
		this.vipGrade = vipGrade;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getActivityName() {
		return activityName;
	}
	
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public String getActivityAddress() {
		return activityAddress;
	}
	
	public void setActivityAddress(String activityAddress) {
		this.activityAddress = activityAddress;
	}
	
	public String getDetailedAddress() {
		return detailedAddress;
	}

	public void setDetailedAddress(String detailedAddress) {
		this.detailedAddress = detailedAddress;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getManNumber() {
		return manNumber;
	}
	public void setManNumber(String manNumber) {
		this.manNumber = manNumber;
	}
	public String getWomanNumber() {
		return womanNumber;
	}
	public void setWomanNumber(String womanNumber) {
		this.womanNumber = womanNumber;
	}
	
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getActivityStartTime() {
		return activityStartTime;
	}

	public void setActivityStartTime(String activityStartTime) {
		this.activityStartTime = activityStartTime;
	}

	public String getActivityCutoffTime() {
		return activityCutoffTime;
	}

	public void setActivityCutoffTime(String activityCutoffTime) {
		this.activityCutoffTime = activityCutoffTime;
	}

	public String getHeadPortraitId() {
		return headPortraitId;
	}

	public void setHeadPortraitId(String headPortraitId) {
		this.headPortraitId =  PictureUtils.getPictureUrl(headPortraitId);
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

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLocation() {
		return location;
	}

	public void setLocation(double location) {
		this.location = location;
	}

	public String getTopFlag() {
		return topFlag;
	}

	public void setTopFlag(String topFlag) {
		this.topFlag = topFlag;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getBeEvaluationSign() {
		return beEvaluationSign;
	}

	public void setBeEvaluationSign(Integer beEvaluationSign) {
		this.beEvaluationSign = beEvaluationSign;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getUserIds() {
		return userIds;
	}

	public void setUserIds(Integer userIds) {
		this.userIds = userIds;
	}

	public Integer getOthersScore() {
		return othersScore;
	}

	public void setOthersScore(Integer othersScore) {
		this.othersScore = othersScore;
	}

}
