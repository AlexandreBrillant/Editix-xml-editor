package com.japisoft.framework.xml.refactor.elements;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class AttrProxyNode implements Attr {
	public Attr r;
	private String newNodeName;

	public AttrProxyNode( Attr r, String newNodeName ) {
		this.r = r;
		this.newNodeName = newNodeName;
	}

	public String getName() {
		return r.getName();
	}

	public boolean getSpecified() {
		return r.getSpecified();
	}

	public String getValue() {
		return r.getValue();
	}

	public void setValue(String value) throws DOMException {
		r.setValue( value );
	}

	public Element getOwnerElement() {
		return r.getOwnerElement();
	}

	public TypeInfo getSchemaTypeInfo() {
		return r.getSchemaTypeInfo();
	}

	public boolean isId() {
		return r.isId();
	}

	public String getNodeName() {
		if ( newNodeName != null )
			return newNodeName;
		return r.getNodeName();
	}

	public String getNodeValue() throws DOMException {
		return r.getNodeValue();
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		r.setNodeValue( nodeValue );
	}

	public short getNodeType() {
		return r.getNodeType();
	}

	public Node getParentNode() {
		return r.getParentNode();
	}

	public NodeList getChildNodes() {
		return r.getChildNodes();
	}

	public Node getFirstChild() {
		return r.getFirstChild();
	}

	public Node getLastChild() {
		return r.getLastChild();
	}

	public Node getPreviousSibling() {
		return r.getPreviousSibling();
	}

	public Node getNextSibling() {
		return r.getNextSibling();
	}

	public NamedNodeMap getAttributes() {
		return r.getAttributes();
	}

	public Document getOwnerDocument() {
		return r.getOwnerDocument();
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return r.insertBefore( newChild, refChild );
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return r.replaceChild( newChild, oldChild );
	}

	public Node removeChild(Node oldChild) throws DOMException {
		return r.removeChild( oldChild );
	}

	public Node appendChild(Node newChild) throws DOMException {
		return r.appendChild( newChild );
	}

	public boolean hasChildNodes() {
		return r.hasChildNodes();
	}

	public Node cloneNode(boolean deep) {
		return r.cloneNode( deep );
	}

	public void normalize() {
		r.normalize();
	}

	public boolean isSupported(String feature, String version) {
		return r.isSupported( feature, version );
	}

	public String getNamespaceURI() {
		return r.getNamespaceURI();
	}

	public String getPrefix() {
		return r.getPrefix();
	}

	public void setPrefix(String prefix) throws DOMException {
		r.setPrefix( prefix );
	}

	public String getLocalName() {
		return r.getLocalName();
	}

	public boolean hasAttributes() {
		return r.hasAttributes();
	}

	public String getBaseURI() {
		return r.getBaseURI();
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return r.compareDocumentPosition( other );
	}

	public String getTextContent() throws DOMException {
		return r.getTextContent();
	}

	public void setTextContent(String textContent) throws DOMException {
		r.setTextContent( textContent );
	}

	public boolean isSameNode(Node other) {
		return r.isSameNode( other );
	}

	public String lookupPrefix(String namespaceURI) {
		return r.lookupPrefix( namespaceURI );
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return r.isDefaultNamespace( namespaceURI );
	}

	public String lookupNamespaceURI(String prefix) {
		return r.lookupNamespaceURI( prefix );
	}

	public boolean isEqualNode(Node arg) {
		return r.isEqualNode( arg );
	}

	public Object getFeature(String feature, String version) {
		return r.getFeature( feature, version );
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return r.setUserData( key, data, handler );
	}

	public Object getUserData(String key) {
		return r.getUserData( key );
	}

}
