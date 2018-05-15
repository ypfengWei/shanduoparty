package com.shanduo.party.entity.service;

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
	private String comment;//评论内容
	private List<String> picture;//图片
	private String delFlag;//删除标记
	private List<Comments> comments;//2级回复集合
	
	public class Comments{
		private String id;//2级评论ID
		private String dynamicId;//动态ID
		private Integer userIdOne;//评论人ID
		private String nameOne;//评论人昵称
		private String portraitId;//评论人头像
		private Integer userIdTwo;//被回复人ID
		private String nameTwo;//被回复人昵称
		private String comment;//回复内容
		private List<String> picture;//回复图片
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
		public Integer getUserIdOne() {
			return userIdOne;
		}
		public void setUserIdOne(Integer userIdOne) {
			this.userIdOne = userIdOne;
		}
		public String getNameOne() {
			return nameOne;
		}
		public void setNameOne(String nameOne) {
			this.nameOne = nameOne;
		}
		public String getPortraitId() {
			return portraitId;
		}
		public void setPortraitId(String portraitId) {
			this.portraitId = portraitId;
		}
		public Integer getUserIdTwo() {
			return userIdTwo;
		}
		public void setUserIdTwo(Integer userIdTwo) {
			this.userIdTwo = userIdTwo;
		}
		public String getNameTwo() {
			return nameTwo;
		}
		public void setNameTwo(String nameTwo) {
			this.nameTwo = nameTwo;
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

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}
	
}
