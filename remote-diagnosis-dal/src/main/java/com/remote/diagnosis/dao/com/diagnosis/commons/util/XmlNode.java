package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.*;


public class XmlNode implements Serializable {
    private static final Logger LOGGER = new Logger();
    private static final long serialVersionUID = 8771566483029080145L;

    private String nodeTag;
    private String content;
    
    private Map<String, String> attrs;
    private Map<String, Serializable> childs;
    
    public XmlNode(String tag) {
        this.nodeTag = tag;
    }
    
    public XmlNode(String tag, String content) {
        this.nodeTag = tag;
        this.content = content;
    }
    
    public void putAll(XmlNode node) {
        nodeTag = node.nodeTag;
        content = node.content;
        if (attrs == null && node.attrs != null) {
            attrs = new LinkedHashMap<String, String>();
        }
        if (node.attrs != null) {
            attrs.putAll(node.attrs);
        }
        if (childs == null && node.childs != null) {
            childs = new LinkedHashMap<String, Serializable>();
        }
        if (node.childs != null) {
            childs.putAll(node.childs);
        }
    }
    
    public String getTag() {
        return nodeTag;
    }
    
    public void reset() {
        content = null;
        if (attrs != null) {
            attrs.clear();
            attrs = null;
        }
        if (childs != null) {
            childs.clear();
            childs = null;
        }
    }
    
    public String getString() {
        return content;
    }
    
    public void setString(String str) {
        content = str;
    }
    
    public void appendString(String str) {
        if (content == null) {
            content = str;
        } else {
            content += str;
        }
    }

    //modify by shenjl at 2013-10-31 clone
    public Map<String, String> getAttributesMap() {
        return getAttributes();
    }
    
    public boolean hasAttribute(String name) {
        return getAttribute(name) != null;
    }

    //modify by shenjl at 2013-10-31 clone
    public String getAttribute(String name) {
        return getAttribute(name, null);
    }
    
    public String getAttribute(String name, String dflt) {
        if (attrs != null) {
            String attr = (String) attrs.get(name);
            if (attr == null) {
                return dflt;
            }
            return attr;
        }
        return dflt;
    }
    
    public void setAttribute(String name, String value) {
        if (attrs == null) {
            attrs = new LinkedHashMap<String, String>();
        }
        attrs.put(name, value);
    }
    
