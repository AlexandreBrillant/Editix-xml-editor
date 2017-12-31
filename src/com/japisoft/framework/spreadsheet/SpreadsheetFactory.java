package com.japisoft.framework.spreadsheet;

import java.io.File;
import java.util.Map;

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
