package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA for myChinaBank
 * User: taige
 * Date: 13-8-20
 * Time: 下午4:31
 */
public class LoggerFactory {

    private static final ConcurrentMap<String, Logger> LOGGERS = new ConcurrentHashMap<String, Logger>();

    public static Logger getLogger() {
        return new Logger(LoggerFactory.class);
    }

    public static Logger getLogger(String className) {
        Logger logger = LOGGERS.get(className);
        if (logger == null) {
            LOGGERS.putIfAbsent(className, new Logger(className));
            logger = LOGGERS.get(className);
        }
        return logger;
    }

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

}
