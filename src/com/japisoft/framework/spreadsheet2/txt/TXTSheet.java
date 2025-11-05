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

package com.japisoft.framework.spreadsheet2.txt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.japisoft.framework.spreadsheet2.Row;
import com.japisoft.framework.spreadsheet2.Sheet;
import com.japisoft.framework.spreadsheet2.SpreadSheetConf;
import com.japisoft.framework.spreadsheet2.txt.TXTRow;
import com.japisoft.framework.toolkit.FileToolkit;

public class TXTSheet implements Sheet {

	TXTSheet( SpreadSheetConf conf, String f ) throws Exception {

		Reader r = null;
		r = new InputStreamReader( FileToolkit.inputStreamFromURI( f ), conf.getEncoding() );
		
		BufferedReader br = new BufferedReader( r );
				;
		try {
			String l = null;
			while ( ( l = br.readLine() ) != null ) {
				scanRow( conf, l );
			}
		} finally {
			br.close();
		}
	}
	
	private void scanRow( SpreadSheetConf conf, String l ) {
		l = l.trim();
		if (l.length() > 0 ) {
			String[] cols = new String[] { l };
			addRow( cols );
		}
	}
	
	private List<TXTRow> rows = null; 
			
	private void addRow( String[] cols ) {
		if ( rows == null )
			rows = new ArrayList<TXTRow>();
		rows.add( new TXTRow( cols ) );
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
		return null;
	}
	
}

