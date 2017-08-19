package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

import java.util.LinkedHashMap;
import java.util.Map;


public class NestedComplexProAttrIA extends NestedComplexPropertyIA {


    public void begin(InterpretationContext ec, String localName,
                      Attributes attributes) {
        // LogLog.debug("in NestComponentIA begin method");
        // get the action data object pushed in isApplicable() method call
        IADataForComplexProperty actionData =  actionDataStack
                .peek();

        String className = attributes.getValue(CLASS_ATTRIBUTE);
        // perform variable name substitution
        className = ec.subst(className);

        Class<?> componentClass = null;
        try {
            if (!OptionHelper.isEmpty(className)) {
                componentClass = Loader.loadClass(className, context);
            } else {
                // guess class name via implicit rules
                PropertySetter parentBean = actionData.parentBean;
                componentClass = parentBean.getClassNameViaImplicitRules(actionData.getComplexPropertyName(), actionData.getAggregationType(), ec.getDefaultNestedComponentRegistry());
            }

            if (componentClass == null) {
                actionData.inError = true;
                String errMsg = "Could not find an appropriate class for property ["
                        + localName + "]";
                addError(errMsg);
                return;
            }
            Object obj = componentClass.newInstance();
            PropertySetter bean = new PropertySetter(obj);
            bean.setContext(context);
            boolean setAttributes = bean.computeAggregationType("attributes")==AggregationType.AS_COMPLEX_PROPERTY;
            Map<String,String> map = new LinkedHashMap<String, String>();
            for (int i = 0; i < attributes.getLength(); i++) {
                AggregationType aggregationType= bean.computeAggregationType(attributes.getQName(i));
                if (aggregationType == AggregationType.AS_BASIC_PROPERTY) {
                    bean.setProperty(attributes.getQName(i), attributes.getValue(i));
                } else if (aggregationType==AggregationType.NOT_FOUND) {
                     if (setAttributes) {
                        map.put(attributes.getQName(i),attributes.getValue(i));
                     }
                }
            }

            if (setAttributes && map.size() > 0) {
                bean.setComplexProperty("attributes",map);
            }

            if (OptionHelper.isEmpty(className)) {
                addInfo("Assuming default type [" + componentClass.getName()
                        + "] for [" + localName + "] property");
            }
            actionData.setNestedComplexProperty(obj);

            // pass along the repository
            if (actionData.getNestedComplexProperty() instanceof ContextAware) {
                ((ContextAware) actionData.getNestedComplexProperty())
                        .setContext(this.context);
            }
            ec.pushObject(actionData.getNestedComplexProperty());

        } catch (Exception oops) {
            oops.printStackTrace();
            actionData.inError = true;
            String msg = "Could not create component [" + localName + "] of type ["
                    + className + "]";
            addError(msg, oops);
        }

    }

    @Override
    public void body(InterpretationContext ec, String body) throws ActionException {
        String finalBody = ec.subst(body);
        if (!OptionHelper.isEmpty(finalBody)) {
            //濡備綍body闈炵┖锛屽垯璋冪敤bean鐨剆etBody鏂规硶锛屽皢body鍐呭娉ㄥ叆bean銆�
            IADataForComplexProperty actionData =  actionDataStack.peek();
            if (actionData.inError) {
                return;
            }

            PropertySetter nestedBean = new PropertySetter(actionData.getNestedComplexProperty());
            nestedBean.setContext(context);

            // have the nested element point to its parent if possible
            if (nestedBean.computeAggregationType("body") == AggregationType.AS_BASIC_PROPERTY) {
                nestedBean.setComplexProperty("body", body);
            }
        }
    }

    /**
     * 鍦ㄥ嚭鐜拌В鏋愰敊璇椂锛屾姏鍑哄紓甯�
     * @param ec
     * @param tagName
     */
    @Override
    public void end(InterpretationContext ec, String tagName) {
        IADataForComplexProperty actionData = actionDataStack.peek();

        if (actionData.inError) {
            //閿欒鏃讹紝鎶涘嚭寮傚父
            //鍦ㄨВ鏋愬畬鎴愬悗鍐嶆姏鍑哄紓甯�
//            ErrorStatus errorStatus = null;
//            for (Status status : context.getStatusManager().getCopyOfStatusList()) {
//                if (status instanceof ErrorStatus) {
//                    errorStatus = (ErrorStatus) status;
//                }
//            }
//            if (errorStatus != null) {
//                throw new RuntimeException(errorStatus.getMessage(), errorStatus.getThrowable());
//            }
            return;
        }
        PropertySetter nestedBean = new PropertySetter(actionData.getNestedComplexProperty());
        nestedBean.setContext(context);

        // have the nested element 's tagName
        if (nestedBean.computeAggregationType("tagName") == AggregationType.AS_BASIC_PROPERTY) {
            nestedBean.setProperty("tagName", tagName);
        }

        Object obj = ec.getObject(0);
        if (obj != null) {
            // have the nested element point to its top if possible
            if (nestedBean.computeAggregationType("topNode") == AggregationType.AS_COMPLEX_PROPERTY) {
                nestedBean.setComplexProperty("topNode", obj);
            }
        }

        super.end(ec, tagName);
    }
}
