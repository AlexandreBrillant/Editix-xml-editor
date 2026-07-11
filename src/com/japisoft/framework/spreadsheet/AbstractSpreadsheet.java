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

package com.japisoft.framework.spreadsheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.TableModel;

/**
 * Common Spreadsheet
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public abstract class AbstractSpreadsheet implements Spreadsheet {

	private List<String> columns = null;
	
	public int getColumnCount() {
		if ( delegate != null )
			return delegate.getColumnCount();
		if ( columns == null )
			return 0;
		return columns.size();
	}

	protected void addColumn( String name ) {
		if ( columns == null )
			columns = new ArrayList<String>();
		columns.add( name );
	}
	
	public String getColumnName(int columnIndex) {
		if ( delegate != null )
			return delegate.getColumnName( columnIndex );
		return columns.get( columnIndex );
	}

	private TableModel delegate = null;
	
	public void reset( TableModel tm ) {
		this.delegate = tm;
	}
	
	protected void setColumns( String[] content ) {
		for ( int i = 0;i < content.length; i++ ) {
			addColumn( content[ i ] );
		}
	}
	
	private List<List<String>> rows = null;


	private boolean isEmptyLine( String[] content ) {
		for ( int i = 0; i < content.length; i++ ) {
			if ( !"".equals( content[ i ] ) ) {
				return false;
			}
		}
		return true;
	}	

	private boolean checkHeader = false;

	protected void readLine( String[] content ) {
		if ( !isEmptyLine( content ) ) {
			if ( !checkHeader ) {
				int location = -1;
				if ( location == -1 ) {
					// First line for the header
					setColumns( content );
				} else {
					// No header, overwrite it
					String[] tmp = new String[ content.length ];
					for ( int i = 0; i < content.length; i++ ) {
						tmp[ i ] = "Data " + ( i + 1 );
					}
					setColumns( tmp );
					addRow( content );
				}
				checkHeader = true;
			} else {
				addRow( content );
			}
		}
	}
	
	public int getRowCount() {
		if ( delegate != null )
			return delegate.getRowCount();
		if ( rows == null )
			return 0;
		return rows.size();
	}

	public String getValueAt(int rowIndex, int columnIndex) {
		if ( delegate != null ) {
			Object obj = delegate.getValueAt( rowIndex, columnIndex );
			if ( obj == null )
				return null;
			return obj.toString();
		}
		List<String> m = rows.get( rowIndex );
		if ( m == null || m.size() <= columnIndex ) {
			return null;
		}
		return m.get( columnIndex );
	}

	public void setValueAt( int rowIndex, int columnIndex, String value ) {
		List<String> m = rows.get( rowIndex );
		if ( m == null ) {
			rows.set( rowIndex, m = new ArrayList<String>() );
		}
		m.set( columnIndex, value );
	}
	
	private void addRow( String[] content ) {
		if ( rows == null )
			rows =new ArrayList<List<String>>();
		rows.add( Arrays.asList( content ) );
	}

}
