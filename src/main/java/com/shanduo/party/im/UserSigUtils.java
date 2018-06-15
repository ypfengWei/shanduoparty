package com.shanduo.party.im;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.json.JSONException;

import com.shanduo.party.im.tls_sigature.CheckTLSSignatureResult;
import com.shanduo.party.im.tls_sigature.GenTLSSignatureResult;

/**
 * TLS签名生成
 * @ClassName: tls_sigDemo
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月2日 下午2:39:39
 *
 */
public class UserSigUtils {

	/**
	 * 生成签名
	 * @Title: getSig
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getSig(String userId) {
		GenTLSSignatureResult result = null;
		try {
			result = tls_sigature.GenTLSSignatureEx(ImConfig.APP_ID, userId, ImConfig.PRIVATE_STR);
		} catch (IOException e) {
			e.printStackTrace();
		}
        if (0 == result.urlSig.length()) {
        	return "";
        }
		return result.urlSig;
	}
	
	/**
	 * 检查签名
	 * @Title: checkSig
	 * @Description: TODO
	 * @param @param sig
	 * @param @param userId
	 * @param @return
	 * @param @throws DataFormatException
	 * @param @throws JSONException
	 * @return boolean
	 * @throws
	 */
	public static boolean checkSig(String sig,String userId) throws DataFormatException, JSONException {
		CheckTLSSignatureResult checkResult = tls_sigature.CheckTLSSignatureEx(sig, ImConfig.APP_ID, userId, ImConfig.PUBLIV_STR);
        if(checkResult.verifyResult == false) {
            return false;
        }
        return true;
	}
	
}
