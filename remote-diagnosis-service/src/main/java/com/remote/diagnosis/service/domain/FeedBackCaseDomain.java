package com.remote.diagnosis.service.domain;

import com.wangyin.customercare.facade.domain.BaseDomain;

/**
 * Created by heliqing on 2017/3/21.
 */
public class FeedBackCaseDomain  extends BaseDomain {
    String caseId;   //反馈单id
    String caseStatus;  //反馈单状态
    String caseType;   //反馈单类型（批量问题，单个客诉）
//    String caseBizType;  // 反馈单业务类型
//    String caseSubject;  //反馈单主题
//    String priority;    //优先级
//    String belongType; //初步定位
//    String description; //描述
//    String claimDate; //认领时间
//    String dealDate; //处理时间。
    String currentDeal; //当前处理人
//    String lastBelongType; //最终问题定位。
//    String extentionInfo; //包括jdpin，outtrad_no等信息
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
