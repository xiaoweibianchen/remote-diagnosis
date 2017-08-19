package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.AggregationType;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * Created with IntelliJ IDEA.
 * User: wyshenjianlin
 * Date: 13-9-6
 * Time: 下午3:38
 */
public class BeanUtil extends MethodUtils {
    private static final Logger _log = new Logger();

    public static void invokeSetterQuietly(Object bean, String name, Object value) {
        try {
            invokeSetter(bean, name, value);
        } catch (Exception e) {
            _log.warnf("E invokeSetterQuietly(%s).set(%s,%s)", bean.getClass().getSimpleName(), name, value);
        }
    }

    public static void invokeSetter(Object bean, String name, Object value) {
        PropertySetter setter = new PropertySetter(bean);
        AggregationType aggregationType = setter.computeAggregationType(name);
        //根据标签找到对应的setget方法，并且get方法返回类型为基本类型，则将属�?�直接注入给bean
        if (aggregationType == AggregationType.AS_BASIC_PROPERTY) {
            setter.setProperty(name, value == null ? null : value.toString());
        } else {
            setter.setComplexProperty(name, value);
        }
    }
}
