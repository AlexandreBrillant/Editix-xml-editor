// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
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

package com.japisoft.framework.dialog.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.japisoft.framework.ApplicationModel;

/**
 * Here a panel for having a kind of console like the shell console, thus
 * user can see any output message from the interface. You can control the
 * console buffer size with the CONSOLE_OUTPUT_MAX_BUFFER constant. You
 * must call the static method <code>initConsoleState</code> for having a console mode, thus
 * any System.out.println or System.err.println will work after.
 * Note that if the application model is in a debug mode then the output will be
 * also redirect to the standard console. 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class ConsolePanel extends JPanel implements ActionListener {
	private JTextArea ta;
	private static JTextArea VISIBLE_TEXTE = null;
	
	private ConsolePanel() {
		prepareUI();
		setPreferredSize( new Dimension( 500, 200 ) );
	}
	
	private static ConsolePanel instance = null;
	
	public static ConsolePanel instance() {
		if ( instance == null )
			instance = new ConsolePanel();
		if ( instance.getParent() != null ) {
			Container parent = instance.getParent();
			parent.remove( instance );
			parent.invalidate();
			parent.revalidate();
			parent.repaint();
		}
		return instance;
	}
	
	public ConsolePanel setText( String msg ) {
		bo = null;
		System.out.print( msg );
		return this;
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
		cbDebugMode.addActionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		VISIBLE_TEXTE = null;
		cbDebugMode.removeActionListener( this );
	}

	public void actionPerformed(ActionEvent e) {
		System.getProperty( "application.debug", Boolean.toString( cbDebugMode.isSelected() ).toLowerCase() );
		ApplicationModel.DEBUG_MODE = cbDebugMode.isSelected();
	}
	
	private JCheckBox cbDebugMode = null;
	
	private void prepareUI() {
		setLayout( new BorderLayout() );
		ta = new JTextArea();
		ta.setBackground( Color.BLACK );
		ta.setForeground( Color.WHITE );
		ta.setFont( new Font( "courier", Font.PLAIN, 12 ) );
		ta.setEditable( false );
		add( new JScrollPane( ta ) );		
		JToolBar tb = new JToolBar();
		tb.add( new CleanAction() );
		tb.add( new CopyAction() );
		
		tb.add( cbDebugMode = new JCheckBox( "Debug mode" ) );
		
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
		
		@Override
		public void flush() throws IOException {
			if ( bo != null )
				if ( VISIBLE_TEXTE != null )
					VISIBLE_TEXTE.setText( bo.toString() );
		}
		
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

