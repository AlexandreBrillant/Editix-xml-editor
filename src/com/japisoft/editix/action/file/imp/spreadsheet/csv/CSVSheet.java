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

package com.japisoft.editix.action.file.imp.spreadsheet.csv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.japisoft.editix.action.file.imp.spreadsheet.Row;
import com.japisoft.editix.action.file.imp.spreadsheet.Sheet;
import com.japisoft.editix.action.file.imp.spreadsheet.SpreadSheetConfPanel;

public class CSVSheet implements Sheet {

	CSVSheet( SpreadSheetConfPanel conf, File f ) throws Exception {

		Reader r = null;
		if ( "DEFAULT".equalsIgnoreCase( conf.getEncoding() ) )
			r = new FileReader( f );
		else
			r = new InputStreamReader( new FileInputStream( f ), conf.getEncoding() );
		
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
	
	private void scanRow( SpreadSheetConfPanel conf, String l ) {
		String sep = conf.getColSep();
		l = l.trim();

		String esep = sep;
		
		if ( l.startsWith( "\"" ) &&
			l.endsWith( "\"" ) ) {
			esep = "\"" + sep + "\"";

			// Remove the first and the last string quote
			l = l.substring( 1 );
			l = l.substring( 0, l.length() - 1 );
		}
		
		String[] cols = l.split( esep );
		addRow( cols );
	}
	
	private List<CSVRow> rows = null; 
			
	private void addRow( String[] cols ) {
		if ( rows == null )
			rows = new ArrayList<CSVRow>();
		rows.add( new CSVRow( cols ) );
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
