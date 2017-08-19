package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EmptyStackException;
import java.util.Stack;

public class SAXmlDecoder extends DefaultHandler {
    private static final Logger log = new Logger();
    
    private boolean debug;
    private Stack<XmlNode> nodes = new Stack<XmlNode>();
    private XmlTopNode topNode;
    private Locator2 locator2;
    
    public void startDocument() throws SAXException {
        topNode = null;
    }

    public void endDocument() throws SAXException {
        if (debug) log.debug("decode done: \n", topNode);
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void setDocumentLocator(Locator locator) {
        locator2 = locator instanceof Locator2 ? (Locator2) locator : null;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
        XmlNode node;
        if (topNode == null) {
            if (locator2 == null) {
                topNode = new XmlTopNode(qName);
            } else {
                topNode = new XmlTopNode(qName, locator2.getEncoding(), locator2.getXMLVersion());
            }
            node = topNode;
        } else {
            node = new XmlNode(qName);
        }
        for (int i = 0; i < attr.getLength(); i++) {
            node.setAttribute(attr.getQName(i), attr.getValue(i));
        }
        nodes.push(node);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        XmlNode node = (XmlNode) nodes.pop();
        String str = node.getString();
        if (str != null) {
            str = str.trim();
            if (str.length() == 0) {
                node.setString(null);
            } else {
                //add 2008.11.07 增加html编码处理
                str = StringUtil.replace(str, "&amp;", "&");
                str = StringUtil.replace(str, "&lt;", "<");
                str = StringUtil.replace(str, "&gt;", ">");
                node.setString(str);
            }
        }
        try {
            XmlNode parent = (XmlNode) nodes.peek();
            parent.addChildNode(node);
        } catch (EmptyStackException e) {}
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
        XmlNode node = (XmlNode) nodes.peek();
        node.appendString(new String(ch, start, length));
    }
    
    public XmlTopNode decode(byte[] bs) throws ParserConfigurationException, SAXException, IOException {
        return decode(new ByteArrayInputStream(bs));
    }
    
    public XmlTopNode decode(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();
        saxParser.parse(is, this);
        return getTopNode();
    }
    
    public XmlTopNode getTopNode() {
        return topNode;
    }
    
}
