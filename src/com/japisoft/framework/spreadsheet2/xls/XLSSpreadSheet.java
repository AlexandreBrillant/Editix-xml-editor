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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.japisoft.framework.spreadsheet2.Sheet;
import com.japisoft.framework.spreadsheet2.SpreadSheet;
import com.japisoft.framework.spreadsheet2.SpreadSheetConf;
import com.japisoft.framework.spreadsheet2.xls.XLSSheet;
import com.japisoft.framework.toolkit.FileToolkit;

public class XLSSpreadSheet implements SpreadSheet {
	
	private List<Sheet> l = null;
	
	public XLSSpreadSheet( SpreadSheetConf conf, String f ) throws Exception {
		process( conf, WorkbookFactory.create( FileToolkit.inputStreamFromURI( f ) ) );		
	}
	
	private void process( SpreadSheetConf conf, Workbook w ) {
		for ( int i = 0; i < w.getNumberOfSheets(); i++ ) {
			org.apache.poi.ss.usermodel.Sheet s = w.getSheetAt( i );
			if ( l == null )
				l = new ArrayList<Sheet>();
			l.add( new XLSSheet( conf, s ) );
		}
	}
	
	@Override
	public int getSheetCount() {
		if ( l == null )
			return 0;
		return l.size();
	}

	@Override
	public Sheet getSheet(int index) {
		return l.get( index - 1 );
	}
	
}
