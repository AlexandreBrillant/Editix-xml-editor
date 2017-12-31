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
class ElementProxyNode implements Element {
	private Element ref;

	public ElementProxyNode( Element ref ) {
		this.ref = ref;
	}

	private String newLocalName;
	
	void setNewLocalName( String localName ) {
		this.newLocalName = localName;
	}

	public String getAttribute(String name) {
		return ref.getAttribute(name);
	}

	public Attr getAttributeNode(String name) {
		return ref.getAttributeNode(name);
	}

	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
		return ref.getAttributeNodeNS(namespaceURI, localName);
	}

	public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
		return ref.getAttributeNS( namespaceURI, localName );
	}

	public NodeList getElementsByTagName(String name) {
		return ref.getElementsByTagName( name );
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
		return ref.getElementsByTagNameNS(namespaceURI,localName);
	}

	public TypeInfo getSchemaTypeInfo() {
		return ref.getSchemaTypeInfo();
	}

	public String getTagName() {
		return ref.getTagName();
	}

	public boolean hasAttribute(String name) {
		return ref.hasAttribute( name );
	}

	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
		return ref.hasAttributeNS( namespaceURI, localName ) ;
	}

	public void removeAttribute(String name) throws DOMException {
		ref.removeAttribute( name );
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return ref.removeAttributeNode( oldAttr );
	}

	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		ref.removeAttributeNS( namespaceURI, localName );
	}

	public void setAttribute(String name, String value) throws DOMException {
		ref.setAttribute( name, value );
	}

	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return ref.setAttributeNode( newAttr );
	}

	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return ref.setAttributeNodeNS( newAttr );
	}

	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		ref.setAttributeNS( namespaceURI, qualifiedName, value );
	}

	public void setIdAttribute(String name, boolean isId) throws DOMException {
		ref.setIdAttribute( name, isId );
	}

	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
		ref.setIdAttributeNode( idAttr, isId );
	}

	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
		ref.setIdAttributeNS( namespaceURI, localName, isId );
	}

	public Node appendChild(Node newChild) throws DOMException {
		return ref.appendChild( newChild );
	}

	public Node cloneNode(boolean deep) {
		return ref.cloneNode( deep );
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return ref.compareDocumentPosition( other );
	}

	public NamedNodeMap getAttributes() {
		return ref.getAttributes();
	}

	public String getBaseURI() {
		return ref.getBaseURI();
	}

	public NodeList getChildNodes() {
		return ref.getChildNodes();
	}

	public Object getFeature(String feature, String version) {
		return ref.getFeature( feature, version );
	}

	public Node getFirstChild() {
		return ref.getFirstChild();
	}

	public Node getLastChild() {
		return ref.getLastChild();
	}

	public String getLocalName() {
		if ( newLocalName != null )
			return newLocalName;
		return ref.getLocalName();
	}

	public String getNamespaceURI() {
		return ref.getNamespaceURI();
	}

	public Node getNextSibling() {
		return ref.getNextSibling();
	}

	public String getNodeName() {
		return ref.getNodeName();
	}

	public short getNodeType() {
		return ref.getNodeType();
	}

	public String getNodeValue() throws DOMException {
		return ref.getNodeValue();
	}

	public Document getOwnerDocument() {
		return ref.getOwnerDocument();
	}

	public Node getParentNode() {
		return ref.getParentNode();
	}

	public String getPrefix() {
		return ref.getPrefix();
	}

	public Node getPreviousSibling() {
		return ref.getPreviousSibling();
	}

	public String getTextContent() throws DOMException {
		return ref.getTextContent();
	}

	public Object getUserData(String key) {
		return ref.getUserData( key );
	}

	public boolean hasAttributes() {
		return ref.hasAttributes();
	}

	public boolean hasChildNodes() {
		return ref.hasChildNodes();
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return ref.insertBefore( newChild,refChild );
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return ref.isDefaultNamespace( namespaceURI );
	}

	public boolean isEqualNode(Node arg) {
		return ref.isEqualNode( arg );
	}

	public boolean isSameNode(Node other) {
		return ref.isSameNode( other );
	}

	public String lookupNamespaceURI(String prefix) {
		return ref.lookupNamespaceURI( prefix );
	}

	public String lookupPrefix(String namespaceURI) {
		return ref.lookupPrefix( namespaceURI );
	}

	public void normalize() {
		ref.normalize();
	}

	public Node removeChild(Node oldChild) throws DOMException {
		return ref.removeChild( oldChild );
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return ref.replaceChild( newChild, oldChild );
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		ref.setNodeValue( nodeValue );
	}

	public void setPrefix(String prefix) throws DOMException {
		ref.setPrefix( prefix );
	}

	public void setTextContent(String textContent) throws DOMException {
		ref.setTextContent( textContent );
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return ref.setUserData( key, data, handler );
	}
	
	public boolean isSupported(String arg0, String arg1) {
		return ref.isSupported( arg0, arg1 );
	}	
	
}
