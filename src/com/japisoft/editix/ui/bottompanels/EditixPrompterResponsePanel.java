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

package com.japisoft.editix.ui.bottompanels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

public class EditixPrompterResponsePanel extends JPanel implements ActionListener {

	private JTextArea txtResponse = new JTextArea();
	private JButton btCopy = null;
	private JButton btInsert = null;
	
	public EditixPrompterResponsePanel() {
		setLayout( new BorderLayout() );
		add( new JScrollPane( txtResponse = new JTextArea() ) );
		
		JToolBar tb;
		add( tb = new JToolBar(), BorderLayout.SOUTH );
		tb.add( btCopy = new JButton( "Copy" ) );
		tb.add( btInsert = new JButton( "Insert" ) );
		tb.setFloatable( false );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btCopy.addActionListener( this );
		btInsert.addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btCopy.removeActionListener( this );
		btInsert.removeActionListener( this );		
	}

	public void setText( String content ) {
		txtResponse.setText( content );
	}
	
	public void setTextFont( Font f ) {
		txtResponse.setFont( f );
	}
	
	public Font getTextFont() {
		return txtResponse.getFont();
	}

	public void requestTextFocus() {
		txtResponse.requestFocus();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btCopy ) {
			txtResponse.selectAll();
			txtResponse.copy();
			txtResponse.setCaretPosition( 0 );
		} else
		if ( e.getSource() == btInsert ) {
			String text = txtResponse.getText();
			XMLContainer container = EditixFrame.THIS.getSelectedContainer();
			if ( container == null )
				EditixFactory.buildAndShowWarningDialog( "No document ?" );
			else {
				container.insertText( text );
			}
		}
	}

}
