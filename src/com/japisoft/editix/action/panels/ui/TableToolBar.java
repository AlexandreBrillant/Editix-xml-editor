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

package com.japisoft.editix.action.panels.ui;

import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.JToolBar;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.Resource;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.spreadsheet.Spreadsheet;
import com.japisoft.framework.spreadsheet.SpreadsheetFactory;
import com.japisoft.framework.ui.toolkit.FileManager;

public class TableToolBar extends JToolBar {

	private JTable table;
	
	public TableToolBar( JTable table ) {
		this.table = table;
		add( new PrintAction() );
		add( new ExportAction() );
	}
	
	public class PrintAction extends AbstractAction {
	
		public PrintAction() {
			putValue( Action.SMALL_ICON, Resource.getImage( "images/printer.png") );
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			try {
				table.print();
			} catch( PrinterException exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't print : " + exc.getMessage() );
			}

		}
		
	}
	
	public class ExportAction extends AbstractAction {
		
		public ExportAction() {
			putValue( Action.SMALL_ICON, Resource.getImage( "images/tables.png") );
		}
		
		public void actionPerformed( ActionEvent evt ) {
			File selectedFile = FileManager.getSelectedFile(
					false, 
					new String[] { 
						"xlsx","xls","csv" 
					}, 
					new String[] { 
						"Microsoft Excel 2007 and later (*.xlsx)", "Microsoft Excel (*.xls)", "Comma-separated values File (*.csv)" 
					}
				);			
				
				if ( selectedFile != null ) {
					
					try {
						Spreadsheet spreadsheet = SpreadsheetFactory.getInstance().getSpreadsheet( selectedFile );
						spreadsheet.reset( table.getModel() );
						spreadsheet.write( new FileOutputStream( selectedFile ));
					} catch( Exception exc ) {
						ApplicationModel.fireApplicationValue( "error", exc.getMessage() );
					}
					
				}
			
		}
		
	}
	
}
