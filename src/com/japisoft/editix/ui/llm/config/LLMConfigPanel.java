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

package com.japisoft.editix.ui.llm.config;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import javax.swing.table.TableModel;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;

import net.miginfocom.swing.MigLayout;

public class LLMConfigPanel extends JPanel implements ActionListener, TableModel, ListSelectionListener, DocumentListener {
	
	private JButton btnNew;
	private JButton btnDelete;
	private JButton btnRename;
	private JButton btnTest;
	private JTable tbLLM;
	private JTextField taTest;
	private JTextArea txtSysPrompt;
	private JCheckBox cbThink;
	
	public LLMConfigPanel() {
		setLayout( new MigLayout( "fill, insets 5", "[grow]", "[][][grow 100][][grow 200][][][][][]" ) );
		add( btnNew = new JButton( "New" ), "cell 0 0, left" );
		add( btnDelete = new JButton( "Delete" ), "cell 0 0, left" );
		add( btnRename = new JButton( "Rename" ), "cell 0 0, left, wrap" );

		add( new JLabel( "Parameters" ), "wrap" );	
		add( new JScrollPane( tbLLM = new JTable( this ) ), "cell 0 2,span,grow, wrap" );

		add( new JLabel( "System prompt" ), "cell 0 3, wrap" );

		JScrollPane sp = null;
		add( sp = new JScrollPane( txtSysPrompt = new JTextArea() ), "cell 0 4, span, grow, wrap" );
		sp.setPreferredSize( new Dimension( 0, 300 ));
		
		txtSysPrompt.setRows( 3 );

		add( new JLabel( "Think mode (if available)" ), "cell 0 6" );
		add( cbThink = new JCheckBox( "True"), "cell 0 6,wrap" );

		add( new JLabel( "Test a prompt" ), "wrap" );	
		add( taTest = new JTextField() , "cell 0 7, growx, left" );
		add( btnTest = new JButton( "Test" ), "cell 0 7,left, wrap" );

		// URL
		tbLLM.getColumnModel().getColumn( 2 ).setCellEditor( new DefaultCellEditor( new JTextField() ) );
		// models
		tbLLM.getColumnModel().getColumn( 3 ).setCellEditor( new LLMModelTableCellEditor() );		
		
		tbLLM.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		JButton[] tmp = {
			btnNew,
			btnDelete,
			btnRename,
			btnTest
		};
		for ( JButton bt : tmp )
			bt.addActionListener( this );

		tbLLM.getSelectionModel().addListSelectionListener( this );
		txtSysPrompt.getDocument().addDocumentListener( this );
		cbThink.addActionListener( this );
		
		if ( tbLLM.getModel().getRowCount() > 0 ) {
			SwingUtilities.invokeLater( () -> tbLLM.getSelectionModel().setSelectionInterval( 0, 0 ) );
		}

	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		JButton[] tmp = {
				btnNew,
				btnDelete,
				btnRename,
				btnTest
			};
		for ( JButton bt : tmp )
			bt.removeActionListener( this );		
		
		tbLLM.getSelectionModel().removeListSelectionListener( this );
		txtSysPrompt.getDocument().removeDocumentListener( this );		
		cbThink.removeActionListener( this );
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}
	@Override
	public void insertUpdate(DocumentEvent e) {
		updateSystemPrompt();
	}
	@Override
	public void removeUpdate(DocumentEvent e) {
		updateSystemPrompt();
	}
	
	private void updateSystemPrompt() {
		if ( currentLLM != null ) {
			currentLLM.setProperty( LLM.SYSTEM_PROPERTY, txtSysPrompt.getText() );
		}
	}

	private LLM currentLLM;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int row = tbLLM.getSelectedRow();
		if ( row == -1 )
			return;

