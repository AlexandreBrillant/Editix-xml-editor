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

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
