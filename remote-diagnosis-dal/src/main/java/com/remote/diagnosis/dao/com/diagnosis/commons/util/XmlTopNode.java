package com.remote.diagnosis.dao.com.diagnosis.commons.util;


import java.nio.charset.Charset;

public class XmlTopNode extends XmlNode {
    private static final long serialVersionUID = -9092007302678112928L;

    private String encoding;
    private String version;

    private String topString;
    
    public XmlTopNode(String tag) {
        this(tag, Charset.defaultCharset().name(), "1.0");
    }
    
    public XmlTopNode(String tag, String encoding) {
        this(tag, encoding, "1.0");
    }
    
    public XmlTopNode(String tag, String encoding, String version) {
        super(tag);
        this.encoding = encoding;
        this.version = version;
        this.topString = "<?xml version=\"" + version + "\" encoding=\"" + encoding + "\" ?>\n";
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public String getXMLVersion() {
        return version;
    }
    
    public void putAll(XmlNode node) {
        if (node instanceof XmlTopNode) {
            encoding = ((XmlTopNode) node).encoding;
            version = ((XmlTopNode) node).version;
        }
        super.putAll(node);
    }
    
    public String toString() {
        return topString +
            super.toString();
    }
    
    
}
