package com.japisoft.framework.ui.list;

import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

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
public class FileListCellRenderer implements ListCellRenderer {

	private JLabel lbl = new JLabel();
	
	public FileListCellRenderer() {
		lbl.setIcon(
				new ImageIcon(
						ClassLoader.getSystemResource( "images/document_plain.png" ) ) );
		lbl.setOpaque( true );
	}

	public Component getListCellRendererComponent(
			JList list, 
			Object value,
			int index, 
			boolean isSelected, 
			boolean cellHasFocus ) {
		
		if ( isSelected ) {
			lbl.setForeground( list.getSelectionForeground() );
			lbl.setBackground( list.getSelectionBackground() );
		} else {
			lbl.setForeground( list.getForeground() );
			lbl.setBackground( list.getBackground() );
		}
		
		File f = ( File )value;
		lbl.setText( f.toString() + " (" + f.length() + " bytes)" );

		return lbl;
	}

}
