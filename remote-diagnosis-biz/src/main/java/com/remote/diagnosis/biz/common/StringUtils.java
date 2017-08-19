package com.remote.diagnosis.biz.common;
/**
 * Created  on 2017/4/6.
 *
 * @author liwei
 */
public class StringUtils {

    public static final String SPLIT_STR = "/";
	public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
	 public static boolean isNotEmpty(CharSequence cs) {
	        return !StringUtils.isEmpty(cs);
	    }
	 public static boolean isEmpty(CharSequence cs) {
	        return cs == null || cs.length() == 0;
	    }
}
