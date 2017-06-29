package com.remote.diagnosis.dao.dal;

/**
 * Created by heliqing on 2017/3/22.
 */
public class FeedBackOrderDO extends BaseDO {
    String caseId;   //反馈单id
    String caseStatus;  //反馈单状态
    String caseType;   //反馈单类型（批量问题，单个客诉）
    String currentDeal; //当前处理人
    String assignType; //指派类型
    String assignedErp; //被指派人erp

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCurrentDeal() {
        return currentDeal;
    }

    public void setCurrentDeal(String currentDeal) {
        this.currentDeal = currentDeal;
    }

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public String getAssignedErp() {
        return assignedErp;
    }

    public void setAssignedErp(String assignedErp) {
        this.assignedErp = assignedErp;
    }

    @Override
    public String toString() {
        return "FeedBackOrderDO{" +
                "caseId='" + caseId + '\'' +
                ", caseStatus='" + caseStatus + '\'' +
                ", caseType='" + caseType + '\'' +
                ", currentDeal='" + currentDeal + '\'' +
                ", assignType='" + assignType + '\'' +
                ", assignedErp='" + assignedErp + '\'' +
                '}';
    }
}
