package com.remote.diagnosis.dao.ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;


public class NestedBasicPropertyWithDefValIA extends NestedBasicPropertyIA {
    public final static String DEFAULT_VALUE = "true";

    private boolean bodySetted;

    @Override
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        super.begin(ec, localName, attributes);
        bodySetted = false;
    }

    @Override
    public void body(InterpretationContext ec, String body) {
        super.body(ec, body);
        bodySetted = true;
    }

    @Override
    public void end(InterpretationContext ec, String tagName) {
        if (! bodySetted) {
            body(ec, DEFAULT_VALUE);
        }
        super.end(ec, tagName);
    }
}
