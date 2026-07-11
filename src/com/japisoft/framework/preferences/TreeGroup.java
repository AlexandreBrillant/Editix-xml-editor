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

package com.japisoft.framework.preferences;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class TreeGroup extends JTree {
	
	Icon i1;
	Icon i2;
	Icon i3;
	
	public TreeGroup() {
		setCellRenderer( new CustomTreeCellRendererComponent() );
		
		i1 = UIManager.getIcon( "preferences.right" );
		i2 = UIManager.getIcon( "preferences.right2" );
		i3 = UIManager.getIcon( "preferences.pawn" );

		if ( i1 == null )
			i1 = new ImageIcon( ClassLoader.getSystemResource( "images/navigate_right.png" ) );	
		if ( i2 == null )
			i2 = new ImageIcon( ClassLoader.getSystemResource( "images/navigate_right2.png" ) );
		if ( i3 == null )
			i3 = new ImageIcon( ClassLoader.getSystemResource( "images/pawn_glass_blue.png" ) );
	}

	class CustomTreeCellRendererComponent extends JLabel implements TreeCellRenderer {
		
		public CustomTreeCellRendererComponent() {
			setOpaque( true );
		}
		
		public Component getTreeCellRendererComponent(
				JTree tree, 
				Object value, 
				boolean selected, 
				boolean expanded, 
				boolean leaf, 
				int row, 
				boolean hasFocus) {

			if ( !leaf )
				setIcon( i3 );
			else {
				if ( selected ) {
					setIcon( i2 );
				}
				else {
					setIcon( i1 );
				}
			}
			
			if ( selected ) {
				setBackground( UIManager.getColor( "Tree.selectionBackground" ) );
				setForeground( UIManager.getColor( "Tree.selectionForeground" ) );
			} else {
				setBackground( UIManager.getColor( "Tree.background" ) );
				setForeground( UIManager.getColor( "Tree.foreground" ) );				
			}
			
			setText( value.toString() );
			
			return this;
		}
	}
	
	

}
