package com.remote.diagnosis.dao.com.diagnosis.commons.joran;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * 针对XML文件的顶层节点解析规则类
 * 在对XML文件进行解析时，将该类与顶层节点名对应添加到RuleStore
 * 从�?�文件加载时，可以对XML层层解析
 *
 * User: zhangyao
 * Date: 13-5-27
 * Time: 下午3:32
 */
public class TopNodeAction extends Action {
    private Object obj;

    private Class<?> superClass;

    private String propertyName;

    public TopNodeAction(Class<?> superClass, String propertyName) {
        this.superClass = superClass;
        this.propertyName = propertyName;
    }


    /**
     * 标签解析�?始时，调用该方法
     * 获取对应标签的解析类，将属�?�注入给该bean ，InterpretationContext
     *
     * @param ec
     * @param s
     * @param attributes
     * @throws ch.qos.logback.core.joran.spi.ActionException
     */
    @Override
    public void begin(InterpretationContext ec, String s, Attributes attributes) throws ActionException {
        String className = attributes.getValue(CLASS_ATTRIBUTE);
        try {
            if (!OptionHelper.isEmpty(className)) {
                obj = OptionHelper.instantiateByClassName(className, superClass, context);
            } else {
                Class clazz = ec.getDefaultNestedComponentRegistry().findDefaultComponentType(superClass, propertyName);
                if (clazz != null) {
                    obj = clazz.newInstance();
                } else {
                    obj = superClass.newInstance();
                }
            }

            //zhangyao 20130528 修改begin方法，将标签的属性也注入给bean
            PropertySetter setter = new PropertySetter(obj);
            setter.setContext(context);
            boolean setAttributes = setter.computeAggregationType("attributes") == AggregationType.AS_COMPLEX_PROPERTY;
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (int i = 0; i < attributes.getLength(); i++) {
                AggregationType aggregationType = setter.computeAggregationType(attributes.getQName(i));
                //根据标签找到对应的setget方法，并且get方法返回类型为基本类型，则将属�?�直接注入给bean
                if (aggregationType == AggregationType.AS_BASIC_PROPERTY) {
                    setter.setProperty(attributes.getQName(i), attributes.getValue(i));
                } else if (aggregationType == AggregationType.NOT_FOUND) {
                    //没有对应的setget方法，则查看是否存在addAtrribute()方法，如有将属�?�注入；
                    if (setAttributes) {
                        map.put(attributes.getQName(i), attributes.getValue(i));
                    }
                }
            }

            if (setAttributes && map.size() > 0) {
                //存在添加属�?�方法，且属性map大小大于0，则进行属�?�注�?
                setter.setComplexProperty("attributes", map);
            }
            //zhangyao 20130528 修改begin方法，将标签的属性也注入给bean

            ec.pushObject(obj);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    /**
     * 标签解析完成时，调用该方�?
     * 将该标签对应的实体对象从interpretationContext删除，同时保存到Context
     * 以便在所有标签解析完成后，可以�?�过Context拿到对应的实体bean
     *
     * @param interpretationContext
     * @param tagName
     * @throws ch.qos.logback.core.joran.spi.ActionException
     */
    @Override
    public void end(InterpretationContext interpretationContext, String tagName) throws ActionException {
        PropertySetter nestedBean = new PropertySetter(obj);
        nestedBean.setContext(context);

        // have the nested element 's tagName
        if (nestedBean.computeAggregationType("tagName") == AggregationType.AS_BASIC_PROPERTY) {
            nestedBean.setProperty("tagName", tagName);
        }

        interpretationContext.popObject();
        interpretationContext.getContext().putObject(propertyName, obj);
    }

}
