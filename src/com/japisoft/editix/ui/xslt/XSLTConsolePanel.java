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

package com.japisoft.editix.ui.xslt;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class XSLTConsolePanel extends JPanel {

	private JTextArea ta = null;
	
	public XSLTConsolePanel() {
		setLayout( new BorderLayout() );
		add( new JScrollPane( ta = new JTextArea() ) );
		ta.setEditable( false );
	}

	public void setMessage( String msg ) {
		ta.setText( msg );
	}
	
	private PrintStream currentStream = null;
	private PrintStream currentStreamErr = null;	
	
	public void setEnabledConsole( boolean enabled ) {
		if ( enabled ) {
			ta.setText( "" );
			currentStream = System.out;
			currentStreamErr = System.err;
			PrintStream po = new PrintStream( new CustomOut() );
			System.setOut( po );
			System.setErr( po );
		} else {
			if ( currentStream != null ) {
				System.out.flush();
				System.err.flush();
				System.setOut( currentStream );
				System.setErr( currentStreamErr );
			}
		}		
	}

	class CustomOut extends OutputStream {
		
		@Override
		public void write(int b) throws IOException {
			int length = ta.getDocument().getLength();
			try {
				ta.getDocument().insertString( length, Character.toString( (char)b ), null );
			} catch( BadLocationException bex ) {
			}
		}

	}
	
}

