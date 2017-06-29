package com.remote.diagnosis.service.result;

import com.remote.diagnosis.dao.dal.FeedBackOrderDO;


/**
 * Created by heliqing on 2017/3/22.
 */
public class FeedBackOrderResult extends ResultBase{
    FeedBackOrderDO data;

    public FeedBackOrderDO getData() {
        return data;
    }

    public void setData(FeedBackOrderDO data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FeedBackOrderResult{" +
                "data=" + data +
                '}';
    }
}
