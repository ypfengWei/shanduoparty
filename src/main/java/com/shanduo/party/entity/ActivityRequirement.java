package com.shanduo.party.entity;

import java.util.Date;

public class ActivityRequirement {
    private String id;  //活动要求Id
 
    private String activityId; //活动id

    private String mode; //类型

    private Integer manNumber; //男生人数

    private Integer womanNumber; //女生人数
 
    private String shanduoLabel; //标签

    private String remarks; //备注

    private Date createDate; //创建时间

    private Date updateDate; //修改时间

    private String delFlag; //删除标志

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId == null ? null : activityId.trim();
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode == null ? null : mode.trim();
    }

    public Integer getManNumber() {
        return manNumber;
    }

    public void setManNumber(Integer manNumber) {
        this.manNumber = manNumber;
    }

    public Integer getWomanNumber() {
        return womanNumber;
    }

    public void setWomanNumber(Integer womanNumber) {
        this.womanNumber = womanNumber;
    }

    public String getShanduoLabel() {
        return shanduoLabel;
    }

    public void setShanduoLabel(String shanduoLabel) {
        this.shanduoLabel = shanduoLabel == null ? null : shanduoLabel.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag == null ? null : delFlag.trim();
    }
}