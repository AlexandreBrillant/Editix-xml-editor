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

package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.preferences.Preferences;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class InformationDialog extends JDialog implements ActionListener {
	
	JButton btn;
	JCheckBox cb;
	String systemKey;

	public InformationDialog( JLabel content, String systemKey ) {
		super( EditixFrame.THIS );
		this.systemKey = systemKey;
		setModal( true );
		setDefaultLookAndFeelDecorated( false );
		setTitle( "Information" );
		getContentPane().add( content );
		
		JPanel panel2 = new JPanel();
		panel2.setLayout( new BorderLayout() );
		cb = new JCheckBox( "Show this dialog the next time", true );
		btn = new JButton( "OK" );
		panel2.add( BorderLayout.WEST, cb );
		panel2.add( BorderLayout.EAST, btn );
		
		getContentPane().add( BorderLayout.SOUTH, panel2 );
		
		pack();
		setSize( getWidth() + 20, getHeight() + 20 );
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( 
				( dim.width - getWidth() ) / 2, 
				( dim.height - getHeight() ) / 2 );
	}

	public void setVisible( boolean state ) {
		super.setVisible( state );
	}
	
	public void addNotify() {
		super.addNotify();
		btn.addActionListener( this );
		cb.addActionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		btn.removeActionListener( this );
		cb.removeActionListener( this );
	}

	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == btn )
			setVisible( false );
		else {
			Preferences.setRawPreference( "system", systemKey, new Boolean( cb.isSelected() ) );
			// Preferences.setRawPreference( "system", "taskdialog" + message, new Boolean( cb.isSelected() ) );
		}
	}
}