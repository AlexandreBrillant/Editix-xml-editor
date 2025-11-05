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

package com.japisoft.xmlform.designer.properties;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

public class PropertiesEditor implements TableCellEditor, PropertyEditorListener {

	private PropertyEditor currentEditor = null;
	
	public Component getTableCellEditorComponent(
			JTable table, 
			Object value,
			boolean isSelected, 
			int row, 
			int column ) {

		currentEditor = 
			PropertyEditorFactory.getEditor( 
					( ( PropertiesModel )table.getModel() ).getPropertyAt( row ) );

		currentEditor.setPropertyEditorListener( this );
		currentEditor.setSelected( isSelected );
		
		return currentEditor.getView();		

	}
	
	private CellEditorListener l = null;

	public void addCellEditorListener( CellEditorListener l ) {
		this.l = l;
	}

	public void cancelCellEditing() {
	}

	public Object getCellEditorValue() {
		return currentEditor.getValue();
	}

	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	public void removeCellEditorListener(CellEditorListener l) {
		this.l = null;
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	public boolean stopCellEditing() {
		return true;
	}

	// ----------------------------------------------------------------------

	public void cancel() {
		this.l.editingCanceled( 
				new ChangeEvent( this ) );
	}

	public void stop() {
		this.l.editingStopped( 
				new ChangeEvent( this ) );
	}

}

