package com.remote.diagnosis.service.result;

import com.wangyin.customercare.facade.result.Page;
import com.wangyin.customercare.facade.result.ResultBase;

/**
 * Created by heliqing on 2017/3/21.
 */
public class PageResultBase<T> extends ResultBase {
     Page<T> data;

    public Page<T> getData() {
        return data;
    }

    public void setData(Page<T> data) {
        this.data = data;
    }
}
