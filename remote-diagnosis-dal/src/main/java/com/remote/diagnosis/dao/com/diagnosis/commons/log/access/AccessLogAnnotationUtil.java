package com.remote.diagnosis.dao.com.diagnosis.commons.log.access;

import com.remote.diagnosis.dao.com.diagnosis.commons.util.ClassUtil;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.Logger;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.remote.diagnosis.dao.com.diagnosis.commons.util.CachedAnnotationUtil.isAnnotationExist;

/**
 * AccessLog工具类，提取参数中的关于请求流水、响应流水�?�业务参数�?�返回码等注解信�?
 *
 * @author wyshenjianlin <a
 *         href="mailto:wyshenjianlin@chinabank.com.cn">wyshenjianlin@chinabank.com.cn</a> <br>
 *         QQ: 79043549
 * @version 1.0 2014-4-8
 */
public class AccessLogAnnotationUtil {
    private static final int PRINT_THRESHOLD = 2000000;//2ms
    private static final Logger LOGGER = new Logger();
    /**
     * 提取结果�? Annotation字段
     *
     * @param accessLogParameters Log参数
     * @param result              调用结果
     * @throws IllegalAccessException
     */
    public static void extractResultAnnotations(AccessLogParameters accessLogParameters, Object result) throws IllegalAccessException {
        if (result == null || skipScanAnnotation(result.getClass())) {
            return;  //balking
        }
        long startTime = System.nanoTime();
        // 从响应中获取响应码，业务数据
        Class<?> clazz = result.getClass();
        do {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (isAnnotationExist(clazz, field, BusinessElement.class)) {
                    Object value = readField(result, field);
                    accessLogParameters.addBusinessElement(value);
                } else if (isAnnotationExist(clazz, field, ResponseCode.class)) {
                    Object value = readField(result, field);
                    accessLogParameters.setResponseCode(value);
                } else if (isAnnotationExist(clazz, field, ResponseTrace.class)) {
                    Object value = readField(result, field);
                    accessLogParameters.setResponseTrace(value);
                }  else if (isAnnotationExist(clazz, field, ResponseDesc.class)) {
                    Object value = readField(result, field);
                    accessLogParameters.setResponseDesc(value);
                }
            }
            clazz = clazz.getSuperclass();
        } while (!skipScanAnnotation(clazz));
        Logger.timeSpentNan("extractResultAnnotations", startTime, PRINT_THRESHOLD, PRINT_THRESHOLD);
    }

    private static final Object readField(final Object target, final Field field) throws IllegalAccessException {
        long startTime = System.nanoTime();
        Object value = FieldUtils.readField(field, target, true);
        Logger.timeSpentNan("readField", startTime, PRINT_THRESHOLD, PRINT_THRESHOLD);
        return value;
    }

    /**
     * 提取请求参数内部注解字段
     *
     * @param accessLogParameters Log参数
     * @param args
     * @throws IllegalAccessException
     */
    public static void extractRequestAnnotations(AccessLogParameters accessLogParameters, Object[] args) throws IllegalAccessException {
        long startTime = System.nanoTime();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null || skipScanAnnotation(args[i].getClass())) {
                continue;
            }
            // 自定义类型， 遍历字段
            Class<?> clazz = args[i].getClass();
            do {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (isAnnotationExist(clazz, field, RequestTrace.class)) {
                        Object value = readField(args[i], field);
                        accessLogParameters.setRequestTrace(value);
                    } else if (isAnnotationExist(clazz, field, BusinessElement.class)) {
                        Object value = readField(args[i], field);
                        accessLogParameters.addBusinessElement(value);
                    }
                }
                clazz = clazz.getSuperclass();
            } while (!skipScanAnnotation(clazz));
        }
        Logger.timeSpentNan("extractRequestAnnotations", startTime, PRINT_THRESHOLD, PRINT_THRESHOLD);
    }

    /**
     * 提取请求参数注解
     *
     * @param accessLogParameters Log参数
     * @param method              远程调用方法                       y
     * @param args
     * @throws java.lang.NullPointerException
     */
    public static void extractMethodParameterAnnotations(AccessLogParameters accessLogParameters, Method method, Object[] args) {
        long startTime = System.nanoTime();
        Annotation[][] parameterAnnotationArrays = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotationArrays.length; i++) {
            //有参数注�?
            if (parameterAnnotationArrays[i].length > 0) {
                for (int j = 0; j < parameterAnnotationArrays[i].length; j++) {
                    // 每一个参数的注解列表
                    Annotation annotation = parameterAnnotationArrays[i][j];
                    if (annotation instanceof RequestTrace) {
                        accessLogParameters.setRequestTrace(args[i]);
                    } else if (annotation instanceof BusinessElement) {
                        accessLogParameters.addBusinessElement(args[i]);
                    }
                }
            }
        }
        Logger.timeSpentNan("extractMethodParameterAnnotations", startTime, PRINT_THRESHOLD, PRINT_THRESHOLD);
    }

    /**
     * 跳过基础类型字段扫描
     *
     * @param clazz 参数类型
     * @return true 如果 requestParamType 是基�?类型，或者JDK本身的类
     */
    public static boolean skipScanAnnotation(Class<?> clazz) {
        boolean primitiveOrWrapper = ClassUtils.isPrimitiveOrWrapper(clazz);
        boolean ignorePackages = ClassUtil.isSdkPackage(clazz);
        // 忽略基础类型 ,JDK本身的类
        return primitiveOrWrapper || ignorePackages;
    }
}
