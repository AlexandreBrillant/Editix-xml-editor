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

package com.japisoft.framework.application.descriptor.composer;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import org.w3c.dom.Element;
import com.japisoft.framework.ui.FastLabel;

public class DescriptorTreeRenderer implements TreeCellRenderer {

	private FastLabel fl = new FastLabel();

	private Map<String,Icon> icons = new HashMap<String,Icon>();

	public DescriptorTreeRenderer() {
		Icon container = new ImageIcon( getClass().getResource( "folder_cubes.png" ) );
		Icon item = new ImageIcon( getClass().getResource( "bullet_square_blue.png" ) );
		Icon separator = new ImageIcon( getClass().getResource( "navigate_minus.png" ) );
		Icon root = new ImageIcon( getClass().getResource( "environment2.png" ) );

		icons.put( "menuBar", container );
		icons.put( "menu", container );
		icons.put( "toolBar", container );
		icons.put( "popup", container );
		icons.put( "item", item );
		icons.put( "itemRef", item );
		icons.put( "separator", separator );
		icons.put( "editix", root );
	}

	public Component getTreeCellRendererComponent(
		JTree tree, 
		Object value,
		boolean selected, 
		boolean expanded, 
		boolean leaf, 
		int row,
		boolean hasFocus ) {
		
		if ( value instanceof Element ) {
		
			Element n = ( Element )value;
			fl.setText( n.getNodeName() + ( n.hasAttribute( "id" ) ? ( " [ " + n.getAttribute( "id" ) + " ]" ) : "" ) );
			fl.setIcon( icons.get( n.getNodeName() ) );

			fl.setBackground( tree.getBackground() );
			fl.setForeground( tree.getForeground() );
			
			if ( selected ) {
				fl.setBackground( UIManager.getColor( "Tree.selectionBackground" ) );
				fl.setForeground( UIManager.getColor( "Tree.selectionForeground" ) );
			}
			
		} else {
		
			fl.setText( value.toString() );	// ?
			
		}
		
		return fl;
	}

}

