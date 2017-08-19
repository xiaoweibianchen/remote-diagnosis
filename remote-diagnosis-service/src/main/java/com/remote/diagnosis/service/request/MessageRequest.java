/*
 * @(#)MessageRequest.java	1.0 2014-2-28
 *
 * Copyright 2009 chinabank payment All Rights Reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Author Email: wychenbinghong@chinabank.com.cn
 */
package com.remote.diagnosis.service.request;

import java.io.Serializable;
import java.util.Date;


public class MessageRequest implements Serializable {

    private String  requestModule;
    private Date requestTime;

    public MessageRequest() {

    }

    public MessageRequest(String module, Date date) {
    	this();
    	this.requestModule = module;
    	this.requestTime = date;
    }

    public MessageRequest(MessageRequest msgRequest) {
    	this(msgRequest.getRequestModule(), msgRequest.getRequestTime());
    }
	public String getRequestModule() {
		return requestModule;
	}

	public void setRequestModule(String requestModule) {
		this.requestModule = requestModule;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageRequest{");
        sb.append("requestModule='").append(requestModule).append('\'');
        sb.append(", requestTime=").append(requestTime);
        sb.append('}');
        return sb.toString();
    }
}
