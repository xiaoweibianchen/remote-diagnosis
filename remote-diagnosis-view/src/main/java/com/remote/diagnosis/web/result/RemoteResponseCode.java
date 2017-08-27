package com.remote.diagnosis.web.result;

public enum RemoteResponseCode {

    SUCCESS("00000","成功"),
    FAIL("99999", "请求处理失败"),
    ;

    private String code;

    private String msg;

    RemoteResponseCode(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public static RemoteResponseCode getByCode(String code) {
    	RemoteResponseCode[] list = values();
        for (RemoteResponseCode status : list) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum code '" + code + "'. " + RemoteResponseCode.class);
    }
}
