package com.remote.diagnosis.dao.com.diagnosis.commons.log.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解：请求流�?,自动打印访问日志�?
 *
 * @author wyshenjianlin <a
 *         href="mailto:wyshenjianlin@chinabank.com.cn">wyshenjianlin@chinabank.com.cn</a> <br>
 *         QQ: 79043549
 * @version 1.0 2014-4-8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface RequestTrace {

}
