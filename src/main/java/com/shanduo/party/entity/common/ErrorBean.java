package com.shanduo.party.entity.common;

/**
 * @Description : 错误结果bean
 * @Author : 
 * @Creation Date : 2018-4-13 上午9:00:51
 */
public class ErrorBean extends ResultBean {

    private String errorCode;

    public ErrorBean() {
        this.setSuccess(false);
    }

    public ErrorBean(String errorCode) {
        this.setSuccess(false);
        this.setErrorCode(errorCode);
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
