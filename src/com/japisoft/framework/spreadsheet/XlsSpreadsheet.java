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

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XlsSpreadsheet extends AbstractSpreadsheet {

	protected void read( Workbook wb ) {
		Sheet sh = wb.getSheetAt( 0 );
		for ( int i = 0; i <= sh.getLastRowNum(); i++ ) {
			Row row = sh.getRow( i );
			String[] content = new String[ row.getLastCellNum() - row.getFirstCellNum() ];
			int col = 0;
			for ( int j = row.getFirstCellNum(); j< row.getLastCellNum(); j++ ) {
				Cell c = row.getCell( j );
				String value = null;
				if ( c.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
					value = Double.toString( c.getNumericCellValue() );
				} else
				if ( c.getCellType() == Cell.CELL_TYPE_BOOLEAN ) {
					value = Boolean.toString( c.getBooleanCellValue() );
				} else
				if ( c.getCellType() == Cell.CELL_TYPE_FORMULA ) {
					value = c.getCellFormula();
				} else
					value = c.getStringCellValue();
				content[ col++ ] = value;
			}
			readLine( content );
		}
	}

	protected void write( Workbook wb ) {
		Sheet sh = wb.createSheet();
		Row header = sh.createRow( 0 );	// header
		for ( int i = 0; i < getColumnCount(); i++ ) {
			Cell cell = header.createCell( i );
			cell.setCellValue( getColumnName( i ) );
		}
		for ( int j = 0; j < getRowCount(); j++ ) {
			Row r = sh.createRow( j + 1 );
			for ( int i = 0; i < getColumnCount(); i++ ) {
				Cell cell = r.createCell( i );
				cell.setCellValue( getValueAt( j, i ) );
			}
		}
	}

	public void write( OutputStream output ) throws Exception {
		HSSFWorkbook wb = null;
		write( wb = new HSSFWorkbook() );
		wb.write( output );
	}
	
	public void read( InputStream input ) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook( input );
		read( wb );
	}

}
