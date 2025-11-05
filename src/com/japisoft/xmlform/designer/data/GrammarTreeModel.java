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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.japisoft.framework.xml.grammar.GrammarElement;
import com.japisoft.framework.xml.grammar.GrammarNode;

public class GrammarTreeModel implements TreeModel {

	private GrammarNodeTreeNode root = null;

	public GrammarTreeModel( GrammarElement root ) {
		ArrayList<GrammarNode> processed = new ArrayList<GrammarNode>();
		this.root = 
			new GrammarNodeTreeNode( processed, root );
	}

	public void addTreeModelListener(TreeModelListener l) {
	}

	public Object getChild(Object parent, int index) {
		return ( ( TreeNode )parent ).getChildAt( index );
	}

	public int getChildCount(Object parent) {
		return ( ( TreeNode )parent ).getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ( ( TreeNode )parent ).getIndex( ( TreeNode )child );
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		return ( (TreeNode)node ).isLeaf();
	}

	public void removeTreeModelListener(TreeModelListener l) {}
	public void valueForPathChanged(TreePath path, Object newValue) {}

}

