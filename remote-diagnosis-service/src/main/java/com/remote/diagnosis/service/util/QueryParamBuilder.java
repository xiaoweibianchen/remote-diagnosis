package com.remote.diagnosis.service.util;

import com.remote.diagnosis.service.request.PageRequestBase;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heliqing on 2017/3/22.
 */
@Component("queyParamBuilder")
public class QueryParamBuilder <S extends PageRequestBase>  implements DomainBuilder<Map<String,Object>,S> {
    @Override
    public Map<String, Object> builde(S source) {
       if(source ==null){
           return new HashMap<String, Object>(0);
       }
        Map<String,Object> param=buildInternal(source);
        param.put("start", source.getStart());
        param.put("offset", source.getEnd());
        param.put("pageNo",source.getPageNo());
        param.put("pageSize",source.getPageSize());
        return param;
    }

    private  Map<String,Object> buildInternal(S source) {
        if (source == null) {
            return new HashMap<String, Object>(2);
        }
        Map<String,Object> map =new HashMap<String, Object>();
        Class clazz=source.getClass();
        Field[] fields=clazz.getDeclaredFields();
        for(Field f:fields){
            String fieldName=f.getName();
            String methodBegin=null;
            Class type=f.getType();
            f.setAccessible(true);
            if(type == Boolean.class || type == boolean.class){
                methodBegin="is";
            }else{
                methodBegin="get";
            }
            String methodName=methodBegin+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            try {
                Method m=clazz.getMethod(methodName);
                try {
                    Object mapvalue=m.invoke(source);
                    map.put(fieldName,mapvalue);
                } catch (IllegalAccessException e) {
                    continue;
                } catch (InvocationTargetException e) {
                    continue;
                }
            } catch (NoSuchMethodException e) {
                break;
            }
        }
        return map;
    }

}
