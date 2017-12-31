package com.japisoft.framework.spreadsheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
