package com.japisoft.xmlpad.helper.model;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

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
public class CommonDescriptorRenderer extends DefaultListCellRenderer {

	private Icon defIcon;

	private CommonDescriptorRenderer() {
		super();
		defIcon = UIManager.getIcon("xmlpad.helper.icon");
		if ( defIcon == null )
			defIcon = new ImageIcon( getClass().getResource( "element.png" ) );
	}

	private static CommonDescriptorRenderer RENDERER = null;

	public static CommonDescriptorRenderer getRenderer() {
		if ( RENDERER == null )
			RENDERER = new CommonDescriptorRenderer();
		return RENDERER;
	}

	Font f = null;

	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus) {
		JLabel c =
			( JLabel )super.getListCellRendererComponent(
				list,
				value,
				index,
				isSelected,
				cellHasFocus );

		Icon icon = null;		
		
		if ( ( value != null ) && ( value instanceof Descriptor ) ) {
			Descriptor d = ( Descriptor )value;
			c.setEnabled( d.isEnabled() );
			c.setText( d.getNameForHelper() );
			if ( d.getColor() != null )
				c.setForeground( 
						d.getColor() );
			else {
				if ( isSelected )
					c.setForeground( 
							list.getSelectionForeground() );
				else
					c.setForeground( 
							list.getForeground() );
			}
			icon = d.getIcon();
			if ( icon == null )
				icon = defIcon;
		}

		if (f == null)
			c.setFont(
				f = new Font( 
					getFont().getName(), 
					0, 
					getFont().getSize() ) );
		else
			c.setFont( f );

		if ( icon != null )
			c.setIcon( icon );

		return c;
	}

}
