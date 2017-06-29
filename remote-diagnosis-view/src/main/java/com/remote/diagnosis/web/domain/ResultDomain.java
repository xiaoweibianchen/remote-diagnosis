package com.remote.diagnosis.web.domain;

/**
 * Created by heliqing on 2017/3/17.
 */
public class ResultDomain {
    String rsultMessage;
    Object data;

    public String getRsultMessage() {
        return rsultMessage;
    }

    public void setRsultMessage(String rsultMessage) {
        this.rsultMessage = rsultMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
