package com.remote.diagnosis.dao.com.diagnosis.commons.log.access;

import com.remote.diagnosis.dao.com.diagnosis.commons.util.Formatter;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.StringUtil;

import java.util.LinkedList;

/**
 * AccessLog参数对象,包装响应�?,功能�?,请求流水,响应流水,业务数据1，业务数�?2...等数�?
 *
 * @author wyshenjianlin <a
 *         href="mailto:wyshenjianlin@chinabank.com.cn">wyshenjianlin@chinabank.com.cn</a> <br>
 *         QQ: 79043549
 * @version 1.0 2014-4-8
 */
public class AccessLogParameters {
    static final String NULL = "<null>";
    /**
     * 成功响应�?
     */
    static final String SUCCESS_RESPONSE_CODE = "00000";
    /**
     * 分隔�?
     */
    private static final String DELIMITER = ";";
    /**
     * 返回�?,默认成功
     */
    private String responseCode = SUCCESS_RESPONSE_CODE;
    /**
     * 响应描述
     */
    private String responseDesc = NULL;
    /**
     * 功能�?
     */
    private String funCode = NULL;
    /**
     * 请求流水
     */
    private String requestTrace = NULL;
    /**
     * 响应流水
     */
    private String responseTrace = NULL;
    /**
     * 业务元素列表，简单起见默认扫描顺�?
     */
    private LinkedList<Object> businessElements;
    /**
     * 调用�?始时间，默认创建时间
     */
    private long invokesStartTime = System.nanoTime();
    /**
     * 调用结束时间，默�?-1
     */
    private long invokesEndTime = -1L;

    public long getInvokesEndTime() {
        return invokesEndTime;
    }

    public void setInvokesEndTime(long invokesEndTime) {
        this.invokesEndTime = invokesEndTime;
    }

    public long getInvokesStartTime() {
        return invokesStartTime;
    }

    public void setInvokesStartTime(long invokesStartTime) {
        this.invokesStartTime = invokesStartTime;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        if (StringUtil.isNotEmpty(responseCode)) {
            this.responseCode = responseCode;
        }
    }

    public void setResponseCode(Object responseCode) {
        if (responseCode != null) {
            this.setResponseCode(responseCode.toString());
        }
    }

    public String getRequestTrace() {
        return requestTrace;
    }

    public void setRequestTrace(String requestTrace) {
        if (StringUtil.isNotEmpty(requestTrace)) {
            this.requestTrace = requestTrace;
        }
    }

    public void setRequestTrace(Object requestTrace) {
        if (requestTrace != null) {
            this.setRequestTrace(requestTrace.toString());
        }
    }

    public String getResponseTrace() {
        return responseTrace;
    }

    public void setResponseTrace(String responseTrace) {
        if (StringUtil.isNotEmpty(responseTrace)) {
            this.responseTrace = responseTrace;
        }
    }

    public void setResponseTrace(Object responseTrace) {
        if (responseTrace != null) {
            this.setResponseTrace(responseTrace.toString());
        }
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

    public void setResponseDesc(Object responseDesc) {
        if (responseDesc != null) {
            setResponseDesc(responseDesc.toString());
        }
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public String getFunCode() {
        return funCode;
    }

    public void setFunCode(String funCode) {
        this.funCode = funCode;
    }

    public LinkedList<Object> getBusinessElements() {
        return businessElements;
    }

    public void addBusinessElement(Object value) {
        if (businessElements == null) {
            businessElements = new LinkedList<Object>();
        }
        if (value != null) {
            businessElements.addLast(value);
        } else {
            businessElements.addLast(NULL);
        }
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(128);
        //格式：请求流�?,功能�?,响应�?,响应流水,业务数据1,业务数据2...,耗时(ns)
        buf.append(requestTrace).append(DELIMITER);
        buf.append(funCode).append(DELIMITER);
        buf.append(responseCode).append(DELIMITER);
        buf.append(responseDesc).append(DELIMITER);
        buf.append(responseTrace).append(DELIMITER);
        if (businessElements != null) {
            for (Object businessElement : businessElements) {
                buf.append(businessElement).append(DELIMITER);
            }
        }
        if (invokesEndTime == -1L) {
            invokesEndTime = System.nanoTime();
        }
        buf.append(Formatter.formatNS(invokesEndTime - invokesStartTime)); //.append("ns");
        return buf.toString();
    }
}

