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

package com.japisoft.editix.ui.windows;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.japisoft.editix.ui.EditixFactory;

public class ProcessDialog {

	private static ProcessDialog instance = null;

	public static ProcessDialog instance() {
		if ( instance == null )
			instance = new ProcessDialog();
		return instance;
	}

	private static Map<String,JDialog> processDialogs = null;
	private static Map<String,Boolean> processStopped = null;

	public static void updateProcessMessage( String id, Object message ) {
		JDialog dialog = processDialogs.get( id );
		if ( dialog != null ) {
			Component first = dialog.getContentPane().getComponent( 0 );
			if ( first instanceof JLabel ) {
				( ( JLabel )first ).setText( message.toString() );
			} else
			if ( first instanceof JScrollPane ) {
				( (JTextArea) ( ((JScrollPane)first).getViewport().getView() ) ).setText( message.toString() );
			}
		}
	}

	public static void buildAndShowProcessDialog( String id, Window owner, String title, Object message ) {
		
		SwingUtilities.invokeLater(
				() -> {
		
					if ( processDialogs != null && processDialogs.containsKey( id ) ) {
						processDialogs.get( id ).dispose();
					}
					if ( processStopped != null && processStopped.containsKey( id ) ) {
						processStopped.remove( id );
					}

					JDialog dialog = null;
					if ( owner == null || owner instanceof JFrame ) {
						dialog = new JDialog( (JFrame)( owner == null ? EditixFrame.THIS : owner ), title, true );
					}
					else {
						dialog = new JDialog( (JDialog)owner, title, true );
					}
					
					final JDialog dialogTmp = dialog;
					
					dialog.getRootPane().registerKeyboardAction(
						    e -> dialogTmp.dispose(),
						    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
						    JComponent.WHEN_IN_FOCUSED_WINDOW
						);					
					
					boolean pack = true;
					
					if ( message instanceof String ) {
						String strMessage = ( String )message;
						if ( strMessage.length() < 40 ) {
							JLabel b = new JLabel( strMessage );
							b.setBorder( new EmptyBorder( 10, 10, 10, 10 ));					
							dialog.add( b );
						} else {
							JTextArea area = new JTextArea(strMessage);
							area.setWrapStyleWord( true );
							area.setLineWrap( true );
							dialog.add( new JScrollPane( area ) );
							dialog.setSize( 500, 400 );
							pack = false;
						}
					} else
					if ( message instanceof JComponent ) {
						dialog.add( ( JComponent )message );
					} else {
						dialog.add( new JLabel( message.toString() ) );
					}

					dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
					if ( pack )
						dialog.pack();

					if ( owner == null )
						dialog.setLocationRelativeTo( EditixFrame.THIS );
					else
						dialog.setLocationRelativeTo( owner );
			
					if ( id != null ) {			
						if ( processDialogs == null )
							processDialogs = new HashMap<String,JDialog>();
						processDialogs.put( id, dialog );
						
						if ( processStopped == null )
							processStopped = new HashMap<String, Boolean>();
						processStopped.put( id, Boolean.FALSE );
					}
										
					
					dialog.setVisible( true );
					
					if ( id != null )
						stoppingProcess( id );

				} );
	}

	private static void stoppingProcess( String id ) {
		if ( processStopped != null && processStopped.containsKey( id ) && !processStopped.get( id ) ) {
			processStopped.put( id, Boolean.TRUE );
			EditixFactory.buildAndShowInformationDialog( "Stopping the current task..." );
		}
	}

	public static void hideProcessDialog( String id ) {
		if ( processDialogs != null ) {
			SwingUtilities.invokeLater(
					() -> {
						JDialog dialog = processDialogs.get( id );
						processDialogs.remove( id );
						processStopped.remove( id );						
						dialog.dispose();
					} );
		}
	}

	public static boolean isProcessStopped( String id ) {
		if ( processStopped != null )
			return processStopped.containsKey( id ) && processStopped.get( id );
		return false;
	}
	
}
