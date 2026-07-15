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

package com.japisoft.editix.action.xml;

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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import com.japisoft.editix.main.steps.EditixEntityResolver;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixDialog;
import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.framework.dialog.actions.OKAction;
import com.japisoft.framework.ui.table.ExportableTable;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class XMLCatalogDialog extends EditixDialog {

	public XMLCatalogDialog() {
		super(	"XML Catalog", 
				"XML Catalog", 
				"Use your OASIS XML Catalogs for Parsing...\nNote that if you modify a catalog you must reload EditiX" );
		initUI();
	}

	DefaultTableModel tableModel = null;
	JTable tb;

	private void initUI() {
		tb = new ExportableTable( 
				tableModel = new DefaultTableModel( 
						new String[] { "Catalog path" }, 0 ) );
		getContentPane().add( new JScrollPane( tb ) );
		JToolBar bar = new JToolBar();
		bar.setFloatable( false );
		bar.add( new AddAction() );
		bar.add( new RemoveAction() );
		bar.addSeparator();
		bar.add( new EditAction() );
		getContentPane().add( bar, BorderLayout.SOUTH );
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
				EditixEntityResolver.getInstance().loadCatalogs();				
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
			JFileChooser chooser = EditixFactory.buildFileChooserForDocumentType( "XML" );
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

	class EditAction extends AbstractAction {
		public EditAction() {
			putValue( Action.NAME, "Edit" );
		}

		public void actionPerformed(ActionEvent e) {
			int row = tb.getSelectedRow();
			if ( row > -1 ) {
				String loc = ( String )tableModel.getValueAt( row, 0 );
				ActionModel.activeActionById( 
						ActionModel.OPEN, e, 
						loc, 
						"XML" 
				);
			}
		}
	}
}
