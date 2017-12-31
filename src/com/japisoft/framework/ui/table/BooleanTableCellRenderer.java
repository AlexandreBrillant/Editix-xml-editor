package com.japisoft.framework.ui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

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
public class BooleanTableCellRenderer implements TableCellRenderer {

	private static JCheckBox cb = new JCheckBox();		

	public BooleanTableCellRenderer() {
		super();
		cb.setHorizontalAlignment( SwingConstants.CENTER );
	}
	
	private Color background, foreground, background2;

	public BooleanTableCellRenderer( Color background, Color foreground ) {
		this();
		this.background = background;
		this.foreground = foreground;
		this.background2 = this.background;
	}

	public BooleanTableCellRenderer( Color background, Color background2, Color foreground ) {
		this( background, foreground );
		this.background2 = background2;
	}

	public Component getTableCellRendererComponent(
			JTable table, 
			Object value,
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column ) {
		
		if ( isSelected ) {
			cb.setBackground( table.getSelectionBackground() );
			cb.setForeground( table.getSelectionForeground() );
		} else {
			
			if ( background != null ) {
				if ( row % 2 == 0 )
					cb.setBackground( background2 );
				else
					cb.setBackground( background );
			} else {
				cb.setBackground( background == null ? table.getBackground() : background );
				cb.setForeground( foreground == null ? table.getForeground() : foreground );
			}
		}

		if ( table.getModel() instanceof EnableTableModel ) {
			boolean enabled =
				( ( EnableTableModel )table.getModel() ).isEnabled( row );
			if ( !enabled ) {
				if ( !isSelected )
					cb.setBackground( SharedProperties.DISABLED_BGCOLOR );
			}
		}

		if ( table.getModel() instanceof ErrorTableModel ) {
			boolean error = 
				( ( ErrorTableModel )table.getModel() ).hasError( row );
			if ( error ) {
				if ( !isSelected )
					cb.setBackground( SharedProperties.ERROR_BGCOLOR );
			}
		}
		
		cb.setSelected( ( ( Boolean )value ).booleanValue() );
		return cb;
	}	

}
