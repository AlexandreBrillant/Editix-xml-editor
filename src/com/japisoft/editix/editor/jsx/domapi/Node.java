// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.editor.jsx.domapi;

/**
 * The DOM Node interface is an abstract base class upon which many other DOM API objects are based, thus letting those object types to be used similarly and often interchangeably. As an abstract class, there is no such thing as a plain Node object. All objects that implement Node functionality are based on one of its subclasses. Most notable are Document, Element
 */
public class Node {

	protected org.w3c.dom.Node node;

	public String baseURI;
	public NodeList childNodes;
	
	public String localName;
	public String nodeName;
	public String tagName;
	public String namespaceURI;
	public short nodeType;
	public String nodeValue;
	public Node firstChild = null;
	public Node lastChild = null;
	public Node parentNode = null;
	public String textContent = null;
	
	protected Node( Document owner, org.w3c.dom.Node node ) {
		node.setUserData( "node", this, null );
		node.setUserData( "owner", owner, null );
		
		this.node = node;
		this.baseURI = node.getBaseURI();
		this.nodeValue = node.getNodeValue();
		this.nodeType = node.getNodeType();
		this.tagName = node.getNodeName();
		this.localName = node.getLocalName();
		this.nodeName = node.getNodeName();
		this.namespaceURI = node.getNamespaceURI();
		this.textContent = node.getTextContent();
		
		if ( this.node.getParentNode() != null )
			this.parentNode = (Node)this.node.getParentNode().getUserData( "node" );
		
		updateFromDOM();		
	}
	
	private void updateFromDOM() {
		org.w3c.dom.NodeList nl = node.getChildNodes(); 
		NodeList bnl = new NodeList();
		if ( nl != null )
			for ( int i = 0; i < nl.getLength(); i++ ) {				
				Node newNode = bnl.addNode( getOwner().createNode( nl.item( i ) ) );
				newNode.parentNode = this;
				nl.item( i ).setUserData( "node", newNode, null );
			}
		updateFirstLast();
		this.childNodes = bnl;
	}
	
	private void updateFirstLast() {
		if ( this.childNodes != null ) {
			for ( int i = 0; i < this.childNodes.length; i++ ) {
				if ( i == 0 )
					firstChild = this.childNodes.item( i );
				if ( i == this.childNodes.length - 1 )
					lastChild = this.childNodes.item( i );
			}
		}
	}		
	
	public Node appendChild( Node newChild ) {
		this.childNodes.addNode( newChild );
		this.node.appendChild( newChild.getDOMNode() );
		updateFirstLast();
		return newChild;
	}
	
	public Node cloneNode() {
		return getOwner().createNode( this.node.cloneNode( true ) );
	}
	
	public boolean hasChildNodes() {
		return this.childNodes != null && this.childNodes.length > 0;
	}
	
	public Node insertBefore( Node newNode, Node refNode ) {
		this.node.insertBefore( newNode.getDOMNode(), refNode.getDOMNode() );
		updateFromDOM();
		return newNode;
	}
	
	public boolean isDefaultNamespace( String namespaceURI ) {
		return this.node.isDefaultNamespace( namespaceURI );
	}
	
	public boolean isEqualNode( Node otherNode ) {
		return this.node.isEqualNode( otherNode.getDOMNode() );
	}

	public boolean isSameNode( Node otherNode ) {
		return this.node.isSameNode( otherNode.getDOMNode() );
	}
	
	public String lookupPrefix( String namespaceURI  ) {
		return this.node.lookupPrefix( namespaceURI );
	}
	
	public String lookupNamespaceURI( String prefix ) {
		return this.node.lookupNamespaceURI( prefix );
	}
	
	public void normalize() {
		this.node.normalize();
		updateFromDOM();
	}
	
	public void removeChild( Node child ) {
		this.node.removeChild( child.getDOMNode() );
		updateFromDOM();
	}
	
	public void replaceChild( Node newChild, Node oldChild ) {
		this.node.replaceChild( newChild.getDOMNode(), oldChild.getDOMNode() );
		updateFromDOM();
	}
	
	protected void afterTree() { }
	
	org.w3c.dom.Node getDOMNode() { return this.node; }
	
	Document getOwner() { return (Document)node.getUserData( "owner" ); }
	
}
