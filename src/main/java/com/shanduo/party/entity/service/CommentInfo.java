package com.shanduo.party.entity.service;

import java.util.Date;
import java.util.List;

/**
 * 动态评论内容INFO
 * @ClassName: CommentInfo
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月15日 下午7:27:01
 *
 */
public class CommentInfo {
	private String id;//评论ID
	private String dynamicId;//动态ID
	private Integer userId;//用户ID
	private String portraitId;//头像
	private String name;//昵称
	private String age;//年龄
	private String gender;//性别
	private String comment;//评论内容
	private Integer count;//2级回复数量
	private List<String> picture;//图片
	private Date createDate;//评论时间
	private List<Comments> comments;//2级回复集合
	public class Comments{
		private String id;//2级评论ID
		private String dynamicId;//动态ID
		private Integer userId;//评论人ID
		private String userName;//评论人昵称
		private String portraitId;//评论人头像
		private Integer replyId;//被回复人ID
		private String replyName;//被回复人昵称
		private String comment;//回复内容
		private List<String> picture;//回复图片
		private Date createDate;//回复时间
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDynamicId() {
			return dynamicId;
		}
		public void setDynamicId(String dynamicId) {
			this.dynamicId = dynamicId;
		}
		public Integer getUserId() {
			return userId;
		}
		public void setUserId(Integer userId) {
			this.userId = userId;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getPortraitId() {
			return portraitId;
		}
		public void setPortraitId(String portraitId) {
			this.portraitId = portraitId;
		}
		public Integer getReplyId() {
			return replyId;
		}
		public void setReplyId(Integer replyId) {
			this.replyId = replyId;
		}
		public String getReplyName() {
			return replyName;
		}
		public void setReplyName(String replyName) {
			this.replyName = replyName;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public List<String> getPicture() {
			return picture;
		}
		public void setPicture(List<String> picture) {
			this.picture = picture;
		}
		public Date getCreateDate() {
			return createDate;
		}
		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDynamicId() {
		return dynamicId;
	}
	public void setDynamicId(String dynamicId) {
		this.dynamicId = dynamicId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getPortraitId() {
		return portraitId;
	}
	public void setPortraitId(String portraitId) {
		this.portraitId = portraitId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public List<String> getPicture() {
		return picture;
	}
	public void setPicture(List<String> picture) {
		this.picture = picture;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public List<Comments> getComments() {
		return comments;
	}
	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}
	
}
