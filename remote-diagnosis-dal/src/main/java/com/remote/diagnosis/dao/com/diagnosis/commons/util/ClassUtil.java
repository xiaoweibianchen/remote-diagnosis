package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import org.apache.commons.lang3.ClassUtils;

/**
 * Created with IntelliJ IDEA.
 * User: wyshenjianlin
 * Date: 13-9-11
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class ClassUtil extends ClassUtils {
    private static final String[] SDK_PACKAGES_PREFIX = {"java", "sun.", "oracle.", "org.springframework."};

    public static String getPackageAsPath(Class<?> cls, String fileName) {
        String packagePath = getPackageAsPath(cls);
        if (StringUtil.isEmpty(fileName)) {
            return packagePath;
        } else {
            return new StringBuilder(getPackageAsPath(cls)).append("/").append(fileName).toString();
        }
    }

    public static String getPackageAsPath(Class<?> cls) {
        return cls == null ? "" : StringUtil.replace(cls.getPackage().getName(), ".", "/");
    }

    /**
     * 类的包名是否是JDK内置�?
     *
     * @param clazz 请求参数类型
     * @return true 如果类的包名是否是JDK内置�?
     */
    public static boolean isSdkPackage(Class<?> clazz) {
        // add by shenjl at 2014-7-24 针对int、long等基�?类型
        if(clazz == null || clazz.getPackage() == null){
            return true;
        }
        final String packageName = clazz.getPackage().getName();
        for (String prefix : SDK_PACKAGES_PREFIX) {
            if (packageName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
