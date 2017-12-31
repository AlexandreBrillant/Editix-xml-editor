package com.japisoft.framework.xml.refactor.elements;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
public class EntityProxyNode implements EntityReference {
	private EntityReference ref;
	private String newName;
	
	public EntityProxyNode( EntityReference ref, String newName ) {
		this.ref = ref;
		this.newName = newName;
	}

	public String getNotationName() {
		return ref.getNodeName();
	}

	public String getNodeName() {
		if ( newName != null )
			return newName;
		return ref.getNodeName();
	}

	public String getNodeValue() throws DOMException {
		return ref.getNodeName();
	}

	public void setNodeValue(String arg0) throws DOMException {
		ref.setNodeValue( arg0 );
	}

	public short getNodeType() {
		return ref.getNodeType();
	}

	public Node getParentNode() {
		return ref.getParentNode();
	}

	public NodeList getChildNodes() {
		return ref.getChildNodes();
	}

	public Node getFirstChild() {
		return ref.getFirstChild();
	}

	public Node getLastChild() {
		return ref.getLastChild();
	}

	public Node getPreviousSibling() {
		return ref.getPreviousSibling();
	}

	public Node getNextSibling() {
		return ref.getNextSibling();
	}

	public NamedNodeMap getAttributes() {
		return ref.getAttributes();
	}

	public Document getOwnerDocument() {
		return ref.getOwnerDocument();
	}

	public Node insertBefore(Node arg0, Node arg1) throws DOMException {
		return ref.insertBefore( arg0, arg1 );
	}

	public Node replaceChild(Node arg0, Node arg1) throws DOMException {
		return ref.replaceChild( arg0, arg1 );
	}

	public Node removeChild(Node arg0) throws DOMException {
		return ref.removeChild( arg0 );
	}

	public Node appendChild(Node arg0) throws DOMException {
		return ref.appendChild( arg0 );
	}

	public boolean hasChildNodes() {
		return ref.hasChildNodes();
	}

	public Node cloneNode(boolean arg0) {
		return ref.cloneNode(arg0);
	}

	public void normalize() {
		ref.normalize();
	}

	public boolean isSupported(String arg0, String arg1) {
		return ref.isSupported( arg0, arg1 );
	}

	public String getNamespaceURI() {
		return ref.getNamespaceURI();
	}

	public String getPrefix() {
		return ref.getPrefix();
	}

	public void setPrefix(String arg0) throws DOMException {
		ref.setPrefix( arg0 );
	}

	public String getLocalName() {
		return ref.getLocalName();
	}

	public boolean hasAttributes() {
		return ref.hasAttributes();
	}

	public String getBaseURI() {
		return ref.getBaseURI();
	}

	public short compareDocumentPosition(Node arg0) throws DOMException {
		return ref.compareDocumentPosition( arg0 );
	}

	public String getTextContent() throws DOMException {
		return ref.getTextContent();
	}

	public void setTextContent(String arg0) throws DOMException {
		ref.setTextContent( arg0 );
	}

	public boolean isSameNode(Node arg0) {
		return ref.isSameNode( arg0 );
	}

	public String lookupPrefix(String arg0) {
		return ref.lookupPrefix( arg0 );
	}

	public boolean isDefaultNamespace(String arg0) {
		return ref.isDefaultNamespace( arg0 );
	}

	public String lookupNamespaceURI(String arg0) {
		return ref.lookupNamespaceURI( arg0 );
	}

	public boolean isEqualNode(Node arg0) {
		return ref.isEqualNode( arg0 );
	}

	public Object getFeature(String arg0, String arg1) {
		return ref.getFeature( arg0, arg1 );
	}

	public Object setUserData(String arg0, Object arg1, UserDataHandler arg2) {
		return ref.setUserData( arg0, arg1, arg2 );
	}

	public Object getUserData(String arg0) {
		return ref.getUserData( arg0 );
	}

}
