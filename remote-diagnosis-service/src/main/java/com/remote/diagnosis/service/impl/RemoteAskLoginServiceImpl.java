package com.remote.diagnosis.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.remote.diagnosis.dao.dal.RemoteAskLoginDO;
import com.remote.diagnosis.dao.repositoryapi.RemoteAskLoginRepository;
import com.remote.diagnosis.service.api.RemoteAskLoginService;
/**
 * Created by liwei on 2017/4/23.
 */
@Component("remoteAskLoginService")
public class RemoteAskLoginServiceImpl implements RemoteAskLoginService {
@Resource
private RemoteAskLoginRepository remoteAskLoginRepository;
	@Override
	public List<RemoteAskLoginDO> queryLogin(String loginName) {
		
		return remoteAskLoginRepository.queryLogin(loginName);
	}

}
