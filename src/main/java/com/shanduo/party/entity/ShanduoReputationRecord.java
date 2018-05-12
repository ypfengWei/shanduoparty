package com.shanduo.party.entity;

import java.util.Date;

public class ShanduoReputationRecord {
    private String id;

    private Integer userId;

    private String reputationType;

    private Integer deductionCount;

    private Date createDate;

    private Date updateDate;

    private String delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getReputationType() {
        return reputationType;
    }

    public void setReputationType(String reputationType) {
        this.reputationType = reputationType == null ? null : reputationType.trim();
    }

    public Integer getDeductionCount() {
        return deductionCount;
    }

    public void setDeductionCount(Integer deductionCount) {
        this.deductionCount = deductionCount;
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