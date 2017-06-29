package com.remote.diagnosis.service.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heliqing on 2017/3/21.
 */
public class Page<T> implements Serializable {
    private Integer pageSize;
    private Integer pageNo;
    private Integer totalCount;
    private List<T> data;

    public Page() {
    }

    public Page(Integer pageSize, Integer pageNo, Integer totalCount, List<T> data) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.totalCount = totalCount;
        this.data = data;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String toString() {
        return "Page{pageSize=" + this.pageSize + ", pageNo=" + this.pageNo + ", totalCount=" + this.totalCount + ", data=" + this.data + '}';
    }
}