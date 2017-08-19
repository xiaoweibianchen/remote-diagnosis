package com.remote.diagnosis.dao.com.diagnosis.commons.joran;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA for myUmp
 * User: taige
 * Date: 13-6-3
 * Time: 下午5:36
 */
public abstract class XMLConfigBase {

    private Map<String, String> attributes = new LinkedHashMap<String, String>();
    private Map<String, String> map = new LinkedHashMap<String, String>();

    protected String tagName;
    protected String body;

    protected XMLConfigBase topNode;
    protected XMLConfigBase parent;

    protected Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * used by SyncConfigurator
     * @param map
     */
    public void setAttributes(Map<String,String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            this.map.put(e.getKey().toUpperCase(), e.getValue());
            this.attributes.put(e.getKey(), e.getValue());
        }
    }

    public String getAttribute(String key, String defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        String v = map.get(key.toUpperCase());

        if (v == null) {
            return defaultValue;
        }

        return v;
    }

    public int getAttribute(String key, int defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        String v = map.get(key.toUpperCase());

        if (v == null) {
            return defaultValue;
        }

        return Integer.parseInt(v);
    }

    public boolean getAttribute(String key, boolean defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        String v = map.get(key.toUpperCase());

        if (v == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(v);
    }

    public XMLConfigBase getParent() {
        return parent;
    }

    public void setParent(XMLConfigBase parent) {
        this.parent = parent;
    }

    public XMLConfigBase getTopNode() {
        return topNode;
    }

    public void setTopNode(XMLConfigBase topNode) {
        this.topNode = topNode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public abstract void toXml(StringBuilder sb, String prefix);

    protected void _toXmlWithNoEndTag(StringBuilder sb, String prefix) {
        sb.append(prefix).append("<").append(tagName);
        for (Map.Entry<String,String> entry: attributes.entrySet()) {
            sb.append(prefix).append("\n").append("    ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        }
        sb.append(">");
        if (body != null) {
            sb.append(prefix).append(body);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toXml(sb, "");
        return sb.toString();
    }
}
