package com.japisoft.editix.ui.panels.style;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.xml.parser.node.FPNode;

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
class StyleTreeRenderer implements TreeCellRenderer {
	FastLabel fastlabel = new FastLabel(false);
	Icon element = null;
	Icon elementAny = null;
	Icon folder = null;
	Icon folder_closed = null;
	Icon attribute = null;

	public StyleTreeRenderer() {
		try {
			element = new ImageIcon(ClassLoader
					.getSystemResource("images/element.png"));
			elementAny = new ImageIcon(ClassLoader
					.getSystemResource("images/element_refresh.png"));
			attribute = new ImageIcon(ClassLoader
					.getSystemResource("images/attribute.png"));
			folder = new ImageIcon(ClassLoader
					.getSystemResource("images/folder.png"));
			folder_closed = new ImageIcon(ClassLoader
					.getSystemResource("images/folder_closed.png"));
		} catch (Throwable th) {
			System.err.println("Can't init icons ? : " + th.getMessage());
		}
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Icon icon = null;
		if (!(value instanceof FPNode)) {
			fastlabel.setText("?");
			fastlabel.setIcon(null);
			return fastlabel;
		}
		FPNode node = (FPNode) value;
		String label;

		if (node.isAttribute()) {
			label = node.getNodeValue();
			icon = attribute;
		} else {
			if (node.matchContent("item")) {
				String name = node.getAttribute("name");
				label = name;
				icon = element;

				label = label + " (" + node.getAttribute( "param" ) + ")";

			} else {
				if (node.matchContent("group"))
					label = node.getAttribute("name");
				else {
					label = node.getContent();
				}
				icon = !expanded ? folder_closed : folder;
			}

		}

		fastlabel.setIcon(icon);
		fastlabel.setText(label);

		if (selected) {
			fastlabel.setForeground(UIManager
					.getColor("List.selectionForeground"));
			fastlabel.setBackground(UIManager
					.getColor("List.selectionBackground"));
		} else {
			Color foreground = tree.getForeground();
			Color background = tree.getBackground();
			
			if ( node.getApplicationObject() instanceof Color ) {
				background = ( ( Color )( node.getApplicationObject() ) );
			} else {
				String name = ( node.getAttribute( "name" ) );
				if ( name.indexOf( "color" ) > -1 ) {
					try {
						String colorStr = node.getAttribute( "param" );
						if ( colorStr.startsWith( "#" ) ) {
							background = new Color( Integer.parseInt( colorStr.substring( 1 ), 16 ) );
							node.setApplicationObject( background );
						} else {
							if ( colorStr.startsWith( "rgb(" ) ) {
								int j = colorStr.lastIndexOf( ")" );
								if ( j > -1 ) {
									String tmp = colorStr.substring( 4, j );
									String[] rgb = tmp.split( "," );
									if ( rgb.length == 3 ) {
										background = new Color( 
											Integer.parseInt( rgb[ 0 ] ), 
											Integer.parseInt( rgb[ 1 ] ), 
											Integer.parseInt( rgb[ 2 ] ) 
										);
										node.setApplicationObject( background );
									}
								}
							}
						}
					} catch( Throwable exc ) {
						node.setApplicationObject( background );
					}
				}
			}

			if ( Color.BLACK.equals( background ) ) {
				foreground = Color.WHITE;
			}
			
			fastlabel.setForeground(foreground);
			fastlabel.setBackground(background);
		}

		return fastlabel;
	}

}
