package com.shanduo.party.im;

public class ImConfig {

	/**
	 * AppId
	 */
	public static final Long APP_ID = 1400088239L;
	
	/**
	 * 管理员账号
	 */
	public static final String IDENTIFIER = "admin1";
	
	/**
	 * 私钥
	 */
	public static final String PRIVATE_STR = "-----BEGIN PRIVATE KEY-----\n" + 
    		"MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgPYbBhtxXJtl4F6Mq\n" + 
    		"23MBuvY+wZX6goW7PfQuxCAi8gKhRANCAASH3S6Wdi3qlMBSiPiQqxi79CX9EP72\n" + 
    		"9ljNLOCLU2eG3dgEiGPHBik3IM0rs2FvYG03EDTeUC1WkciUdo9ow325\n" + 
    		"-----END PRIVATE KEY-----";
	
	/**
	 * 公钥
	 */
	public static final String PUBLIV_STR = "-----BEGIN PUBLIC KEY-----\n" + 
    		"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEh90ulnYt6pTAUoj4kKsYu/Ql/RD+\n" + 
    		"9vZYzSzgi1Nnht3YBIhjxwYpNyDNK7Nhb2BtNxA03lAtVpHIlHaPaMN9uQ==\n" + 
    		"-----END PUBLIC KEY-----";
	
	public static final String ACCOUNT_IMPORT = "im_open_login_svc/account_import";//账号导入
	public static final String PORTRAIT_SET = "profile/portrait_set";//设置资料
    public static final String FRIEND_ADD = "sns/friend_add";//增加好友
    public static final String FRIEND_DELETE = "sns/friend_delete";//删除好友
    public static final String GROUP_LIST = "group_open_http_svc/get_joined_group_list";//获取用户所加入的群组
    public static final String GROUP_INFO = "group_open_http_svc/get_group_info";//获取群组详细资料
    public static final String MODIFY_GROUP_BASE_INFO = "group_open_http_svc/modify_group_base_info";//修改群组基础资料
    public static final String DESTROY_GROUP = "group_open_http_svc/destroy_group";//解散群组
}