    public String removeAttribute(String name) {
        if (attrs != null) {
            return attrs.remove(name);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public int getChildNodeLength(String tag) {
        if (childs != null) {
            Object obj = childs.get(tag);
            if (obj == null) {
                return 0;
            } else if (obj instanceof ArrayList) {
                return ((ArrayList<XmlNode>) obj).size();
            } else if (obj instanceof XmlNode){
                return 1;
            }
        }
        return 0;
    }
    
    public boolean hasChildNode(String tag) {
        return getChildNode(tag) != null;
    }
    
    public XmlNode getChildNode(String tag) {
        return getChildNode(tag, 0);
    }
    
    @SuppressWarnings("unchecked")
    public XmlNode getChildNode(String tag, int idx) {
        if (childs != null) {
            Object obj = childs.get(tag);
            if (obj == null) {
                return null;
            } else if (obj instanceof ArrayList) {
                return (XmlNode) ((ArrayList<XmlNode>) obj).get(idx);
            } else if (obj instanceof XmlNode){
                if (idx == 0) {
                    return (XmlNode) obj;
                }
            }
        }
        return null;
    }
    
    public void setChildNode(XmlNode child) {
        setChildNode(0, child);
    }
    
    @SuppressWarnings("unchecked")
    public void setChildNode(int idx, XmlNode child) {
        if (childs == null) {
            childs = new LinkedHashMap<String, Serializable>();
        }
        Object obj = childs.get(child.getTag());
        if (obj == null) {
            childs.put(child.getTag(), child);
        } else if (obj instanceof ArrayList) {
            ((ArrayList<XmlNode>) obj).set(idx, child);
        } else {
            if (idx != 0) {
                throw new IndexOutOfBoundsException("index=" + idx + " must < size=1");
            }
            childs.put(child.getTag(), child);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void addChildNode(XmlNode child) {
        if (childs == null) {
            childs = new LinkedHashMap<String, Serializable>();
        }
        Object obj = childs.get(child.getTag());
        if (obj == null) {
            childs.put(child.getTag(), child);
        } else if (obj instanceof ArrayList) {
            ((ArrayList<XmlNode>) obj).add(child);
        } else {
            ArrayList<XmlNode> list = new ArrayList<XmlNode>();
            list.add((XmlNode) obj);
            list.add(child);
            childs.put(child.getTag(), list);
        }
    }
    
    public Object removeChildNode(String tag) {
        if (childs != null) {
            return childs.remove(tag);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public String toString(String indent) {
        StringBuffer sb = new StringBuffer();
        //add 2008.11.07 增加html编码处理
        String nodeTag = this.nodeTag;
//        nodeTag = ToolBox.replace(nodeTag, "&", "&amp;");
//        nodeTag = ToolBox.replace(nodeTag, "<", "&lt;");
//        nodeTag = ToolBox.replace(nodeTag, ">", "&gt;");   
        sb.append(indent).append('<').append(nodeTag);
        if (attrs != null) {
            Iterator<Map.Entry<String, String>> it = attrs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                sb.append(' ').append(entry.getKey()).append("=\"").append(entry.getValue()).append('"');
            }
        }
        if (childs == null && content == null) {
            sb.append("/>\n");
            return sb.toString();
        }
        sb.append('>');
        if (childs != null) {
            sb.append('\n');
            Iterator<Map.Entry<String, Serializable>> it = childs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Serializable> entry = (Map.Entry<String, Serializable>) it.next();
                Object obj = entry.getValue();
                if (obj instanceof ArrayList) {
                    ArrayList<XmlNode> list = (ArrayList<XmlNode>) obj;
                    for (int i = 0; i < list.size(); i++) {
                        sb.append(((XmlNode)list.get(i)).toString(indent + "  "));
                    }
                } else {
                    sb.append(((XmlNode)obj).toString(indent + "  "));
                }
            }
        }
        if (content != null) {
            //add 2008.11.07 增加html编码处理
            String content = this.content;
            content = StringUtil.replace(content, "&", "&amp;");
            content = StringUtil.replace(content, "<", "&lt;");
            content = StringUtil.replace(content, ">", "&gt;");
            sb.append(content);
        }
        if (sb.charAt(sb.length() - 1) == '\n') {
            sb.append(indent);
        }
        sb.append("</").append(nodeTag).append('>');
        if (indent.length() > 0) {
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof XmlNode) {
            return this.toString().equals(obj.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = nodeTag != null ? nodeTag.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    public String toString() {
        return toString("");
    }

    /**
     * Return attrs.
     * @return attrs
     * @author shenjianlin clone
     */
    public Map<String, String> getAttributes() {
        return ObjectUtils.clone(attrs);
    }
     //modify by shenjl at 2013-10-31 clone
    public Map<String, Serializable> getChilds() {
        return ObjectUtils.clone(childs);
    }

    public List<XmlNode> getChilds(String tag) {
        if (childs != null) {
            Object obj = childs.get(tag);
            if (obj == null) {
                return null;
            } else if (obj instanceof List) {
                return (List<XmlNode>) obj;
            } else if (obj instanceof XmlNode){
                ArrayList<XmlNode> list = new ArrayList<XmlNode>(1);
                list.add((XmlNode) obj);
                return list;
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap() {
        Object obj = getMap(this);
        if (obj instanceof String) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put(this.getTag(), obj);
            return map;
        } else {
            return (Map<String, Object>) obj;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static Object getMap(XmlNode xmlNode) {
        Map<String, Serializable> childs = xmlNode.getChilds();
        if (childs == null) {
            return xmlNode.getString();
        }
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Serializable> entry : childs.entrySet()) {
            Serializable obj = entry.getValue();
            if (obj instanceof ArrayList) {
                List<Object> subList = new ArrayList<Object>();
                List<XmlNode> list = (List<XmlNode>) obj;
                for (int i = 0; i < list.size(); i++) {
                    XmlNode node = ((XmlNode) list.get(i));
                    Object sub = getMap(node);
                    if (sub != null) {
                        subList.add(sub);
                    }
                }
                map.put(entry.getKey(), subList);
            } else {
                XmlNode node = ((XmlNode)obj);
                Object o = getMap(node);
                if (o instanceof Map) {
                    //子标签都封装成List，便于外部使用�??2008.11.04
                    List<Object> subList = new ArrayList<Object>();
                    subList.add(o);
                    o = subList;
                }
                map.put(node.getTag(), o);
            }
        }
        return map;
    }

    public void putMap(Map<String, Object> map) {
        putMap(this, map);
    }
    
    @SuppressWarnings("unchecked")
    private static void putMap(XmlNode xmlNode, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String tag = entry.getKey();
            Object value = entry.getValue();
            if (value == null || tag == null) {
                continue;
            }
            if (value instanceof String) {
                XmlNode node = new XmlNode(tag);
                node.setString((String) value);
                xmlNode.addChildNode(node);
            } else if (value instanceof List) {
                List list = (List) value;
                for (int i = 0; i < list.size(); i++) {
                    XmlNode node = new XmlNode(tag);
                    Object sub = list.get(i);
                    if (sub instanceof String) {
                        node.setString((String) sub);
                        xmlNode.addChildNode(node);
                    } else if (sub instanceof Map) {
                        putMap(node, (Map<String, Object>) sub);
                        xmlNode.addChildNode(node);
                    } else {
                        LOGGER.warn("unknow class: " , sub.getClass() , " on tag " , tag , "[" , i , "]");
                    }
                }
            } else if (value instanceof Map) {
                XmlNode node = new XmlNode(tag);
                putMap(node, (Map<String, Object>) value);
                xmlNode.addChildNode(node);
            } else {
                LOGGER.warn("unknow class: " , value.getClass() , " on tag " , tag);
            }
        }
    }

}