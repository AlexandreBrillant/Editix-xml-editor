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

package com.japisoft.editix.ui.panels.diff;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class CustomTreeRenderer implements TreeCellRenderer {

	private JLabel lbl = new JLabel();

	public CustomTreeRenderer() {
		lbl.setOpaque( true );
	}
	
	public static int guessType( FPNode node ) {
		int type = -1;
		if ( "true".equals( node.getAttribute( "dfx:insert" ) ) )
			type = DiffResourceFactory.ADD_TYPE;
		else
		if ( "true".equals( node.getAttribute( "dfx:delete" ) ) ) {
			type = DiffResourceFactory.REMOVE_TYPE;
		}

		for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
			if ( node.getViewAttributeAt( i ).startsWith( "del:" ) ) {
				type = DiffResourceFactory.ATT_TYPE;
				break;
			}
		}
		return type;
	}
	
	public Component getTreeCellRendererComponent(
			JTree tree, 
			Object value,
			boolean selected, 
			boolean expanded, 
			boolean leaf, 
			int row,
			boolean hasFocus ) {

		if ( selected ) {
			lbl.setForeground( 
					UIManager.getColor( 
							"Tree.selectionForeground" ) );
			lbl.setBackground( 
					UIManager.getColor( 
							"Tree.selectionBackground" ) );
		} else {
			lbl.setBackground( tree.getBackground() );
			lbl.setForeground( tree.getForeground() );
		}

		if ( value instanceof FPNode ) {
			FPNode node = ( FPNode )value;
			
			int type = guessType( node );

			lbl.setIcon( DiffResourceFactory.getIconForType( type ) );
			lbl.setIcon( DiffResourceFactory.getIconForType( type ) );				

			lbl.setBackground( DiffResourceFactory.getBgColorForType( type ) );
			lbl.setText( node.getContent() );
			
			if ( selected ) {
				lbl.setBackground( UIManager.getColor( "Tree.selectionBackground" ) );
				lbl.setForeground( UIManager.getColor( "Tree.selectionForeground" ) );				
			} else
				lbl.setForeground( tree.getForeground() );
			
		}

		return lbl;
	}
	
}

