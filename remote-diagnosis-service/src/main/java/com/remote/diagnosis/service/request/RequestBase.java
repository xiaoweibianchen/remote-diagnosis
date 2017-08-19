package com.remote.diagnosis.service.request;

import java.io.Serializable;
import java.util.Date;
public class RequestBase  extends MessageRequest implements Serializable{

    @RequestTrace
    private String  customerCareRequestNo;
    /** 鍒涘缓浜� */
    private String created;
    /** 鍒涘缓鏃堕棿 */
    private Date createdDate;
    /** 淇敼浜� */
    private String modified;
    /** 淇敼鏃堕棿 */
    private Date modifiedDate;

    public String getCustomerCareRequestNo() {
        return customerCareRequestNo;
    }

    public void setCustomerCareRequestNo(String customerCareRequestNo) {
        this.customerCareRequestNo = customerCareRequestNo;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
