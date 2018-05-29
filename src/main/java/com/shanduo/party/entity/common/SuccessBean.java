package com.shanduo.party.entity.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 成功结果Bean
 * @ClassName: SuccessBean
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月29日 上午9:49:47
 *
 */
public class SuccessBean extends ResultBean {
	
	public SuccessBean() {
        this.setSuccess(true);
    }

    public SuccessBean(Object result) {
        this.setSuccess(true);
        this.setResult(result);
    }

    /**
     * 单一结果返回的快速方法
     * <p>Title: </p>
     * <p>Description: </p>
     * @param key
     * @param value
     */
    public SuccessBean(String key, Object value) {
    	this.setSuccess(true);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(key, value);
        this.setResult(resultMap);
    }

}
