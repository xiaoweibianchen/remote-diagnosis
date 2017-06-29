package com.remote.diagnosis.service.result;

import com.wangyin.customercare.facade.domain.FeedBackCaseDomain;
import com.wangyin.customercare.facade.result.ResultBase;

/**
 * Created by heliqing on 2017/3/21.
 */
public class TestPageResult extends ResultBase {
    Page<FeedBackCaseDomain> data;

    public Page<FeedBackCaseDomain> getData() {
        return data;
    }

    public void setData(Page<FeedBackCaseDomain> data) {
        this.data = data;
    }
}
