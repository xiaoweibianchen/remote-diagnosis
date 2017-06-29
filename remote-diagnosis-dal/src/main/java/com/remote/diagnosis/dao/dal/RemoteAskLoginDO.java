package com.remote.diagnosis.dao.dal;

public class RemoteAskLoginDO extends BaseDO {
private String id;
//登录名
private String loginName;
//医疗机构ID
private String institutionId;
//医师ID
private String physictianId;
//数据状态
private int state;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getLoginName() {
	return loginName;
}
public void setLoginName(String loginName) {
	this.loginName = loginName;
}
public String getInstitutionId() {
	return institutionId;
}
public void setInstitutionId(String institutionId) {
	this.institutionId = institutionId;
}
public String getPhysictianId() {
	return physictianId;
}
public void setPhysictianId(String physictianId) {
	this.physictianId = physictianId;
}
public int getState() {
	return state;
}
public void setState(int state) {
	this.state = state;
}
}
