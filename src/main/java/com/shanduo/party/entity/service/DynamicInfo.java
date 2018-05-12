package com.shanduo.party.entity.service;

import java.util.Date;
import java.util.List;

public class DynamicInfo {
	private String id;//动态ID
	private Integer userId;//用户ID
	private String name;//用户昵称
	private String portraitId;//头像
	private String age;//年龄
	private String content;//动态内容
	private List<String> picture;//动态图片或视频
	private Integer praise;//点赞人数
	private boolean isPraise;//是否点赞
	private double location;//距离 好友动态没有
	private String remarks;//备注
	private Date createDate;//发动态的时间
	private Integer vip;//VIP等级
	private String gender;//性别
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPortraitId() {
		return portraitId;
	}
	public void setPortraitId(String portraitId) {
		this.portraitId = portraitId;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getPicture() {
		return picture;
	}
	public void setPicture(List<String> picture) {
		this.picture = picture;
	}
	public Integer getPraise() {
		return praise;
	}
	public void setPraise(Integer praise) {
		this.praise = praise;
	}
	public boolean isPraise() {
		return isPraise;
	}
	public void setPraise(boolean isPraise) {
		this.isPraise = isPraise;
	}
	public double getLocation() {
		return location;
	}
	public void setLocation(double location) {
		this.location = location;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Integer getVip() {
		return vip;
	}
	public void setVip(Integer vip) {
		this.vip = vip;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
}
