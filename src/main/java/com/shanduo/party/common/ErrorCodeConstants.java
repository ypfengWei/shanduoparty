package com.shanduo.party.common;

public class ErrorCodeConstants {

	public static final String USER_TOKEN_ERROR = "用户登录超时";
	public static final String USER_TOKEN_PASTDUR = "登录已失效";
	public static final String TOKEN_TEXT_ISNULL = "Token为空";
	public static final String USER_LIMITED_AUTHORITY = "权限不足";
	public static final String PAGE_NONUMBER = "页数格式错误";
	public static final String CORRECT_DATA ="输入正确的数据";
	public static final String SERVER_INTERNAL_ERROR = "服务器内部错误";
	public static final String POWERED_UPDATE_ERROR = "数据库:修改错误";
	public static final String POWERED_INSERT_ERROR = "数据库:插入错误";
	public static final String POWERED_DELETE_ERROR = "数据库:删除错误";
	public static final String PARAMETER_FORMAT = "参数格式错误";
	public static final String PARAMETER = "参数错误";
    public static final String CARD_USERNAME_IS_NULL = "用户不能为空";
    public static final String RC_INFO_OPERATEAMOUNT_IS_NULL = "充值金额为空";
    public static final String RC_INFO_OPERATEAMOUNT_IS_MINUS = "充值金额必须大于零";
    public static final String RC_INFO_DECLINE = "拒绝操作，您没有权限进行此项操作";
    public static final String RC_UNKNOWN = "充值未知错误";
    
    public static final int TOKEN_INVALID = 10001; //token失效
    public static final int PARAMETER_ERROR = 10002; //参数错误
    public static final int BACKSTAGE_ERROR = 10003; //数据库操作错误
    public static final int LOGIN_FAILURE = 10010; //登录失败
    public static final int UNBOUND = 10011; //未绑定
    public static final int BINDINGS_FAILURE = 10012; //绑定失败
    
}