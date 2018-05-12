package com.shanduo.party.entity;

import java.util.Date;

public class ActivityScore {
    private String id;

    private Integer userId;

    private String activityId;

    private Integer score;

    private Integer othersScore;

    private String evaluationContent;

    private String beEvaluated;

    private String remarks;

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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId == null ? null : activityId.trim();
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getOthersScore() {
        return othersScore;
    }

    public void setOthersScore(Integer othersScore) {
        this.othersScore = othersScore;
    }

    public String getEvaluationContent() {
        return evaluationContent;
    }

    public void setEvaluationContent(String evaluationContent) {
        this.evaluationContent = evaluationContent == null ? null : evaluationContent.trim();
    }

    public String getBeEvaluated() {
        return beEvaluated;
    }

    public void setBeEvaluated(String beEvaluated) {
        this.beEvaluated = beEvaluated == null ? null : beEvaluated.trim();
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