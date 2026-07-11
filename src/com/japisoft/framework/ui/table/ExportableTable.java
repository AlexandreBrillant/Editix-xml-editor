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

package com.japisoft.framework.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.spreadsheet.Spreadsheet;
import com.japisoft.framework.spreadsheet.SpreadsheetFactory;
import com.japisoft.framework.ui.toolkit.FileManager;

public class ExportableTable extends JTable implements MouseListener {

	private JPopupMenu menu = new JPopupMenu();

	public ExportableTable() {
		super();
		initPopup();		
	}

	public ExportableTable(TableModel model) {
		super(model);
		initPopup();
	}

	private void initPopup() {
		menu.add( new ExportAction() );
		menu.add( new PrintAction() );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		addMouseListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeMouseListener( this );
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ( e .isPopupTrigger() ) {
			menu.show( e.getComponent(), e.getX(), e.getY() );
		}		
	}

	public void mouseReleased(MouseEvent e) {
		if ( e.isPopupTrigger() ) {
			menu.show( e.getComponent(), e.getX(), e.getY() );
		}				
	}

	private class PrintAction extends AbstractAction {
		public PrintAction() {
			putValue( Action.NAME, "Print..." );
			putValue( Action.LONG_DESCRIPTION, "Print this table" );
		}
		public void actionPerformed(ActionEvent e) {
			try {
			    boolean complete = print();
			    if (complete) {
			    	ApplicationModel.fireApplicationValue( "info", "Print completed" );
			    } else {
			    }
			} catch (PrinterException pe) {
				ApplicationModel.fireApplicationValue( "error", "Can't print : " + pe.getMessage() );
			}			
		}
	}
	
	private class ExportAction extends AbstractAction {
		public ExportAction() {
			putValue( Action.NAME, "Export..." );
			putValue( Action.LONG_DESCRIPTION, "Export this table" );
		}
		public void actionPerformed(ActionEvent arg0) {
			
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
				
				if ( FileManager.hasFileExt( selectedFile, "xlsx","xls","csv" ) ) {				
					try {
						Spreadsheet spreadsheet = SpreadsheetFactory.getInstance().getSpreadsheet( selectedFile );
						spreadsheet.reset( getModel() );
						spreadsheet.write( new FileOutputStream( selectedFile ));
					} catch( Exception exc ) {
						ApplicationModel.fireApplicationValue( "error", exc.getMessage() );
					}
				} else {
					try {
						BufferedWriter bw = new BufferedWriter( 
							new FileWriter( selectedFile ) 
						);
						try {
							int row = getModel().getRowCount();
							int col = getModel().getColumnCount();
							for ( int i = 0; i < row; i++ ) {
								if ( i > 0 )
									bw.newLine();
								for ( int j = 0; j < col; j++ ) {
									Object value = getModel().getValueAt( i, j );
									if ( value != null ) {
										if ( j > 0 )
											bw.write( "\t" );
										bw.write( value.toString() );
									}
								}
							}
						} finally {
							try {
								bw.close();
							} catch( Exception exc ) {
							}
						}
					} catch( IOException exc ) {

					}
				}

			}
		}
	}

}
