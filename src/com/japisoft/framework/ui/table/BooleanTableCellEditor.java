package com.japisoft.framework.ui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

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
public class BooleanTableCellEditor extends AbstractCellEditor implements
		ActionListener, TableCellEditor {

	private TableModel model;
	private JCheckBox cb = new JCheckBox();
	
	public BooleanTableCellEditor( TableModel model ) {
		this.model = model;
		cb.setHorizontalAlignment( SwingConstants.CENTER );
	}

	public void addCellEditorListener( CellEditorListener l ) {
		super.addCellEditorListener( l );
		cb.addActionListener( this );
	}

	public void removeCellEditorListener( CellEditorListener l ) {
		super.removeCellEditorListener( l );
		cb.removeActionListener( this );
	}

	public Object getCellEditorValue() {
		return new Boolean( cb.isSelected() );
	}

	public boolean isCellEditable( EventObject e ) {
		return true;
	}

	private int lastEditedRow = -1;

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		cb.setSelected( ( ( Boolean ) value ).booleanValue() );
		lastEditedRow = row;
		return cb;
	}

	public void actionPerformed(ActionEvent e) {
		model.setValueAt( getCellEditorValue(), lastEditedRow, 1 );
	}

}
