package com.remote.diagnosis.service.request;


/**
 * Created by heliqing on 2017/3/21.
 */
public class PageRequestBase extends RequestBase {
    private static final Integer DEFAULT_PAGE_SIZE=20;

    private static final Integer DEFAULT_PAGE_NO =0;

    private Integer pageSize;

    private Integer pageNo;

    private Integer offset;

    private Integer limit;

    public Integer getPageSize() {
        if(offset != null && limit != null) {
            return limit;
        }

        if(pageSize==null)
        {
            pageSize=DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if(offset != null && limit != null) {
            this.pageSize = limit;
            return;
        }

        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        if(offset != null && limit != null) {
            return offset/limit;
        }

        if(pageNo==null){
            pageNo=DEFAULT_PAGE_NO;
        }
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        if(offset != null && limit != null) {
            this.pageNo = offset/limit;
            return;
        }
        this.pageNo = pageNo;
    }

    public Integer getOffset(){
        return getPageNo()*getPageSize();
    }

    public Integer getStart(){
        return getOffset();
    }

    public Integer getEnd(){
        return (getPageNo()+1)*pageSize;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "PageRequestBase{" +
                "pageSize=" + pageSize +
                ", pageNo=" + pageNo +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
