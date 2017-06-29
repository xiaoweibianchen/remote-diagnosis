package com.remote.diagnosis.dao.repositoryapi;

import com.remote.diagnosis.dao.dal.FeedBackOrderDO;

/**
 * Created by heliqing on 2017/3/22.
 */
public interface FeedBackOrderRepository {
    public int createOrder(FeedBackOrderDO feedBackOrderDO);
}
