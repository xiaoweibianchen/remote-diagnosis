package com.remote.diagnosis.dao.com.diagnosis.commons.joran;


import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.GenericConfigurator;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ImplicitAction;
import ch.qos.logback.core.joran.action.NestedComplexProAttrIA;

import com.remote.diagnosis.dao.ch.qos.logback.core.joran.action.NestedBasicPropertyWithDefValIA;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.

 * XML配置文件加载�?
 * 根据顶层节点名及解析基类，添加了顶层节点的解析规�?

 * User: zhangyao
 * Date: 13-5-27
 * Time: 上午11:26
 */
public class XMLConfigurator extends GenericConfigurator {
    private Map<ElementSelector, Action> ruleMap;
    private List<ImplicitAction> iaList;

    private String topNodeName;

    /**
     * topNodeName顶层标签�?  topNodeSuperClass顶层节点载体类的基类
     * 为顶层节点添加解析规�?
     *
     * @param topNodeName
     * @param topNodeSuperClass
     */
    public XMLConfigurator(String topNodeName, Class<?> topNodeSuperClass) {
        this.topNodeName = topNodeName;
        ruleMap = new HashMap<ElementSelector, Action>();
        ruleMap.put(new ElementSelector(topNodeName), new TopNodeAction(topNodeSuperClass, topNodeName));
    }

    /**
     * add pattern and actions
     * @param tagPattern
     * @param action
     */
    public void addRule(String tagPattern, Action action) {
        if (ruleMap == null) {
            ruleMap = new HashMap<ElementSelector, Action>();
        }
        ruleMap.put(new ElementSelector(tagPattern), action);
    }

    /**
     * add implicit rule
     * @param action
     */
    public void addImplicitRule(ImplicitAction action) {
        if (iaList == null) {
            iaList = new ArrayList<ImplicitAction>();
        }
        iaList.add(action);
    }

    /**
     *
     * @param is
     * @param <T>
     * @return
     * @throws ch.qos.logback.core.joran.spi.JoranException
     */
    public <T> T parseConfiguration(InputStream is) throws JoranException {
        if (getContext() == null) {
            setContext(new ContextBase());
        }
        doConfigure(is);

        //Error check
        StatusManager sm = context.getStatusManager();
        if (sm != null) {
            StatusUtil statusUtil = new StatusUtil(context);
            if (statusUtil.getHighestLevel(0) == ErrorStatus.ERROR) {
                StatusPrinter.print(sm);
                errorCheck(sm);
            }
        }

        try {
            return (T) context.getObject(topNodeName);
        } catch (ClassCastException e) {
            throw new JoranException(e.getMessage(), e);
        }
    }

    private void errorCheck(StatusManager sm) throws JoranException {
        ErrorStatus errorStatus = null;
        for (Status status : sm.getCopyOfStatusList()) {
            if (status instanceof ErrorStatus) {
                errorStatus = (ErrorStatus) status;
            }
        }
        if (errorStatus != null) {
            throw new JoranException(errorStatus.getMessage(), errorStatus.getThrowable());
        }
    }

    /**
     * 加载配置文件，并返回顶层节点的载体类
     *
     * @param configFileName
     * @return
     * @throws ch.qos.logback.core.joran.spi.JoranException
     *
     */
    public <T> T parseConfiguration(String configFileName) throws JoranException {
        return parseConfiguration(this.getClass().getClassLoader().getResourceAsStream(configFileName));
    }

    /**
     * 标签解析规则添加
     *
     * @param rs
     */
    @Override
    protected void addInstanceRules(RuleStore rs) {
        for (ElementSelector elementSelector : ruleMap.keySet()) {
            Action action = ruleMap.get(elementSelector);
            rs.addRule(elementSelector, action);
        }
    }

    /**
     * 添加标签默认的解析规�?
     *
     * @param interpreter
     */
    @Override
    protected void addImplicitRules(Interpreter interpreter) {
        NestedComplexProAttrIA nestedComplexPropertyIA = new NestedComplexProAttrIA();
        nestedComplexPropertyIA.setContext(context);
        interpreter.addImplicitAction(nestedComplexPropertyIA);

        NestedBasicPropertyWithDefValIA nestedBasicIA = new NestedBasicPropertyWithDefValIA();
        nestedBasicIA.setContext(context);
        interpreter.addImplicitAction(nestedBasicIA);

        if (iaList == null) {
            return;
        }
        for (ImplicitAction ia : iaList) {
            interpreter.addImplicitAction(ia);
        }
    }

}
