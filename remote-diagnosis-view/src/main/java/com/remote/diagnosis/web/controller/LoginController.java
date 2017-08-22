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
/**
 * Created by liwei on 2017/8/22.
 */
@Controller
@RequestMapping("remote/login")
public class LoginController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(LoginController.class);
	@RequestMapping(value = "/login.htm",method = RequestMethod.GET)
    @ResponseBody
    public String queryUserInfo(HttpServletRequest request) {
        
        return "test";
    }
}
