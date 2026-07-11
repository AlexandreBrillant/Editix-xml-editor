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

package com.japisoft.framework.spreadsheet2.xls;

import java.util.ArrayList;
import java.util.List;

import com.japisoft.framework.spreadsheet2.Row;
import com.japisoft.framework.spreadsheet2.Sheet;
import com.japisoft.framework.spreadsheet2.SpreadSheetConf;
import com.japisoft.framework.spreadsheet2.xls.XLSRow;

public class XLSSheet implements Sheet {

	private String name;
	
	XLSSheet( SpreadSheetConf conf, org.apache.poi.ss.usermodel.Sheet s  ) {
		name = s.getSheetName();
		for ( int i = 0; i <= s.getLastRowNum(); i++ ) {
			org.apache.poi.ss.usermodel.Row r = s.getRow( i );
			addRow( r );
		}
	}
		
	private List<XLSRow> rows = null; 
			
	private void addRow( org.apache.poi.ss.usermodel.Row r ) {
		if ( rows == null )
			rows = new ArrayList<XLSRow>();
		rows.add( new XLSRow( r ) );
	}
	
	@Override
	public int getRowCount() {
		if ( rows == null )
			return 0;
		return rows.size();
	}
	
	@Override
	public Row getRow(int index) {
		return rows.get( index - 1 );
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
