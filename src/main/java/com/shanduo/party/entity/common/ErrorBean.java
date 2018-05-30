package com.shanduo.party.entity.common;

/**
 * 错误结果bean
 * @ClassName: ErrorBean
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月29日 上午9:52:41
 *
 */
public class ErrorBean extends ResultBean {

	/**
	 * 错误码
	 */
    private Integer errCode;
    /**
     * 错误原因
     */
    private String errCodeDes;

    public ErrorBean() {
    	this.setSuccess(false);
    }
    
    /**
     * 报错返回
     * <p>Title: </p>
     * <p>Description: </p>
     * @param errorCode 错误码
     * @param errCodeDes 错误原因
     */
    public ErrorBean(Integer errorCode,String errCodeDes) {
    	this.setSuccess(false);
    	this.setErrCode(errorCode);
    	this.setErrCodeDes(errCodeDes);
    }

	public Integer getErrCode() {
		return errCode;
	}

	public void setErrCode(Integer errCode) {
		this.errCode = errCode;
	}

	public String getErrCodeDes() {
		return errCodeDes;
	}

	public void setErrCodeDes(String errCodeDes) {
		this.errCodeDes = errCodeDes;
	}
    
}
