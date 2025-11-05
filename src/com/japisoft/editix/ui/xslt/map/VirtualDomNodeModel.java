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

package com.japisoft.editix.ui.xslt.map;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class VirtualDomNodeModel implements TreeModel {

	private VirtualDomNode root;

	public VirtualDomNodeModel( VirtualDomNode root ) {
		this.root = root;
	}

	private TreeModelListener l;
	
	public void addTreeModelListener(TreeModelListener l) {
		this.l = l;
	}

	public Object getChild(Object parent, int index) {
		return ( ( VirtualDomNode )parent ).getChildAt( index );
	}

	public int getChildCount(Object parent) {
		return ( ( VirtualDomNode )parent ).getChildCount();		
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ( ( VirtualDomNode )parent ).getIndexOfChild( ( VirtualDomNode )child );
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		return getChildCount( node ) == 0;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		this.l = null;
	}

	public void fireUpdate() {
		this.l.treeStructureChanged( new TreeModelEvent(this, new Object[] { getRoot() } ) );
	}
	
	public void valueForPathChanged(TreePath path, Object newValue) {
	}
	
}

