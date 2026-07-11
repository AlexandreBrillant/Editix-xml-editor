// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.xmlform.designer.properties.editors;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class DictionnaryPanel extends JPanel {

	public DictionnaryPanel() {
		initUI();
	}

	private JTable table = null;
	
	private void initUI() {
		setLayout( new BorderLayout() );
		add( new JScrollPane( table = new JTable() ) );
	}

	public void stopEditing() {
		if ( table.getCellEditor() != null )
			table.getCellEditor().stopCellEditing();
	}

	public void init( HashMap<String,String> map ) {
		table.setModel( new CustomTableModel( map ) );
	}

	class CustomTableModel implements TableModel {

		private HashMap<String,String> map = null;
		private List<String> lst = null;
		
		CustomTableModel( HashMap<String,String> map ) {
			this.map = map;
			this.lst = new ArrayList<String>();
			for ( String key : map.keySet() ) {
				lst.add( key );
			}
			Collections.sort( lst );
		}

		public void addTableModelListener(TableModelListener l) {
		}

		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int columnIndex) {
			if ( columnIndex == 0 )
				return "Value";
			else
				return "Visible value";
		}

		public int getRowCount() {
			return lst.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if ( columnIndex == 0 ) {
				return lst.get( rowIndex );
			} else {
				return map.get( lst.get( rowIndex ) );
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if ( columnIndex == 1 )
				return true;
			return false;
		}

		public void removeTableModelListener( TableModelListener l ) {
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if ( columnIndex == 1 ) {
				
				// Reset to the default value
				if ( "".equals( aValue ) )
					aValue = lst.get( rowIndex );

				map.put( 
					lst.get( rowIndex ), 
					( String )aValue );
			}
		}

	}
	
}
