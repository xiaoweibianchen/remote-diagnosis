package com.remote.diagnosis.web.request;



import java.util.List;

/**
 * Created by liwei on 2017/8/11.
 */
public class UserRequest extends BaseRequest {
    private String userName;
    private String passWord;


    public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}


	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}


	@Override
    public String toString() {
        return "{\"UserRequest\":"
                + super.toString()
                + ",\"userName\":\"" + userName + "\""
                + ",\"password\":\"" + passWord + "\""
                + "}";
    }
}
