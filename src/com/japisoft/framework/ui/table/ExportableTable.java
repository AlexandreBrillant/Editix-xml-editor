package com.japisoft.framework.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.spreadsheet.Spreadsheet;
import com.japisoft.framework.spreadsheet.SpreadsheetFactory;
import com.japisoft.framework.ui.toolkit.FileManager;

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
				
				try {
					Spreadsheet spreadsheet = SpreadsheetFactory.getInstance().getSpreadsheet( selectedFile );
					spreadsheet.reset( getModel() );
					spreadsheet.write( new FileOutputStream( selectedFile ));
				} catch( Exception exc ) {
					ApplicationModel.fireApplicationValue( "error", exc.getMessage() );
				}
				
			}
		}
	}

}
