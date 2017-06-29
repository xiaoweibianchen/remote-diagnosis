package com.remote.diagnosis.dao.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.remote.diagnosis.dao.dal.RemoteAskLoginDO;
import com.remote.diagnosis.dao.repositoryapi.RemoteAskLoginRepository;
/**
 * Created by liwei on 2017/4/23.
 */
@Component("remoteAskLoginRepository")
public class RemoteAskLoginRepositoryImpl extends BaseRepository implements RemoteAskLoginRepository{

	@Override
	public void addLogin(RemoteAskLoginDO login) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteLogin(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLogin(RemoteAskLoginDO login) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<RemoteAskLoginDO> queryLogin(String loginName) {
		// TODO Auto-generated method stub
		return this.getSuperDAO().getList("RemoteAskLoginMapper.selectByParams", loginName);
	}

}
