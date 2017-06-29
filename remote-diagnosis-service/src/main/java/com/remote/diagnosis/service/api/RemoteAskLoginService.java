package com.remote.diagnosis.service.api;

import java.util.List;

import com.remote.diagnosis.dao.dal.RemoteAskLoginDO;

public interface RemoteAskLoginService {
	 List<RemoteAskLoginDO> queryLogin(String loginName);
}
