package com.remote.diagnosis.dao.impl;


import com.remote.diagnosis.dao.ISuperDAO;
import com.wangyin.customercare.facade.result.Page;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
public class SuperDAO extends SqlSessionDaoSupport implements ISuperDAO {

    public Integer update(String statementName, Object parameterObject) {
        return this.getSqlSession().update(statementName, parameterObject);
    }


    public Integer delete(String statementName, Object parameterObject) {
        return this.getSqlSession().delete(statementName, parameterObject);
    }


    public int insert(String statementName, Object parameterObject) {
        return this.getSqlSession().insert(statementName, parameterObject);
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String statementName, Object parameterObject) throws DataAccessException {
        return this.getSqlSession().selectList(statementName, parameterObject);
    }


    @SuppressWarnings("unchecked")
    public <T, V> Map<T, V> getMap(String statementName, Object parameterObject, String key) {
        return this.getSqlSession().selectMap(statementName, parameterObject, key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String statementName, Object parameterObject) {
        return (T) this.getSqlSession().selectOne(statementName, parameterObject);
    }

	@Override
    @SuppressWarnings("unchecked")
	public Page queryPagination(String statementName, Map<String, Object> param) {
		// 查询数据总数
		Integer totalCount = (Integer) this.getSqlSession().selectOne(statementName + "-count", param);

		Integer pageSize = 20;
		if (param.get("pageSize") != null) {

			pageSize = (Integer) param.get("pageSize");
		}

		Integer pageNo = 0;
		if (param.get("pageNo") != null) {
			pageNo = (Integer) param.get("pageNo");
		}

		if (pageSize < 1) {
			// 小于1时直接查询全部记录
			param.put("start", 0);
			param.put("offset", totalCount);
		} else {
			param.put("offset", pageSize);
			param.put("start", pageSize * (pageNo));
		}

        ArrayList data = null;
		List resultList = this.getSqlSession().selectList(statementName, param);
		if (resultList != null) {
			data = new ArrayList();
			data.addAll(resultList);
		}

		return new Page(pageSize, pageNo, totalCount, data);
	}
}
