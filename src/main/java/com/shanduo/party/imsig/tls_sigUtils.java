package com.shanduo.party.imsig;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.json.JSONException;

import com.shanduo.party.imsig.tls_sigature.CheckTLSSignatureResult;
import com.shanduo.party.imsig.tls_sigature.GenTLSSignatureResult;

/**
 * TLS签名生成
 * @ClassName: tls_sigDemo
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月2日 下午2:39:39
 *
 */
public class tls_sigUtils {

	/**
	 * sdkAppId
	 */
	private static final long SdkAppId = 1400088239;
	
	/**
	 * 私钥
	 */
	private static final String privStr = "-----BEGIN PRIVATE KEY-----\n" + 
    		"MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgPYbBhtxXJtl4F6Mq\n" + 
    		"23MBuvY+wZX6goW7PfQuxCAi8gKhRANCAASH3S6Wdi3qlMBSiPiQqxi79CX9EP72\n" + 
    		"9ljNLOCLU2eG3dgEiGPHBik3IM0rs2FvYG03EDTeUC1WkciUdo9ow325\n" + 
    		"-----END PRIVATE KEY-----";
	
	/**
	 * 公钥
	 */
	private static final String pubStr = "-----BEGIN PUBLIC KEY-----\n" + 
    		"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEh90ulnYt6pTAUoj4kKsYu/Ql/RD+\n" + 
    		"9vZYzSzgi1Nnht3YBIhjxwYpNyDNK7Nhb2BtNxA03lAtVpHIlHaPaMN9uQ==\n" + 
    		"-----END PUBLIC KEY-----";
	
	/**
	 * 生成签名
	 * @Title: getSig
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @param @throws IOException
	 * @return String
	 * @throws
	 */
	public static String getSig(String userId) throws IOException {
		 GenTLSSignatureResult result = tls_sigature.GenTLSSignatureEx(SdkAppId, userId, privStr);
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
		CheckTLSSignatureResult checkResult = tls_sigature.CheckTLSSignatureEx(sig, SdkAppId, userId, pubStr);
        if(checkResult.verifyResult == false) {
            return false;
        }
        return true;
	}
	
}
