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

import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class PropertiesModel implements TableModel {

	private List<PropertyDescriptor> data = null;
	
	public void init( List<PropertyDescriptor> descriptors ) {
		this.data = descriptors;
		l.tableChanged( new TableModelEvent( this ) );
	}

	private TableModelListener l;

	public void addTableModelListener(TableModelListener l) {
		this.l = l;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int columnIndex) {
		if ( columnIndex == 0 )
			return "Name";
		return "Value";
	}

	public int getRowCount() {
		if ( data == null )
			return 0;
		else
			return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		PropertyDescriptor pd = 
			data.get( rowIndex );
		if ( columnIndex == 0 )
			return pd.getName();
		else
			return pd.getValue();
	}
	
	public PropertyDescriptor getPropertyAt( int rowIndex ) {
		return data.get( rowIndex );
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if ( columnIndex == 0 )
			return false;
		return true;
	}

	public void removeTableModelListener(TableModelListener l) {
		this.l = null;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		PropertyDescriptor pd = 
			data.get( rowIndex );
		if ( columnIndex == 1 )
			pd.setValue( value );		
	}

}

