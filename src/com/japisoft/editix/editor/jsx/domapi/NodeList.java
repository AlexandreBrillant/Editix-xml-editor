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

import java.util.ArrayList;
import java.util.List;

/**
 * NodeList objects are collections of nodes, usually returned by properties such as Node.childNodes
 */
public class NodeList {

	private List<Node> nodes = null;
	public int length = 0; 

	public Node item(int index) {
		if ( nodes == null )
			return null;
		if ( index >= nodes.size() || index < 0 )
			return null;
		return nodes.get( index );
	}
	
	public Node addNode( Node node ) {
		if ( nodes == null )
			nodes = new ArrayList<Node>();
		nodes.add( node );
		length = nodes.size();
		return node;
	}
	
	int indexOf( Node node ) {
		if ( nodes == null )
			return -1;
		return nodes.indexOf( node );
	}
	
	void insert( Node parent, int index, Node node ) {
		if ( nodes == null || index >= length ) {
			addNode( node );
			parent.getDOMNode().appendChild( node.getDOMNode() );
		} else
		{
			parent.getDOMNode().insertBefore( node.getDOMNode(), nodes.get( index ).getDOMNode() );
			
			nodes.add( index, node );
			length++;
			

		}
	}
}
