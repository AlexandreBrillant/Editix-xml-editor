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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NodeList;

/**
 * Element is the most general base class from which all element objects (i.e. objects that represent elements) in a Document inherit. It only has methods and properties common to all kinds of elements. More specific classes inherit from Element.
 */
public class Element extends Node {

	public Element []children = null;
	public int childElementCount = 0;
	public NamedNodeMap attributes;
	public Element firstElementChild = null;
	public Element lastElementChild = null;
	
	public Element( Document owner, org.w3c.dom.Node node ) {
		super( owner, node );
		resolveChildren();
	}
	
	protected void resolveChildren() {
		children = null;
		ArrayList<Element> tab = null;
		
		if ( node.hasChildNodes() ) {
			NodeList nl = node.getChildNodes();
			for ( int i = 0; i < nl.getLength(); i++ ) {
				if ( nl.item( i ) instanceof org.w3c.dom.Element ) {
					if ( tab == null )
						tab = new ArrayList<Element>();
					tab.add( new Element( getOwner(), nl.item( i ) ) );
				}			
			}
		}
		
		childElementCount = 0;
		if ( tab != null )
			childElementCount = tab.size();
		
		attributes = new NamedNodeMap( this, node.getAttributes() );
		firstElementChild = null;
		lastElementChild = null;
		
		if ( childElementCount > 0 ) {
			firstElementChild = tab.get( 0 );
			lastElementChild = tab.get( tab.size() - 1 );
		}
		
		if ( tab != null ) {
			children = tab.toArray( new Element[ tab.size() ] );
		}
	}
	
	public void after( Object...nodes ) {
		int index = parentNode.childNodes.indexOf( this );

		for ( Object n : nodes ) {
			if ( n instanceof String ) {
				parentNode.childNodes.insert( parentNode, index + 1, getOwner().createTextNode( (String)n ) );
			} else
			if ( n instanceof Node ) {
				parentNode.childNodes.insert( parentNode, index + 1, (Node)n );
			}
		}
	}

	public void append( Object...nodes ) {
		for ( Object n : nodes ) {
			if ( n instanceof Node ) {
				getDOMNode().appendChild( ((Node)n).getDOMNode() );
			} else
			if ( n instanceof String ) {
				getDOMNode().appendChild( getDOMNode().getOwnerDocument().createTextNode( (String)n ) );
			}
		}
		resolveChildren();
	}

	public void before( Object...nodes ) {
		int index = parentNode.childNodes.indexOf( this );

		for ( Object n : nodes ) {
			if ( n instanceof String ) {
				parentNode.childNodes.insert( parentNode, index, getOwner().createTextNode( (String)n ) );
			} else
			if ( n instanceof Node ) {
				parentNode.childNodes.insert( parentNode, index, (Node)n );
			}
		}
	}

	public String getAttribute( String attributeName ) {
		Attr att = getAttributeNode( attributeName );
		if ( att == null )
			return null;
		return att.nodeValue;
	}
	
	public String getAttributeNS( String namespace, String name ) {
		Attr att = getAttributeNodeNS( namespace, name );
		if ( att == null )
			return null;
		return att.nodeValue;
	}

	public boolean hasAttribute( String name ) {
		for ( int i = 0; i < attributes.length; i++ )
			if ( name.equals( attributes.item( i ).name ) )
				return true;
		return false;
	}
	
	public String[] getAttributesNames() {
		if ( attributes == null )
			return new String[] {};
		String[] res = new String[ attributes.length ];
		for ( int i = 0; i < attributes.length; i++ )
			res[ i ] = attributes.item( i ).name;
		return res;
	}
		
	public Attr getAttributeNode( String name ) {
		if ( attributes == null )
			return null;
		return attributes.getNamedItem( name );
	}
	
	public boolean hasAttributes() {
		return attributes != null && attributes.length > 0;
	}
	
	public Attr getAttributeNodeNS( String namespace, String name ) {
		if ( attributes == null )
			return null;
		return attributes.getNamedItemNS( namespace, name );
	}
	
	public void setAttribute( String name, String value ) {
		Attr node = getAttributeNode( name );
		if ( node != null ) {
			node.getDOMNode().setNodeValue( value );
			node.nodeValue = value;
		} else {
			// New Attribute
			org.w3c.dom.Attr att = getDOMNode().getOwnerDocument().createAttribute( name );
			att.setNodeValue( value );
			getDOMNode().getAttributes().setNamedItem( att );
			attributes = new NamedNodeMap( this, getDOMNode().getAttributes() );
		}
	}
	
	public Element[] getElementsByTagNameNS( String namespaceURI, String tagName ) {	
		org.w3c.dom.NodeList nl = ((org.w3c.dom.Element)getDOMNode()).getElementsByTagNameNS( namespaceURI, tagName );
		List<Element> res = null;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( res == null )
				res = new ArrayList<Element>();
			org.w3c.dom.Node n = nl.item( i );
			if ( n.getUserData( "node" ) instanceof Element ) {
				res.add( ( Element )n.getUserData( "node" ) );
			}
		}
		if ( res == null )
			return new Element[] {};
		return res.toArray( new Element[ res.size() ] );
	}
	
	public Element[] getElementsByTagName(String tagName) {
		org.w3c.dom.NodeList nl = ((org.w3c.dom.Element)getDOMNode()).getElementsByTagName( tagName );
		List<Element> res = null;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( res == null )
				res = new ArrayList<Element>();
			org.w3c.dom.Node n = nl.item( i );
			if ( n.getUserData( "node" ) instanceof Element ) {
				res.add( ( Element )n.getUserData( "node" ) );
			}
		}
		if ( res == null )
			return new Element[] {};
		return res.toArray( new Element[ res.size() ] );
	}
	
	public Object evaluate( String xpathExpression, short type ) { 
		return getOwner().evaluate( xpathExpression, this, null, type, null );
	}
	
	public Object evaluate( String xpathExpression ) {
		return evaluate( xpathExpression, (short)4 );
	}
	
}

