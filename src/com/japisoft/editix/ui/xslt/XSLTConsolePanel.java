package com.japisoft.editix.ui.xslt;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

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

