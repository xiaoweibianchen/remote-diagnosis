package com.remote.diagnosis.web.request;

import java.util.Date;

/**
 * <b>author: liwei</b></br>
 * <p>创建日期：2017/6/30 </p>
 * <b>version: 0.01</b></br>
 */
public class BaseRequest {

    private Long id;

    private String medicalInstitutionId;

    private String userId;


    /**
     * 开始时间
     */
    private Date begin;

    /**
     * 结束时间
     */
    private Date end;

    /**
     * 页面大小
     */
    private int pageSize;

    /**
     * 当前分页页码
     */
    private int pageNum;

    private String created;
    private Date createdDate;
    private String modified;
    private Date modifiedDate;

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

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicalInstitutionId() {
		return medicalInstitutionId;
	}

	public void setMedicalInstitutionId(String medicalInstitutionId) {
		this.medicalInstitutionId = medicalInstitutionId;
	}

	@Override
    public String toString() {
        return "{\"BaseRequest\":{"
                + "\"id\":\"" + id + "\""
                  + "\"medicalInstitutionId\":\"" + medicalInstitutionId + "\""                
                + ",\"userId\":\"" + userId + "\""
                + ",\"begin\":" + begin
                + ",\"end\":" + end
                + ",\"pageSize\":\"" + pageSize + "\""
                + ",\"pageNum\":\"" + pageNum + "\""
                + ",\"created\":\"" + created + "\""
                + ",\"createdDate\":" + createdDate
                + ",\"modified\":\"" + modified + "\""
                + ",\"modifiedDate\":" + modifiedDate
                + "}}";
    }
}
