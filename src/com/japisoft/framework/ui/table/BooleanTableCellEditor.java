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
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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
