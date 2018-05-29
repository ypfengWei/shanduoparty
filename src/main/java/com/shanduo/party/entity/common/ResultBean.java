package com.shanduo.party.entity.common;

/**
 * 结果bean父类
 * @ClassName: ResultBean
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月29日 上午9:50:30
 *
 */
public class ResultBean {
	
	/**
	 * 业务结果
	 */
	private boolean success;
    
	/**
	 * 正确返回值
	 */
	private Object result;
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
}
