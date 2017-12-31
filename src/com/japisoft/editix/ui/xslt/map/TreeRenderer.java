package com.japisoft.editix.ui.xslt.map;

import java.awt.Color;
import java.awt.Component;

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
public class TreeRenderer extends FastLabel implements TreeCellRenderer {
	
	public TreeRenderer() {
		setForeground( UIManager.getColor( "Tree.foreground" ) );
		setBackground( UIManager.getColor( "Tree.background" ) );
		setFont( UIManager.getFont( "Tree.font" ) );
		setUnderlineColor( Color.BLUE );
		setIcon( new ImageIcon( getClass().getResource( "element.png" ) ) );
	}

	public Component getTreeCellRendererComponent(
		JTree tree, 
		Object value,
		boolean selected, 
		boolean expanded, 
		boolean leaf, 
		int row,
		boolean hasFocus) {

		setUnderlineMode( selected );		
		String tmp = value.toString();
		if ( value instanceof VirtualDomNode ) {
			VirtualDomNode vdm = ( VirtualDomNode )value;
			if ( vdm.getSource() instanceof Element ) {
				Element e = ( Element )vdm.getSource();
				if ( e.hasAttribute( "match" ) ) {
					tmp += " [" + e.getAttribute( "match" ) + "]";
				} else
				if ( e.hasAttribute( "name" ) ) {
					tmp += " [" + e.getAttribute( "name" ) + "]";
				} else
				if ( e.hasAttribute( "select" ) )
					tmp += " [" + e.getAttribute( "select" ) + "]";
			}
		}
		setText( tmp );

		return this;
	}

}
