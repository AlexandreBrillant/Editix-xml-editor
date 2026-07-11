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

/*
 * Created on 30 ao�t 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.japisoft.xmlpad.helper.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Basic popup menu.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class CommonPopup extends JPopupMenu implements ChangeListener {

	public CommonPopup( String title ) {
		super( title );	
		setBackground( Color.WHITE );
		setForeground( Color.BLACK );
		setBorderPainted( true );
	}
	
	protected JMenuItem getMenuItem() {
		JMenuItem item = new JMenuItem();
		item.setBackground( Color.WHITE );
		item.setFont( new Font( "arial", Font.PLAIN, 12 ) );
		item.addChangeListener( this );
		return item;
	}

	protected void firePopupMenuWillBecomeInvisible() {
		super.firePopupMenuWillBecomeInvisible();
		if ( lastSelection > -1 )
			notifySelection( lastSelection );	
		removeListeners();
	}
		
	private void removeListeners() {
		for ( int i = 0; i < getComponentCount(); i++ ) {
			if ( getComponent( i ) instanceof JMenuItem ) {
				( ( JMenuItem )getComponent( i ) ).removeChangeListener( this );
			}
		}
	}

	private int lastSelection = 0;

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		lastSelection = getComponentIndex( ( Component )e.getSource() );
	}

	protected void notifySelection( int selection ) {
	}

}
