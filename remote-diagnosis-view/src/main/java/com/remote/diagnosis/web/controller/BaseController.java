package com.remote.diagnosis.web.controller;

import com.remote.diagnosis.dao.impl.Page;
import com.remote.diagnosis.service.result.ResultBase;
import com.remote.diagnosis.web.tool.JsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    private static final Logger logger= LoggerFactory.getLogger(BaseController.class);

    public String processData(ResultBase data,Page page){
        if(page!=null){
            return this.toPageJson(page);
        }else{
            return  this.toJson(data);
        }
    }

    private String toJson(ResultBase data){
        logger.info("成功返回");

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("resultCode","00000");
        map.put("msg","操作成功");
        map.put("data",data);
        return JsonUtil.toJson(map);
    }
    private String toPageJson(Page data){
        logger.info("成功返回");
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("resultCode", "00000");
        map.put("msg","操作成功");
        if(data!=null) {
            map.put("data", data.getData());
            map.put("total",data.getTotalCount());
            map.put("pageSize",data.getPageSize());
            map.put("current",data.getPageNo());
        }else{
            map.put("data", null);
            map.put("total",0);
            map.put("pageSize",20);  //暂定
            map.put("current",1);
        }
        return JsonUtil.toJson(map);
    }

    /*
    * */
    @ExceptionHandler(value=Exception.class)   //可以指定特殊的异常
    protected @ResponseBody String exception(HttpServletRequest request, HttpServletResponse response,Exception e){
        logger.info("请求出现异常，e:{}", e.getMessage());
//        response.setContentType("application/json;charset=UTF-8");
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("resultCode","99999");
        map.put("msg",e.getMessage());
        return JsonUtil.toJson(map);
    }

    //todo 根据返回如果不为成功，统一出错抛出异常
    //作为抛出来
    protected String noSucessProcess(HttpServletRequest request, HttpServletResponse response,ResultBase data) throws Exception{
        throw new Exception(data.getResponseDesc());
    }
    
}
