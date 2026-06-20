// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.ui.llm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;

import net.miginfocom.swing.MigLayout;

public class LLMConfigPanel extends JPanel implements ActionListener, TableModel {
	
	private JButton btnNew;
	private JButton btnDelete;
	private JButton btnRename;
	private JButton btnTest;
	
	public LLMConfigPanel() {
		setLayout( new MigLayout( "fill, grow" ) );
		add( btnNew = new JButton( "New" ) );
		add( btnDelete = new JButton( "Delete" ) );
		add( btnRename = new JButton( "Rename" ) );
		add( btnRename = new JButton( "Test" ), "wrap" );
		
		add( new JLabel( "Parameters" ), "swap" );	
		add( new JScrollPane( new JTable( this ) ), "grow, wrap" );
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
					} catch( Exception exc ) {
						EditixFactory.buildAndShowErrorDialog( "Can't add a new LLM ? [" + exc.getMessage() + "]" );
					}
						
				}
			}
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
		// System prompt
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch ( columnIndex ) {
		case 0 : return "Name";
		case 1 : return "Type";
		case 2 : return "URL";
		case 3 : return "System";
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
			case 3 : return llm.getProperty( "system", "" );
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
				llm.setProperty( "system", aValue.toString() );
		} catch( Exception exc ) {
			
		}
	}
	
}
