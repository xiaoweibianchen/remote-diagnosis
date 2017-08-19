package com.remote.diagnosis.service.result;

import com.remote.diagnosis.dao.impl.Page;

public class PageResultBase<T> extends ResultBase {
     Page<T> data;

    public Page<T> getData() {
        return data;
    }

    public void setData(Page<T> data) {
        this.data = data;
    }
}
