package com.remote.diagnosis.web.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonResult implements Serializable {

	private static final long serialVersionUID = 4838211653028989693L;

	// 是否成功
	private Boolean success = Boolean.TRUE;

	// 返回代码
	private String resultCode = RemoteResponseCode.SUCCESS.getCode();

	// 返回单个对象
	private Object data;

/*	// 返回List
	@SuppressWarnings("rawtypes")
	private List rows = new ArrayList();*/

	// 返回消息
	private String message = RemoteResponseCode.SUCCESS.getMessage();
	// 返回分页对象的时候总数据条数
/*	private long total = 0;*/

	public Boolean isSuccess() {
		return success;
	}

	public JsonResult() {
		this.resultCode = RemoteResponseCode.SUCCESS.getCode();
		this.success = Boolean.TRUE;
		this.message = RemoteResponseCode.SUCCESS.getMessage();
	}

	public JsonResult(Boolean success) {
		this.success = success;
		this.message = success ? RemoteResponseCode.SUCCESS.getMessage() : "";
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
		if (!resultCode.equals(RemoteResponseCode.SUCCESS.getCode())) {
			this.success = Boolean.FALSE;
			this.message = null != RemoteResponseCode.getByCode(resultCode) ? RemoteResponseCode.getByCode(resultCode).getMessage() : RemoteResponseCode.FAIL.getMessage();
		}
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

/*	@SuppressWarnings("rawtypes")
	public List getRows() {
		return rows;
	}

	@SuppressWarnings("rawtypes")
	public void setRows(List rows) {
		this.rows = rows;
	}*/

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

/*	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}*/
}