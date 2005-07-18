/*
 * $Id: WriterContext.java,v 1.2 2005-07-18 18:14:22 kohlert Exp $
 */

/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.tools.ws.wsdl.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.ws.util.NamespaceSupport;
import com.sun.tools.ws.util.xml.PrettyPrintingXmlWriter;

/**
 * The context used by writer classes.
 *
 * @author WS Development Team
 */
public class WriterContext {

    public WriterContext(OutputStream os) throws IOException {
        _writer = new PrettyPrintingXmlWriter(os);
        _nsSupport = new NamespaceSupport();
        _newPrefixCount = 2;
    }

    public void flush() throws IOException {
        _writer.flush();
    }

    public void close() throws IOException {
        _writer.close();
    }

    public void push() {
        if (_pendingNamespaceDeclarations != null) {
            throw new IllegalStateException("prefix declarations are pending");
        }
        _nsSupport.pushContext();
    }

    public void pop() {
        _nsSupport.popContext();
        _pendingNamespaceDeclarations = null;
    }

    public String getNamespaceURI(String prefix) {
        return _nsSupport.getURI(prefix);
    }

    public Iterator getPrefixes() {
        return _nsSupport.getPrefixes();
    }

    public String getDefaultNamespaceURI() {
        return getNamespaceURI("");
    }

    public void declarePrefix(String prefix, String uri) {
        _nsSupport.declarePrefix(prefix, uri);
        if (_pendingNamespaceDeclarations == null) {
            _pendingNamespaceDeclarations = new ArrayList();
        }
        _pendingNamespaceDeclarations.add(new String[] { prefix, uri });
    }

    public String getPrefixFor(String uri) {
        if ((getDefaultNamespaceURI() != null && getDefaultNamespaceURI().equals(uri))
            || uri.equals("")) {
            return "";
        } else {
            return _nsSupport.getPrefix(uri);
        }
    }

    public String findNewPrefix(String base) {
        return base + Integer.toString(_newPrefixCount++);
    }

    public String getTargetNamespaceURI() {
        return _targetNamespaceURI;
    }

    public void setTargetNamespaceURI(String uri) {
        _targetNamespaceURI = uri;
    }

    public void writeStartTag(QName name) throws IOException {
        _writer.start(getQNameString(name));
    }

    public void writeEndTag(QName name) throws IOException {
        _writer.end(getQNameString(name));
    }

    public void writeAttribute(String name, String value) throws IOException {
        if (value != null) {
            _writer.attribute(name, value);
        }
    }

    public void writeAttribute(String name, QName value) throws IOException {
        if (value != null) {
            _writer.attribute(name, getQNameString(value));
        }
    }

    public void writeAttribute(String name, boolean value) throws IOException {
        writeAttribute(name, value ? "true" : "false");
    }

    public void writeAttribute(String name, Boolean value) throws IOException {
        if (value != null) {
            writeAttribute(name, value.booleanValue());
        }
    }

    public void writeAttribute(String name, int value) throws IOException {
        writeAttribute(name, Integer.toString(value));
    }

    public void writeAttribute(String name, Object value, Map valueToXmlMap)
        throws IOException {
        String actualValue = (String) valueToXmlMap.get(value);
        writeAttribute(name, actualValue);
    }

    public void writeNamespaceDeclaration(String prefix, String uri)
        throws IOException {
        _writer.attribute(getNamespaceDeclarationAttributeName(prefix), uri);
    }

    public void writeAllPendingNamespaceDeclarations() throws IOException {
        if (_pendingNamespaceDeclarations != null) {
            for (Iterator iter = _pendingNamespaceDeclarations.iterator();
                iter.hasNext();
                ) {
                String[] pair = (String[]) iter.next();
                writeNamespaceDeclaration(pair[0], pair[1]);
            }
        }
        _pendingNamespaceDeclarations = null;
    }

    private String getNamespaceDeclarationAttributeName(String prefix) {
        if (prefix.equals("")) {
            return "xmlns";
        } else {
            return "xmlns:" + prefix;
        }
    }

    public void writeTag(QName name, String value) throws IOException {
        _writer.leaf(getQNameString(name), value);
    }

    public String getQNameString(QName name) {
        String nsURI = name.getNamespaceURI();
        String prefix = getPrefixFor(nsURI);
        if (prefix == null) {
            throw new IllegalArgumentException();
        } else if (prefix.equals("")) {
            return name.getLocalPart();
        } else {
            return prefix + ":" + name.getLocalPart();
        }
    }

    public String getQNameStringWithTargetNamespaceCheck(QName name) {
        if (name.getNamespaceURI().equals(_targetNamespaceURI)) {
            return name.getLocalPart();
        } else {
            return getQNameString(name);
        }
    }

    public void writeChars(String chars) throws IOException {
        _writer.chars(chars);
    }

    private PrettyPrintingXmlWriter _writer;
    private NamespaceSupport _nsSupport;
    private String _targetNamespaceURI;
    private int _newPrefixCount;
    private List _pendingNamespaceDeclarations;
}
