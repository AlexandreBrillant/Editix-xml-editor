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

package com.japisoft.xmlform.designer.data;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.xml.grammar.GrammarNode;

public class DataTreeRenderer implements TreeCellRenderer {

	private BoundImageIcon elementOpened = null;
	private BoundImageIcon elementClosed = null;	
	private BoundImageIcon text = null;	
	private FastLabel label = null;

	public DataTreeRenderer() {
		elementOpened = 
			new BoundImageIcon( Toolkit.getImageIcon( 
					"images/folder.png" ) );
		elementClosed = 
			new BoundImageIcon( Toolkit.getImageIcon( 
					"images/folder_closed.png" ) );		
		text = 
			new BoundImageIcon( Toolkit.getImageIcon( 
					"images/text.png" ) );
		label = 
			new FastLabel();
		label.setOpaque( true );
	}

	public Component getTreeCellRendererComponent(
			JTree tree, 
			Object value,
			boolean selected, 
			boolean expanded, 
			boolean leaf, 
			int row,
			boolean hasFocus ) {
		
		if ( value instanceof GrammarNodeTreeNode ) {
			GrammarNodeTreeNode node = ( GrammarNodeTreeNode )value;
			GrammarNode gn = node.getSource();
			
			elementOpened.setBounded( node.getUserObject() != null );
			elementClosed.setBounded( node.getUserObject() != null );
			text.setBounded( node.getUserObject() != null );
			
			if ( "complex".equals( gn.getType().getType() ) ) {
				if ( expanded )
					label.setIcon( elementOpened );
				else
					label.setIcon( elementClosed );
			} else
				label.setIcon( text );
			label.setText( node.toString() );
			
			if ( !selected ) {
				label.setBackground( tree.getBackground() );
				label.setForeground( tree.getForeground() );
			} else {
				label.setBackground( UIManager.getColor( "Tree.selectionBackground" ) );
				label.setForeground( UIManager.getColor( "Tree.selectionForeground" ) );
			}
			
		} else {
			label.setText( null );
		}

		return label;
	}

	class BoundImageIcon implements Icon {

		private Icon ref = null;
		
		BoundImageIcon( Icon ref ) {
			this.ref = ref;
		}
		
		public int getIconHeight() {
			return ref.getIconHeight();
		}

		public int getIconWidth() {
			return ref.getIconWidth();
		}

		boolean bounded = false;
		
		public void setBounded( boolean bounded ) {
			this.bounded = bounded;
		}
		
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			ref.paintIcon( c, g, x, y );
			if ( bounded ) {
				g.setColor( Color.DARK_GRAY );
				g.fill3DRect( 2, 2, 4, 4, true );
			}
		}

	}

}
