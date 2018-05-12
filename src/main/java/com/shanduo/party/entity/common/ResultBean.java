package com.shanduo.party.entity.common;

/**
 * @Description : 结果bean父类
 * @Author : 
 * @Creation Date : 2018-4-13 上午9:01:45
 */
public class ResultBean {

    private boolean success;

    private Object result;

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the result
     */
    public Object getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(Object result) {
        this.result = result;
    }

}
