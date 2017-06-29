package com.remote.diagnosis.dao.repositoryapi;

import java.util.List;

import com.remote.diagnosis.dao.dal.RemoteAskLoginDO;

public interface RemoteAskLoginRepository {
public void addLogin(RemoteAskLoginDO login);
public void deleteLogin(String id);
public void updateLogin(RemoteAskLoginDO login);
public List<RemoteAskLoginDO> queryLogin(String loginName);
}
