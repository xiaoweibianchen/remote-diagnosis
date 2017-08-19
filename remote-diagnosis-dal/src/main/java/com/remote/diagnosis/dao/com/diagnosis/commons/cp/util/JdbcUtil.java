package com.remote.diagnosis.dao.com.diagnosis.commons.cp.util;

import org.apache.commons.codec.binary.Base64;

import com.remote.diagnosis.dao.com.diagnosis.commons.util.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JdbcUtil
 * @author wuhongqiang.taige
 */
public class JdbcUtil {
    private static final Logger LOGGER = new Logger();

    public final static String CRLF = System.getProperty("line.separator");

    /**
     * Create a new instance of <code>Driver</code> with the driver class name.
     */
    public static Driver createDriver(String driverClassName) throws SQLException {
        Class<?> clazz = null;

        try {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                clazz = contextLoader.loadClass(driverClassName);
            }
        } catch (ClassNotFoundException e) {
            // skip. will check later.
        }

        if (clazz == null) {
            try {
                clazz = Class.forName(driverClassName);
            } catch (ClassNotFoundException e) {
                throw new SQLException(e.getMessage(), e);
            }
        }

        try {
            return (Driver) clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new SQLException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * @param rs ResultSet
     */
    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.warn("E close(ResultSet)...", e);
            }
        }
    }
    /**
     * @param stmt Statement
     */
    public static void closeQuietly(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.warn("E close(Statement)...", e);
            }
        }
    }

    //add by shenjl 增加AES加密方式，固定密钥和向量
    private static byte[] pwdAesRawKey = "f[j@R#?]qM(#}rI$".getBytes();
    private static byte[] IV = pwdAesRawKey; // key == iv
    public static String decodePassword(String secretBase64) throws Exception {
        SecretKeySpec key = new SecretKeySpec(pwdAesRawKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV);//使用CBC模式，需要一个向量iv，可增加加密算法的强�?
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decode = cipher.doFinal(Base64.decodeBase64(secretBase64));
        return new String(decode, "UTF-8");
    }


    public static String multiLinesToOneLine(String lines, String replacement) {
        if (replacement == null) {
            replacement = "";
        }
        String str1 = replace(lines, "\r\n", replacement);
        String str2 = replace(str1, "\r", replacement);
        String str3 = replace(str2, "\n", replacement);
        return str3;
    }

    //以下方法copy from org.apache.commons.lang3.StringUtils，为了让wangyincp减少依赖

    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    public static String replace(String text, String searchString, String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

}
