package com.japisoft.framework.xml.action;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.BasicOKCancelDialogComponent;
import com.japisoft.framework.dialog.actions.OKAction;
import com.japisoft.framework.ui.SimpleFileFilter;

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
public class XMLCatalogDialog extends BasicOKCancelDialogComponent {

	public XMLCatalogDialog() {
		super(	
				ApplicationModel.MAIN_FRAME,
				"XML Catalog", 
				"XML Catalog", 
				"Use your OASIS XML Catalogs for Parsing...\nNote that if you modify a catalog you must reload " + ApplicationModel.SHORT_APPNAME,
				null );
		initUI();
	}

	DefaultTableModel tableModel = null;
	JTable tb;

	private void initUI() {
		tb = new JTable( 
				tableModel = new DefaultTableModel( 
						new String[] { "Catalog path" }, 0 ) );
		
		JPanel panel = new JPanel();
		panel.setLayout( new BorderLayout() );

		panel.add( new JScrollPane( tb ) );
		JToolBar bar = new JToolBar();
		bar.setFloatable( false );
		bar.add( new AddAction() );
		bar.add( new RemoveAction() );
		panel.add( bar, BorderLayout.SOUTH );
		
		setUI( panel );
	}

	protected Dimension getDefaultSize() { 
		return new Dimension( 500, 400 );
	}

	protected void beforeClosing() {
		super.beforeClosing();
		if ( getLastAction() == OKAction.ID ) {
			// Save it
			File catalog = XMLCatalogAction.getCatalogLstPath();
			try {
				BufferedWriter bw = new BufferedWriter( new FileWriter( catalog ) );
				try {
					for ( int i = 0; i < tableModel.getRowCount(); i++ ) {
						bw.write( (String)tableModel.getValueAt( i, 0 ) );
						bw.newLine();
					}
				} finally {
					bw.close();
				}
				// Reload
				CustomEntityResolver.getInstance().loadCatalogs();				
			} catch( IOException exc ) {
			}
		}
	}

	protected void beforeShowing() {
		super.beforeShowing();
		File catalog = XMLCatalogAction.getCatalogLstPath();;
		if ( catalog.exists() ) {
			try {
				BufferedReader reader = new BufferedReader( 
						new FileReader( catalog ) );
				try {
					String line = null;
					while ( ( line = reader.readLine() ) != null ) {
						tableModel.addRow( new Object[] { line } );
					}
				} finally {
					reader.close();
				}
			} catch( IOException exc ) {}
		}
	}

	//////////////////////////

	class AddAction extends AbstractAction {
		public AddAction() {
			putValue( Action.NAME, "Add" );
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(
					new SimpleFileFilter( "XML catalog", "xml" ) );
			if ( chooser.showOpenDialog( XMLCatalogDialog.this ) == JFileChooser.APPROVE_OPTION ) {
				tableModel.addRow( new Object[] { chooser.getSelectedFile().toString() } );
			}
		}
	}

	class RemoveAction extends AbstractAction {
		public RemoveAction() {
			putValue( Action.NAME, "Remove" );
		}

		public void actionPerformed(ActionEvent e) {
			int row = tb.getSelectedRow();
			if ( row > -1 )
				tableModel.removeRow( row );
		}
	}

}
