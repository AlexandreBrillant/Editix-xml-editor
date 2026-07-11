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

package com.japisoft.framework.spreadsheet2;

import com.japisoft.framework.spreadsheet2.SpreadSheet;
import com.japisoft.framework.spreadsheet2.SpreadSheetConf;
import com.japisoft.framework.spreadsheet2.SpreadSheetFactory;
import com.japisoft.framework.spreadsheet2.csv.CSVSpreadSheet;
import com.japisoft.framework.spreadsheet2.txt.TXTSpreadSheet;
import com.japisoft.framework.spreadsheet2.xls.XLSSpreadSheet;
import com.japisoft.framework.toolkit.FileToolkit;

public class SpreadSheetFactory {

	private static SpreadSheetFactory factory = null;
	
	private SpreadSheetFactory() {
		super();
	}
	
	public static SpreadSheetFactory instance() {
		if ( factory == null )
			factory = new SpreadSheetFactory();
		return factory;
	}
	
	public SpreadSheet getSpreadSheet( SpreadSheetConf conf, String f ) throws Exception {
		
		if ( FileToolkit.matchExt( f, "csv" ) )
			return new CSVSpreadSheet( conf, f );
		if ( FileToolkit.matchExt( f, "xls" ) || FileToolkit.matchExt( f, "xlsx" ) )
			return new XLSSpreadSheet( conf, f );
		return new TXTSpreadSheet( conf, f );
	}
	
}
