package com.remote.diagnosis.web.domain;

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
