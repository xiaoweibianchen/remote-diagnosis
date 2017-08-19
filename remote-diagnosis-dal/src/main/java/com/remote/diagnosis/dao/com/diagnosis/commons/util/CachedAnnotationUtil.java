package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Annotation工具类，查询Field, Method ,Class 是否包含指定的Annotation
 *
 * @author wyshenjianlin <a
 *         href="mailto:wyshenjianlin@chinabank.com.cn">wyshenjianlin@chinabank.com.cn</a> <br>
 *         QQ: 79043549
 * @version 1.0 2014-4-8
 */
public class CachedAnnotationUtil {
    private static final int DEFAULT_INITIAL_CAPACITY = 1024;  //尽量避免rehash
    private static final ConcurrentHashMap<String, Annotation> annotationsMap = new ConcurrentHashMap<String, Annotation>(DEFAULT_INITIAL_CAPACITY);

    private static final Annotation getAnnotation(final AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType, final String key) {
        Annotation cachedResult = annotationsMap.get(key);
        if (cachedResult != null) {
            return cachedResult;
        }
        Annotation annotation = annotatedElement.getAnnotation(annotationType); // 可能被多次调�?
        Annotation result = annotation != null ? annotation : NullAnnotation.getInstance();
        annotationsMap.putIfAbsent(key, result);
        return result;
    }

    private static final boolean isAnnotationExist(final AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType, final String key) {
        Annotation annotation = getAnnotation(annotatedElement, annotationType, key);
        return !(annotation instanceof NullAnnotation);
    }

    /**
     * 查找clazz的field上是否包含指定annotationType的Annotation
     *
     * @param clazz          被查找的Class
     * @param field          clazz中的字段
     * @param annotationType 注解类型     z
     * @return true if clazz的field上包含指定annotationType的Annotation
     */
    public static final boolean isAnnotationExist(final Class<?> clazz, final Field field, final Class<? extends Annotation> annotationType) {
        String key = new StringBuilder(clazz.getName()).append(field.getName()).append(annotationType.getName()).toString();
        return isAnnotationExist(field, annotationType, key);
    }

    /**
     * 查找clazz的method上是否包含指定annotationType的Annotation
     *
     * @param clazz          被查找的Class
     * @param method         clazz中的方法
     * @param annotationType 注解类型
     * @return true if clazz的method上包含指定annotationType的Annotation
     */
    public static final boolean isAnnotationExist(final Class<?> clazz, final Method method, final Class<? extends Annotation> annotationType) {
        String key = new StringBuilder(clazz.getName()).append(method.getName()).append(annotationType.getName()).toString();
        return isAnnotationExist(method, annotationType, key);
    }

    /**
     * 查找clazz是否包含指定annotationType的Annotation
     *
     * @param clazz          被查找的Class
     * @param annotationType 注解类型
     * @return true if clazz包含指定annotationType的Annotation
     */
    public static final boolean isAnnotationExist(final Class<?> clazz, final Class<? extends Annotation> annotationType) {
        String key = new StringBuilder(clazz.getName()).append(annotationType.getName()).toString();
        return isAnnotationExist(clazz, annotationType, key);
    }

    /**
     * ConcurrentHashMap 不允许为键�?�对为Null，创建NullObject
     */
    private static class NullAnnotation implements Annotation {
        private static NullAnnotation singletonInstance = new NullAnnotation();

        public static NullAnnotation getInstance() {
            return singletonInstance;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return NullAnnotation.class;
        }
    }
}
