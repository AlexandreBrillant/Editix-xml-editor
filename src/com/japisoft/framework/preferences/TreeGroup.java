package com.japisoft.framework.preferences;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

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
public class TreeGroup extends JTree {
	
	public TreeGroup() {
		setCellRenderer( new CustomTreeCellRendererComponent() );
	}

	Icon i1 = new ImageIcon( ClassLoader.getSystemResource( "images/navigate_right.png" ) );
	Icon i2 = new ImageIcon( ClassLoader.getSystemResource( "images/navigate_right2.png" ) );
	Icon i3 = new ImageIcon( ClassLoader.getSystemResource( "images/pawn_glass_blue.png" ) );
	
	class CustomTreeCellRendererComponent extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(
				JTree tree, 
				Object value, 
				boolean selected, 
				boolean expanded, 
				boolean leaf, 
				int row, 
				boolean hasFocus) {
			Component c = super.getTreeCellRendererComponent(
					tree,
					value,
					selected,
					expanded,
					leaf,
					row,
					hasFocus );
			if ( c instanceof JLabel ) {
				if ( !leaf )
					( ( JLabel )c ).setIcon( i3 );
				else {
					if ( selected )
						( ( JLabel )c ).setIcon( i2 );
					else
						( ( JLabel )c ).setIcon( i1 );
				}
			}
			return c;
		}
	}
	
	

}
