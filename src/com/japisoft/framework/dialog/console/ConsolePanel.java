package com.japisoft.framework.dialog.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.japisoft.framework.ApplicationModel;
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
public class ConsolePanel extends JPanel  {
	private JTextArea ta;
	private static JTextArea VISIBLE_TEXTE = null;
	
	public ConsolePanel() {
		prepareUI();
	}

	public void addNotify() {
		super.addNotify();
		VISIBLE_TEXTE = ta;
		if ( bo != null ) {
			ta.setText( bo.toString() );
			try {
				ta.setCaretPosition( ta.getDocument().getLength() );
			} catch (RuntimeException e) {
			}
		}	
	}

	public void removeNotify() {
		super.removeNotify();
		VISIBLE_TEXTE = null;
	}

	private void prepareUI() {
		setLayout( new BorderLayout() );
		ta = new JTextArea();
		ta.setFont( new Font( "courier", Font.PLAIN, 12 ) );
		ta.setEditable( false );
		add( new JScrollPane( ta ) );		
		JToolBar tb = new JToolBar();
		tb.add( new CleanAction() );
		tb.add( new CopyAction() );
		add( tb, BorderLayout.SOUTH );
		setPreferredSize( new Dimension( 300, 400 ) );
	}

	/** Clean the console */
	class CleanAction extends AbstractAction {
		public CleanAction() {
			putValue( Action.NAME, "Clean" );
		}
		public void actionPerformed(ActionEvent e) {
			ta.setText( "" );
			bo = null;
		}
	}

	/** Copy the console content */
	class CopyAction extends AbstractAction {
		public CopyAction() {
			putValue( Action.NAME, "Copy" );
		}
		public void actionPerformed(ActionEvent e) {
			if ( ta.getSelectedText() == null )
				ta.selectAll();
			ta.copy();
		}
	}
	
	static StringBuffer bo = null;
	
	/** Size of the buffer, outside the buffer the data are lost */
	public static int CONSOLE_OUTPUT_MAX_BUFFER = 4096 * 2;

	private static PrintStream previousErrorState = null;
	private static PrintStream previousOutputState = null;

	/** Must be called for routing all the console message to the dialog content. Note that
	 * after calling this method you will not have output on the standard console */
	public static void initConsoleState() {
		ConsoleOutputStream o = new ConsoleOutputStream();
		PrintStream ps = new PrintStream( o );
		previousErrorState = System.err;
		previousOutputState = System.out;
		System.setErr( ps );
		System.setOut( ps );
	}

	/** Restore the default console state. Must be called when terminating using the console dialog */
	public static void restoreConsoleState() {
		if ( previousErrorState != null ) {
			System.setErr( previousErrorState );
			System.setOut( previousOutputState );
		}
	}

	static class ConsoleOutputStream extends OutputStream {
		public void write( int b ) throws IOException {
			if ( bo == null )
				bo = new StringBuffer();
			if( bo.length() > CONSOLE_OUTPUT_MAX_BUFFER ) {
				bo.deleteCharAt( 0 );
			}
			bo.append( ( char )b );
			
			if ( ApplicationModel.DEBUG_MODE ) {
				previousOutputState.write( b );
			}
			
			if ( VISIBLE_TEXTE != null )
				VISIBLE_TEXTE.setText( bo.toString() );
		}
	}

	public static void main( String[] args ) {
		JFrame f = new JFrame();
		f.getContentPane().add(
				new ConsolePanel() );
		ConsolePanel.initConsoleState();
		for ( int i = 0; i < 100; i++ ) {
			System.out.println( ">sdsdfsdfsfdsfdsfdsdfsdfdf" + i );
			System.err.println( ">>>>>>>>>>>>>>>" + i );
		}
		f.setSize( new Dimension( 400, 400 ) );
		f.setVisible( true );
	}

}
