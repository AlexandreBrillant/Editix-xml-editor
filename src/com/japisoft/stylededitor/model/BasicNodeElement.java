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

package com.japisoft.stylededitor.model;

import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.w3c.dom.Node;

import com.japisoft.framework.css.Property;
import com.japisoft.framework.css.Rule;
import com.japisoft.framework.css.Toolkit;

public class BasicNodeElement implements NodeElement {

	protected Node node = null;
	protected Document document = null;
	private int startOffset = 0, endOffset = 0;
	
	public BasicNodeElement( 
			Document document, 
			Node node, 
			int startOffset ) {
		this.document = document;
		this.node = node;
		this.startOffset = startOffset;
		this.endOffset = startOffset;		
	}

	public AttributeSet getAttributes() {
		return null;
	}

	public Document getDocument() {
		return document;
	}

	public Node getNode() {
		return node;
	}

	public Element getElement( int index ) {
		return null;
	}

	public int getElementCount() {
		return 0;
	}

	public int getElementIndex( int offset ) {
		return -1;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset( int offset ) {
		this.startOffset = offset;
	}
	
	public void setEndOffset( int offset ) {
		this.endOffset = offset;
	}

	public int getEndOffset() {
		return endOffset;
	}
	
	public String getName() {
		return "<" + node.getNodeName() + ">";
	}

	public Element getParentElement() {
		return document.getDefaultRootElement();
	}

	public boolean isLeaf() {
		return true;
	}
	
	public void setData( String name, Object value ) {
		node.setUserData( name, value, null );
	}

	public Object getData( String name ) {
		return node.getUserData( name ); 
	}

	public boolean isDOMElement() {
		return node instanceof org.w3c.dom.Element;
	}

	public Object getCSSProperty( String name, Object defaultValue ) {
		Rule r = ( Rule )node.getUserData( "css" );
		if ( r == null && Toolkit.isInheritedProperty( name ) ) {
			Node tmpNode = node;
			while ( !( tmpNode instanceof Document ) ) {
				tmpNode = tmpNode.getParentNode();
				if ( tmpNode == null )
					break;
				Rule rTmp = ( Rule )tmpNode.getUserData( "css" );
				if ( rTmp != null ) {
					if ( r == null )
						r = rTmp;
					else
						r = r.merge( rTmp );
				}
			}
			if ( r == null )
				r = Rule.EMPTY_RULE;
			node.setUserData( "css", r, null );
		}
		if ( r == null )
			r = Rule.EMPTY_RULE;
		Property p = r.getProperty( name );
		if ( p == null ) {
			return defaultValue;
		}
		return p.getValue();
	}

}

