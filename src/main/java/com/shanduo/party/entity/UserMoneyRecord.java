package com.shanduo.party.entity;

import java.math.BigDecimal;
import java.util.Date;

public class UserMoneyRecord {
    private String id;

    private Integer userId;

    private String moneyType;

    private BigDecimal amount;

    private String remarks;

    private Date createDate;

    private Date updateDate;

    private String dalFlag;

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

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType == null ? null : moneyType.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getDalFlag() {
        return dalFlag;
    }

    public void setDalFlag(String dalFlag) {
        this.dalFlag = dalFlag == null ? null : dalFlag.trim();
    }
}