		currentLLM = LLMManager.instance().get( row );		
		txtSysPrompt.getDocument().removeDocumentListener( this );
		txtSysPrompt.setText( currentLLM.getProperty( LLM.SYSTEM_PROPERTY, "" ) );
		txtSysPrompt.getDocument().addDocumentListener( this );
		
		cbThink.removeActionListener( this );
		cbThink.setSelected( "true".equalsIgnoreCase( currentLLM.getProperty( LLM.THINK_PROPERTY, "false" ) ) );
		cbThink.addActionListener( this );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == btnNew ) {
			String newName = EditixFactory.buildAndShowInputDialog( "Choose a name ?" );
			if ( newName != null ) {
				if ( LLMManager.instance().indexOf( newName ) != -1 )
					EditixFactory.buildAndShowWarningDialog( "This name exists" );
				else {
					try {
						LLMManager.instance().newLLM( newName );
						l.tableChanged( new TableModelEvent( this ) );
						int row = tbLLM.getModel().getRowCount();
						tbLLM.getSelectionModel().setSelectionInterval( row - 1, row - 1 );
					} catch( Exception exc ) {
						EditixFactory.buildAndShowErrorDialog( "Can't add a new LLM ? [" + exc.getMessage() + "]" );
					}		
				}
			}
		} else
		if ( e.getSource() == btnDelete ) {
			int index = tbLLM.getSelectedRow();
			if ( index == -1 )
				EditixFactory.buildAndShowWarningDialog( "No selection ?" );
			else {
				LLMManager.instance().remove( index );
				l.tableChanged( new TableModelEvent( this ) );
				int row = tbLLM.getModel().getRowCount();
				tbLLM.getSelectionModel().setSelectionInterval( row - 1, row - 1 );				
			}
		} else
		if ( e.getSource() == btnRename ) {
			int index = tbLLM.getSelectedRow();
			if ( index == -1 )
				EditixFactory.buildAndShowWarningDialog( "No selection ?" );
			else {
				String currentName = LLMManager.instance().get( index ).getName();
				String newName = null;
				if ( ( newName = EditixFactory.buildAndShowInputDialog( "New name", currentName) ) != null ) {
					LLMManager.instance().renameAt( index, newName );
					l.tableChanged( new TableModelEvent( this ) );
				}
			}
		} else
		if ( e.getSource() == btnTest ) {
			int index = tbLLM.getSelectedRow();
			if ( index == -1 )
				EditixFactory.buildAndShowWarningDialog( "No selection ?" );
			else {
				LLM currentLLM = LLMManager.instance().get( index );
				new LLMRunner(currentLLM ).run( this, taTest.getText() );
			}
		} else
		if ( e.getSource() == cbThink ) {
			currentLLM.setProperty( LLM.THINK_PROPERTY, Boolean.toString( cbThink.isSelected() ).toLowerCase() );
		}

	}

	private TableModelListener l;
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		this.l = l;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		// Name
		// Type
		// URL
		// Model
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch ( columnIndex ) {
		case 0 : return "Name";
		case 1 : return "Type";
		case 2 : return "URL";
		case 3 : return "Model";
		}
		return null;
	}

	@Override
	public int getRowCount() {
		try {
			return LLMManager.instance().size();
		} catch( Exception exc ) {
			return 0;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LLM llm = LLMManager.instance().get( rowIndex );
		switch ( columnIndex ) {
			case 0 : return llm.getName();
			case 1 : return llm.getType();
			case 2 : return llm.getProperty( "url", "" );
			case 3 : return llm.getProperty( "model", "" );
		}			
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex >= 2;
	}
	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.l = null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			LLM llm = LLMManager.instance().get( rowIndex );
			if ( columnIndex == 2 )
				llm.setProperty( "url", aValue.toString() );
			if ( columnIndex == 3 )
				llm.setProperty( "model", aValue.toString() );
		} catch( Exception exc ) {
			
		}
	}

	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new LLMConfigPanel() );
		f.pack();
		f.setVisible( true );;
	}

}