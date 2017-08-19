package com.remote.diagnosis.service.domain;


public class FeedBackCaseDomain  extends BaseDomain {
    String caseId;
    String caseStatus;
    String caseType;
    String currentDeal;
    String assignType;
    String assignedErp;


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
        return "FeedBackCaseDomain{" +
                "caseId='" + caseId + '\'' +
                ", caseStatus='" + caseStatus + '\'' +
                ", caseType='" + caseType + '\'' +
                ", currentDeal='" + currentDeal + '\'' +
                ", assignType='" + assignType + '\'' +
                ", assignedErp='" + assignedErp + '\'' +
                '}';
    }
}
