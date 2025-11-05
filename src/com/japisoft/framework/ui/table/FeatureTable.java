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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/** A table displaying a set of values that user can enabled/disabled with checkbox */
public class FeatureTable extends JTable {

	private String featureType = null;
	private List<String> features = null;
	private Map<String,Boolean> enabled = null;
	
	/**
	 * @param features list of features
	 * @param featureType feature type name displayable
	 */
	public FeatureTable( List<String> features, String featureType ) {
		this.features = features;
		this.featureType = featureType;
		enabled = new HashMap<String, Boolean>();
		setModel( new FeatureTableModel() );
		getColumnModel().getColumn( 0 ).setMaxWidth( 30 );
	}

	public List<String> getSelectedFeatures() {
		List<String> r = new ArrayList<String>();
		TableModel tm = getModel();
		for ( int i = 0; i < tm.getRowCount(); i++ ) {
			if ( tm.getValueAt( i, 0 ) == Boolean.TRUE ) {
				r.add( ( String )tm.getValueAt( i, 1 ) );
			}
		}
		return r;
	}

	// ------------------------------------------------------------------

	class FeatureTableModel implements TableModel {
		
		
		public void addTableModelListener(TableModelListener l) {
		}

		public Class<?> getColumnClass(int columnIndex) {
			if ( columnIndex == 0 )
				return Boolean.class;
			return String.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int columnIndex) {
			if ( columnIndex == 0 )
				return "Ok";
			return featureType;
		}

		public int getRowCount() {
			return features.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if ( columnIndex == 0 ) {
				String featureName = features.get( rowIndex );
				return enabled.get( featureName );
			} else {
				return features.get( rowIndex );
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}

		public void removeTableModelListener(TableModelListener l) {
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			String featureName = features.get( rowIndex );
			enabled.put( featureName, ( Boolean )aValue );
		}

	}
	
	public static void main( String[] args ) {
		List<String> l = new ArrayList<String>();
		l.add( "Rouge" );
		l.add( "Vert" );
		l.add( "Bleu" );
		JFrame f = new JFrame();
		f.add( new FeatureTable( l, "Couleurs" ) );
		f.setSize( 300, 200 );
		f.setVisible( true );
	}

}

