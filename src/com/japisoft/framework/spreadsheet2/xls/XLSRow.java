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

package com.japisoft.framework.spreadsheet2.xls;

import org.apache.poi.ss.usermodel.Cell;

import com.japisoft.framework.spreadsheet2.Col;
import com.japisoft.framework.spreadsheet2.Row;
import com.japisoft.framework.spreadsheet2.xls.XLSCol;

public class XLSRow implements Row {

	private XLSCol[] cols = null;
	
	XLSRow( org.apache.poi.ss.usermodel.Row r ) {
		int firstCell = r.getFirstCellNum();
		int lastCell = r.getLastCellNum();
		cols = new XLSCol[ ( lastCell - firstCell ) + 1 ];
		for ( int i = firstCell; i <= lastCell; i++ ) {
			Cell c = r.getCell( i );
			if ( c != null )
				cols[ i ] = new XLSCol( c );
		}
	}
	
	@Override
	public int getColCount() {
		return cols.length;
	}

	@Override
	public Col getCol(int index) {
		return cols[ index - 1 ];
	}
	
}

