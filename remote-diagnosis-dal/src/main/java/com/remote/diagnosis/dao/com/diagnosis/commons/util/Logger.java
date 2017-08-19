package com.remote.diagnosis.dao.com.diagnosis.commons.util;

/*******************************************************************************
 * 模式符号 - 用�??(附加说明);{可�?�附加�?�项}(附加选项说明)
 * c - 日志名称(通常是构造函数的参数);{数字}("a.b.c" 的名称使�? %c{2} 会输�? "b.c")
 * C - 调用者的类名(速度�?,不推荐使�?);{数字}(同上)
 * d - 日志时间;{SimpleDateFormat�?能使用的格式}
 * F - 调用者的文件�?(速度极慢,不推荐使�?)
 * l - 调用者的函数名�?�文件名、行�?(速度极其极其�?,不推荐使�?)
 * L - 调用者的行号(速度极慢,不推荐使�?)
 * m - 日志
 * M - 调用者的函数�?(速度极慢,不推荐使�?)
 * n - 换行符号
 * p - 日志优先级别(DEBUG, INFO, WARN, ERROR)
 * r - 输出日志�?用毫秒数
 * t - 调用者的进程�?
 * x - Used to output the NDC (nested diagnostic context) associated with the thread that generated the logging event.
 * X - Used to output the MDC (mapped diagnostic context) associated with the thread that generated the logging event.
 ******************************************************************************/
/***************************************************************************************************************************************************************
 * 模式修饰�? - 对齐 - �?小长�? - �?大长�? - 说明 %20c �? 20 ~ %-20c �? 20 ~ %.30c ~ ~ 30 %20.30c �? 20 30 %-20.30c �? 20 30
 **************************************************************************************************************************************************************/

public class Logger extends LoggerBase {

    public Logger() {
        super(Logger.class);
    }

    public Logger(String cls) {
        super(cls);
    }

    @SuppressWarnings("unchecked")
    public Logger(Class cls) {
        super(cls);
    }


}
