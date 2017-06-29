package com.remote.diagnosis.dao.repository;


import com.remote.diagnosis.dao.ISuperDAO;

import org.springframework.beans.factory.annotation.Autowired;


public class BaseRepository {

	private ISuperDAO superDAO;

	public ISuperDAO getSuperDAO() {
		return superDAO;
	}

	@Autowired
	public void setSuperDAO(ISuperDAO superDAO) {
		this.superDAO = superDAO;
	}
}
