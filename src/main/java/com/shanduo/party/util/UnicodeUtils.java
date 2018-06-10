package com.shanduo.party.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 装换16进制工具类
 * @ClassName: AUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月10日 上午10:36:35
 *
 */
public class UnicodeUtils {

	/**
     * 字符串转换unicode
     */
    public static String stringToUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    /**
     * 把十六进制Unicode编码字符串转换为中文字符串
     */
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{2,4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }
}
