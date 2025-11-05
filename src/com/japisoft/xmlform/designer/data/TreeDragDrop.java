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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import com.japisoft.framework.xml.grammar.GrammarContainer;
import com.japisoft.framework.xml.grammar.GrammarNode;

public class TreeDragDrop extends TransferHandler implements MouseMotionListener {

	private JTree tree = null;
	
	public TreeDragDrop( JTree tree ) {
		this.tree = tree;
		tree.setTransferHandler( this );
		this.tree.addMouseMotionListener( this );
	}

	public void mouseDragged( MouseEvent e ) {
		exportAsDrag( tree, e, TransferHandler.MOVE );
	}

	public void mouseMoved(MouseEvent e) {	
	}

	GrammarNodeTreeNode dragNode = null;
	
	@Override
	public void exportAsDrag(
			JComponent comp, 
			InputEvent e, 
			int action ) {
		JTree tree = ( JTree )comp;
		MouseEvent me = ( MouseEvent )e;
		TreePath tp = 
			tree.getPathForLocation( 
					me.getX(), 
						me.getY() );
		if ( tp != null ) {
			// Can't drag the root node
			GrammarNodeTreeNode node = ( GrammarNodeTreeNode )tp.getLastPathComponent(); 
			
			if ( node.getUserObject() == null ) {
			
				if ( node.getParent() != null ) {
					
					GrammarNode source = node.getSource();
					if ( source instanceof GrammarContainer ) {
						return;
					}
					
					dragNode = node;
					super.exportAsDrag(comp, e, action);
				}
			
			}
		}
	}

	@Override
	public boolean canImport(
			JComponent comp, DataFlavor[] transferFlavors ) {
		return false;
	}

	@Override
	protected Transferable createTransferable( JComponent c ) {
		if ( dragNode != null )
			return new StringSelection( "xpath:" + dragNode.toXPath() );
		else
			return super.createTransferable( c );
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

}

