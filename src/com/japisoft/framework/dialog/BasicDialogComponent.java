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

package com.japisoft.framework.dialog;

import javax.swing.*;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.dialog.actions.CancelAction;
import java.awt.*;
import java.awt.event.*;

/** 
 * Basic dialog
 * <p>
 * This dialog box searches title, comment, ok, cancel values from a property bundle located
 * at the same classpath location than the dialog box terminated by '.properties'. Sample if
 * my dialog box is com/CommentDialog it will searches for com/CommentDialog.properties file.
 * </p>
 * <p>
 * Keys for the property file are TITLE, COMMENT, OK, CANCEL, ICON 
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 2.0 */
public class BasicDialogComponent 
		extends JDialog implements 
			WindowListener, DialogComponent, IdDialog {

	static {
		ApplicationMain.class.getName();
	}
	
	public BasicDialogComponent( String dialogTitle, boolean modalMode ) {
		super();
		setTitle( dialogTitle );
		setModal( modalMode );
		init();
	}
	
	public BasicDialogComponent(
		java.awt.Dialog owner, String dialogTitle, boolean modalMode ) {
		super( owner, dialogTitle );
		setModal( modalMode );
		init();
	}
	
	public BasicDialogComponent(
		Frame owner,
		String dialogTitle, boolean modalMode ) {
		super( owner, dialogTitle );
		setModal( modalMode );
		init();
	}
	
	public BasicDialogComponent(
			Frame owner,
			String dialogTitle ) {
		this( owner, dialogTitle, true );
	}

	private void init() {
		// Register a cancel action for the Escape key
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				setLastAction( DialogManager.getDefaultClosingAction() );
				setVisible( false );
			}
		},
		KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
		JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		getContentPane().setLayout(
				new BorderLayout( 0, 0 ) );
	}

	public void windowActivated(WindowEvent e) {
	}
	public void windowClosed(WindowEvent e) {
	}
	public void windowClosing(WindowEvent e) {
		setLastAction( CancelAction.ID );
	}
	public void windowDeactivated(WindowEvent e) {
	}
	public void windowDeiconified(WindowEvent e) {
	}
	public void windowIconified(WindowEvent e) {
	}
	public void windowOpened(WindowEvent e) {
	}

	public void addNotify() {
		super.addNotify();
		addWindowListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeWindowListener( this );
	}

	private DialogFooter footer = null;
	
	Dimension iniDim = null;

	public void setInitialSize( int width, int height ) {
		iniDim = new Dimension( width, height );
	}

	public DialogComponent init( 
			DialogHeader header, 
			JComponent content, 
			DialogFooter footer ) {

		if ( header != null )
			getContentPane().add( header.getView(), BorderLayout.NORTH );
		if ( content != null ) {			
			getContentPane().add( content, BorderLayout.CENTER );
			if  (iniDim != null )
				content.setPreferredSize( iniDim );
		}

		if ( footer != null ) {
			this.footer = footer;
			getContentPane().add( footer.getView(), BorderLayout.SOUTH );
		}
		
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( 
				( dim.width - getWidth() ) / 2,
				( dim.height - getHeight() ) / 2 );

		return this;
	}
	
	public void setSize( int width, int height, boolean center ) {
		super.setSize( width, height );
		if ( center ) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation( 
				( dim.width - getWidth() ) / 2,
				( dim.height - getHeight() ) / 2 
			);
		}
	}

	private int lastAction = CancelAction.ID;

	public void setLastAction( int lastActionId ) {
		this.lastAction = lastActionId;
		actionPerformed( lastActionId );
	}
	
	protected void actionPerformed( int actionId ) {}
	
	public int getLastAction() {
		return lastAction;
	}

	/** Invoke the following action from the footer part. A runtimeException
	 * may be thrown depending on the implementation for an unknown actionId
	 */
	public void invokeAction( int actionId ) {
		footer.invokeAction( actionId );
	}

	/** Called before the dialog is closed */
	protected void beforeClosing() {
		footer.dialogHidden();
	}	

	/** Called before the dialog is visible */
	protected void beforeShowing() {
		if ( footer != null )
			footer.dialogShown();
	}

	public void setVisible( boolean visible ) {
		if ( visible ) {
			beforeShowing();
		}// else
		 //	footer.dialogHidden();
		try {
			super.setVisible( visible );
		} catch( NullPointerException exc ) {
			System.err.println( exc.getMessage() );
		}
	}

	public void setVisible( boolean visible, int lastAction ) {
		this.lastAction = lastAction;
		setVisible( visible );
	}
		
	/**
	 * Enabled/Disabled an action 
	 * @param actionId a dialog action id
	 * @param enabled enabled or disable this action
	 */
	public void setEnabled( int actionId, boolean enabled ) {
		if ( footer != null )
			footer.setEnabled( actionId, enabled );
	}

	/** @return <code>true</code> if the last action was the 'OK' button */
	public boolean isOk() {
		return getLastAction() == DialogManager.OK_ID;
	}

	/** @return <code>true</code> if the last action was the 'CANCEL' button */
	public boolean isCancel() {
		return getLastAction() == DialogManager.CANCEL_ID;
	}

	/** Dispose all the inner resource */
	public void dispose() {
		if  (footer != null ) {
			beforeClosing();
			super.dispose();
			footer.dispose();
			footer = null;		
		}
	}

	public void dispose( int lastAction ) {
		this.lastAction = lastAction;
		setVisible( false );
		dispose();
	}
	
	public Component getView() {
		return this;
	}	

	public String getId() {
		return getTitle();
	}
	
	public static void main( String[] a ) {
		DialogManager.showDialog( null, "TEST", "TEST", "TEST", null, new JPanel() );
	}
	
}
