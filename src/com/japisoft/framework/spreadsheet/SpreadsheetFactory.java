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

import java.io.File;
import java.util.Map;

public class SpreadsheetFactory {

	private static SpreadsheetFactory INSTANCE = null;
	private Map<String,Spreadsheet> model = null;

	private SpreadsheetFactory() {
	}

	public static SpreadsheetFactory getInstance() {
		if ( INSTANCE == null )
			INSTANCE = new SpreadsheetFactory();
		return INSTANCE;
	}

	public Spreadsheet getSpreadsheet( File fileName ) throws Exception {
		String lastPart = fileName.getName().toLowerCase();
		int i = lastPart.lastIndexOf( "." );
		if ( i > -1 ) {
			String ext = lastPart.substring( i + 1 );
			
			if ( "csv".equals( ext ) )
				return new CsvSpreadsheet();
			else
			if ( "xls".equals( ext ) )
				return new XlsSpreadsheet();
			else
			if ( "xlsx".equals( ext ) )
				return new XlsxSpreadsheet();
			else
				throw new Exception( "Unknown file format" );			
		} else {
			throw new Exception( "Unknown file format" );
		}
	}

}
