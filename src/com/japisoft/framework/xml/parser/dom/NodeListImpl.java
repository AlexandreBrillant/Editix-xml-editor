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

package com.japisoft.framework.xml.parser.dom;

import java.util.Vector;

import com.japisoft.framework.xml.parser.node.*;

import org.w3c.dom.*;

/**
 * Implementation for <code>NodeList</code>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class NodeListImpl implements NodeList {
	private FPNode n;

	/** @param n reference node */
	public NodeListImpl(FPNode n) {
		super();
		this.n = n;
	}

	private String filter;

	/** @param n reference node
	 * @param filter tag name to find */
	public NodeListImpl(FPNode n, String filter) {
		this(n);
		this.filter = filter;
		if  ( filter != null )
			prepareChildren();
	}

	private Vector children = null;

	private void prepareChildren() {
		if ( children == null )
			children = new Vector();
		prepareChildren( n );
	}

	private void prepareChildren( FPNode n ) {
		for ( int i = 0; i < n.childCount(); i++ ) {
			FPNode n2 = n.childAt( i );
			if ( n2.isTag() && filter.equals( n2.getNodeContent() ) )
				children.addElement( n2 );
		}
		for ( int i = 0; i < n.childCount(); i++ ) {
			FPNode n2 = n.childAt( i );
			if ( n2.isTag() )
				prepareChildren( n2 );		
		}
	}

	/** @return a node at index or <code>null</code> */
	public Node item(int index) {
		if (filter == null)
			return (Node) n.childAt(index);
		else
			return ( Node )children.elementAt( index );
	}

	/** @return the total number of node */
	public int getLength() {
		if (filter == null)
			return n.childCount();
		else {
			return children.size();
		}
	}

}

