package com.remote.diagnosis.web.inteceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.remote.diagnosis.web.tool.StringUtils;

public class LoginInterceptor extends HandlerInterceptorAdapter {
	 private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
	 @Override
	 public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		 try{
	            Map<String, String> headParams = getHeadersInfo(request);
	            String key = headParams.get("personKey");//以ip、
	            if(StringUtils.isBlank(key)){
	                return false;
	            }
	            //验证缓存
		 }catch (Throwable e){
	            logger.info("用户登录数据异常e:{}, request:{}", e, request);
	            return false;
	        }
		 return true;
	 }
	 private Map<String, String> getHeadersInfo(HttpServletRequest request) {
	        Map<String, String> map = new HashMap<String, String>();
	        Enumeration headerNames = request.getHeaderNames();
	        while (headerNames.hasMoreElements()) {
	            String key = (String) headerNames.nextElement();
	            String value = request.getHeader(key);
	            map.put(key, value);
	        }
	        return map;
	    }
}
