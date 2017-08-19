package com.remote.diagnosis.service.result;

import com.remote.diagnosis.service.request.RequestTrace;

import java.io.Serializable;

/**
 * Created by heliqing on 2017/3/21.
 */
public class ResultBase extends MessageResponse implements Serializable{
    @RequestTrace
    private String customerCareRequestNo;

    public String getMarketingRequestNo() {
        return customerCareRequestNo;
    }
    public ResultBase(String responseDesc){
        super.setResponseDesc(responseDesc);
    }
    public void setCustomerCareRequestNo(String customerCareRequestNo) {
        this.customerCareRequestNo = customerCareRequestNo;
    }
    public ResultBase(String code, String message) {
        this.setResponseModule("CTC");
        this.setResponseCode(code);
        this.setResponseDesc(message);
    }

    public ResultBase(){}
}
