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

package com.japisoft.framework.ui.toolkit;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/** Simple swing toolkit */
public final class Toolkit {

	/** Convert a keystroke to a string like 'ctrl p' */
	public static String getKeyStrokeView( KeyStroke k ) {
		int modifier = k.getModifiers();
		char c = (char)k.getKeyCode();
		String result = KeyEvent.getKeyModifiersText( modifier ) + "-" + 
			c;
		return result;
	}
	
	/** Force a selection of the first child of the refNode */
	public static void selectFirstChild( JTree t, TreeNode refNode ) {

		if ( refNode != null ) {
			
			if ( refNode.getChildCount() > 0 ) {

				selectNode( t, refNode.getChildAt( 0 ) );

			}

		}

	}

	/** Simple tree node selection */
	public static void selectNode( JTree t, TreeNode n ) {

		ArrayList al = new ArrayList();
		while ( n != null ) {
			
			al.add(0,n);
			n = n.getParent();
			
		}

		t.setSelectionPath( new TreePath( al.toArray() ) );
		
	}

	public static void addNodeOrdering( DefaultMutableTreeNode parentNode, MutableTreeNode childNode ) {

		boolean inserted = false;
		
		// Lexically ordering

		for  ( int i = 0; i < parentNode.getChildCount(); i++ ) {
			
			DefaultMutableTreeNode child = 
				(DefaultMutableTreeNode)parentNode.getChildAt( i );
			String currentGroup = child.toString();
			if ( currentGroup.compareTo( childNode.toString()) > -1 ) {
				// Insert it
				parentNode.insert( 
						childNode, i );
				inserted = true;
				break;
			}
			
		}

		if ( !inserted )
			parentNode.add( 
					childNode );

	}
	
	public static TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
	     TreeNode[] retNodes;
	     if(aNode == null) {
	         if(depth == 0)
	          return null;
	         else
	          retNodes = new TreeNode[depth];
	     }
	     else {
	         depth++;
	         retNodes = getPathToRoot(aNode.getParent(), depth);
	         retNodes[retNodes.length - depth] = aNode;
	     }
	     return retNodes;
	}	
	
	public static TreePath getPath( TreeNode aNode ) {
		return new TreePath( getPathToRoot( aNode, 0 ) );
	}
	

	
}
