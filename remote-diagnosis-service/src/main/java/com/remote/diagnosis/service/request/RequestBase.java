package com.remote.diagnosis.service.request;

import com.wangyin.commons.log.access.RequestTrace;
import com.wangyin.npp.common.dto.MessageRequest;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by heliqing on 2017/3/21.
 */
public class RequestBase  extends MessageRequest implements Serializable{

    @RequestTrace
    private String  customerCareRequestNo;
    /** 创建人 */
    private String created;
    /** 创建时间 */
    private Date createdDate;
    /** 修改人 */
    private String modified;
    /** 修改时间 */
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
