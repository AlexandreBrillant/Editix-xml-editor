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

package com.japisoft.xmlform.designer.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public class AbstractTreeNode implements TreeNode {

	private ArrayList<TreeNode> children = null;
	
	public Enumeration children() {
		if ( children != null )
			return Collections.enumeration( children );
		return null;
	}
	
	public void addChild( TreeNode node ) {
		if ( children == null )
			children = new ArrayList<TreeNode>();
		children.add( node );
	}
	
	public TreeNode getNextSibling() {
		if ( parent == null )
			return null;
		int index = parent.getIndex( this );
		if ( index == -1 )	// ?
			return null;
		if ( index < parent.getChildCount() - 1 ) {
			return parent.getChildAt( index + 1 );
		} else
			return null;	// The last one
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		if ( children == null )
			return null;
		return children.get( childIndex );
	}

	public int getChildCount() {
		if ( children == null )
			return 0;
		return children.size();
	}

	public int getIndex(TreeNode node) {
		if ( children == null )
			return -1;
		return children.indexOf( node );
	}

	protected TreeNode parent = null;
	
	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return ( getChildCount() == 0 );
	}

	private Object userObj = null;
	
	public void setUserObject( Object obj ) {
		userObj = obj;
	}
	
	public Object getUserObject() {
		return userObj;
	}

}

