package com.remote.diagnosis.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.remote.diagnosis.web.request.UserRequest;
import com.remote.diagnosis.web.result.JsonResult;
import com.remote.diagnosis.web.result.RemoteResponseCode;
import com.remote.diagnosis.web.tool.StringUtils;
/**
 * Created by liwei on 2017/8/22.
 */
@Controller
@RequestMapping("remote/login")
public class LoginController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(LoginController.class);
	@RequestMapping(value = "/login.htm",method = RequestMethod.POST)
    @ResponseBody
    public String queryUserInfo(HttpServletRequest request,@RequestBody UserRequest userRequest) {
		JsonResult jsonResult = new JsonResult(true);
		if(StringUtils.isBlank(userRequest.getUserName()) || StringUtils.isBlank(userRequest.getPassWord())){
        	logger.info("用户名或密码为空!userRequest:{}",userRequest);
        }
		jsonResult.setResultCode(RemoteResponseCode.SUCCESS.getCode());
		jsonResult.setData("test");
		
		return JSON.toJSONString(jsonResult);
    }
}
