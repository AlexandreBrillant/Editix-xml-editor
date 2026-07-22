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

package com.japisoft.editix.ui.hexa;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import com.japisoft.editix.ui.EditixFactory;

public class HexaPanel extends JPanel implements ActionListener, ListSelectionListener {


    private JComboBox jComboBox1;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private HexaTable table;
    
	private JButton btnRepair = null;
	private JButton btnSearch = null;

	public HexaPanel( Document doc, int currentLine ) {
		initComponents();
		jScrollPane1.setViewportView( table = new HexaTable( doc, currentLine ) );
		jComboBox1.setModel( new DefaultComboBoxModel( new Object[] { HexaTableMode.HEX, HexaTableMode.INT, HexaTableMode.CHAR } ) );
	}

	private HexaListener hl;
	
	public void setHexaListener( HexaListener hl ) {
		this.hl = hl;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		jComboBox1.addActionListener( this );
		( ( JTable )jScrollPane1.getViewport().getView() ).getSelectionModel().addListSelectionListener( this );
		btnRepair.addActionListener( this );
		btnSearch.addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		jComboBox1.removeActionListener( this );
		( ( JTable )jScrollPane1.getViewport().getView() ).getSelectionModel().removeListSelectionListener( this );
		if ( hl != null )
			hl.dispose();
		hl = null;
		btnRepair.removeActionListener( this );
		btnSearch.removeActionListener( this );
	}

	public void valueChanged(ListSelectionEvent e) {
		if ( hl != null ) {
			int row = ( ( JTable )jScrollPane1.getViewport().getView() ).getSelectedRow();
			hl.selectedRow( row );
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btnSearch ) {
			String value = EditixFactory.buildAndShowInputDialog( "Choose a value" );
			if ( value != null ) {
				if ( !table.search( value ) ) {
					EditixFactory.buildAndShowWarningDialog( "Can't find this value" );
				}
			}
		} else
		if ( e.getSource() == btnRepair ) {			
			HexaTable ht = ( HexaTable )jScrollPane1.getViewport().getView();
			if ( ht.repair() ) {
				EditixFactory.buildAndShowInformationDialog( "Your document has been repaired" );
			} else {
				EditixFactory.buildAndShowWarningDialog( "Your document needn't to be repaired" );
			}
			
		} else {
			( ( HexaTable )jScrollPane1.getViewport().getView() ).setMode( ( HexaTableMode )jComboBox1.getSelectedItem() );
		}
	}
	
	private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        JToolBar tb = new JToolBar();
        tb.setFloatable( false );
        tb.add( jComboBox1 );
        tb.addSeparator();
        btnRepair = new JButton( "Repair XML" );
        tb.add( btnRepair ); 
        tb.add( btnSearch = new JButton( "Search" ) );
        jLabel1.setText("Mode");

		setLayout( new BorderLayout() );
		add( jScrollPane1, BorderLayout.CENTER );

		add( tb, BorderLayout.SOUTH );
    }

}
