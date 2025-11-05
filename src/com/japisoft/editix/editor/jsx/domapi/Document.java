// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.editor.jsx.domapi;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.xml.DOMToolkit;

/**
 * The Document Object Model (DOM) connects web pages to scripts or programming languages by representing the structure of a document
 * 
 * The DOM represents a document with a logical tree. Each branch of the tree ends in a node, and each node contains objects. DOM methods allow programmatic access to the tree. 
 * With them, you can change the document's structure, style, or content. Nodes can also have event handlers attached to them. Once an event is triggered, the event handlers get executed.
 */
public class Document extends Node {

	public String documentURI;
	public Element documentElement;
	
	public Document( String documentURI, org.w3c.dom.Document node ) {
		super( null, node );
		this.documentURI = documentURI;
		documentElement = new Element( this, node.getDocumentElement() );
	}

	public Document read( String path ) {
		try {
			org.w3c.dom.Document doc = DOMToolkit.parse( false, path );
			return new Document( path, doc );
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( exc.getMessage() );
			return null;
		}
	}
	
	public boolean save() {
		try {
			DOMToolkit.save( this.documentURI, node );
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( exc.getMessage() );
			return false;
		}
		return true;
	}
	
	public boolean save( String path ) {
		try {
			DOMToolkit.save( path , node );
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( exc.getMessage() );
			return false;
		}
		return true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Attr createAttribute( String name ) {
		return new Attr( 
			this,
			( (org.w3c.dom.Document)getDOMNode() ).createAttribute( name )
		);
	}
	
	public Attr createAttributeNS( String namespaceURI, String qualifiedName ) {
		return new Attr(
			this,
			( (org.w3c.dom.Document)getDOMNode() ).createAttributeNS( namespaceURI, qualifiedName) );
	}

	public Element createElement( String tagName ) {
		return new Element(
			this,
			( (org.w3c.dom.Document)getDOMNode() ).createElement( tagName ) );
	}
	
	public Element createElementNS( String namespaceURI, String qualifiedName ) {
		return new Element(
			this,
			( (org.w3c.dom.Document)getDOMNode() ).createElementNS(namespaceURI, qualifiedName ) );
	}
	
	public Text createTextNode( String data ) {
		return new Text( 
			this,
			( (org.w3c.dom.Document)getDOMNode() ).createTextNode( data ) );
	}
	
	public Node createNode( org.w3c.dom.Node node ) {
		if ( node instanceof org.w3c.dom.Element )
			return new Element( this, node );
		if ( node instanceof org.w3c.dom.Text )
			return new Text( this, node );
		if ( node instanceof org.w3c.dom.Attr )
			return new Attr( this, node );
		return new Node( this, node );
	}
	
	@Override
	Document getOwner() {
		return this;
	}
	
	public Object evaluate( String xpathExpression, Node contextNode, Object resolver, short type, Object result ) {
		QName resType = XPathConstants.NODESET;
		if ( type == 1 )
			resType = XPathConstants.NUMBER;
		if ( type == 2 )
			resType = XPathConstants.STRING;
		if ( type == 3 )
			resType = XPathConstants.BOOLEAN;
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		try {
			Object res = xpath.evaluate( xpathExpression, contextNode.getDOMNode(), resType );
			
			if ( res instanceof org.w3c.dom.NodeList ) {
				org.w3c.dom.Node[] nodes = DOMToolkit.toArray( (org.w3c.dom.NodeList)res );
				res = new Node[ nodes.length ];
				for ( int i = 0; i < nodes.length; i++ )
					((Node[])res)[ i ] = createNode( nodes[ i ] );
				return res;
			}
			
			return res;
			
		} catch( Exception exc ) {
			System.out.println( "Can't evaluate " + xpathExpression );
			System.out.println( "Caused by :" + exc.getMessage() );
		}
		
		return null;
	}
	
	public Node importNode( Node node, boolean deepMode ) {
		return createNode( ( (org.w3c.dom.Document)getDOMNode() ).importNode( node.getDOMNode(), deepMode ) );
	}
	
}

