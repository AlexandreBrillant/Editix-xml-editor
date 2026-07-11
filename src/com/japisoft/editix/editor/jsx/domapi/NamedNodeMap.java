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
 * The NamedNodeMap interface represents a collection of Attr objects. Objects inside a NamedNodeMap are not in any particular order, unlike NodeList, although they may be accessed by an index as in an array.
 */
public class NamedNodeMap {

	private org.w3c.dom.NamedNodeMap dom;
	private Element owner;

	public int length;
	
	public NamedNodeMap( Element owner, org.w3c.dom.NamedNodeMap nnm ) {
		this.owner = owner;
		this.dom = nnm;
		updateLength();
	}
	
	public Attr getNamedItem( String name ) {
		org.w3c.dom.Node node = dom.getNamedItem( name );
		if ( node == null )
			return null;
		return new Attr( owner, node );
	}
	
	public void setNamedItem( Attr att ) {
		dom.setNamedItem( att.getDOMNode() );
		updateLength();
	}
	
	public void removeNamedItem( String name ) {
		dom.removeNamedItem( name );
		updateLength();
	}
	
	public Attr item( int index ) {
		org.w3c.dom.Node node = dom.item( index );
		if ( node == null )
			return null;
		
		Object tmp = node.getUserData( "node" );
		if ( tmp != null ) {
			if ( tmp instanceof Attr )
				return (Attr)tmp;
		}
		
		return new Attr( owner, node );
	}
	
	public Attr getNamedItemNS( String namespace, String localName ) {
		org.w3c.dom.Node node = this.dom.getNamedItemNS( namespace, localName );
		if ( node == null )
			return null;
		
		Object tmp = node.getUserData( "node" );
		if ( tmp != null ) {
			if ( tmp instanceof Attr )
				return (Attr)tmp;
		}
		
		return new Attr( owner, node );
	}
	
	public void setNamedItemNS( Attr att ) {
		dom.setNamedItemNS( att.getDOMNode() );
		updateLength();
	}
		
	public void removeNamedItemNS( String namespace, String localName ) {
		dom.removeNamedItemNS(namespace, localName);
		updateLength();
	}
	
	private void updateLength() {
		length = dom.getLength();
	}
	
}
