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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.japisoft.framework.ApplicationModel;

public class BottomPanels extends JTabbedPane implements ChangeListener, Action {

	public BottomPanels() {
		super( JTabbedPane.LEFT );
		setPreferredSize( new Dimension( 100, 200 ) );
	}

	BottomPanel[] panels; 
	
	@Override
	public void addNotify() {
		super.addNotify();
		initOnce();
		addChangeListener( this );

		getActionMap().put( "close.panels", this );		
		getInputMap( WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "close.panels" );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeChangeListener( this );

		getActionMap().remove( "close.panels" );
		getInputMap( WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).remove( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ) );
	}

	// Action

	@Override
	public void actionPerformed(ActionEvent e) {
		ApplicationModel.fireApplicationValue( "bottom.panels", false );
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
	}
	@Override
	public Object getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}@Override
	public boolean isEnabled() {
		return true;
	}
	@Override
	public void putValue(String key, Object value) {	
	}
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
	}

	///////////////////////////
	
	private boolean init = false;

	private void initOnce() {
		if ( !init ) {
			if ( panels == null ) {
				panels = new BottomPanel[] {
					new EditixOutput(),
					new EditixPrompter()
				};					
			}
			for ( BottomPanel panel : panels ) {
				addTab( panel.getTitle(), panel.getView() );
			}
		}
		init = true;
	}
	

	public void deactivateAll() {
		if ( panels != null )
			for ( BottomPanel sp : panels )
				sp.deactivate();
	}
	
	public void active( String title ) {
		for ( int i = 0; i < getTabCount(); i++ ) {
			if ( title.equalsIgnoreCase( getTitleAt( i ) ) ) {
				setSelectedIndex( i );
				panels[ i ].activate();
				return;
			}				
		}
		setSelectedIndex( 0 );
	}

	private int previousSelection = -1;
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if ( previousSelection != -1 ) {
			panels[ previousSelection ].deactivate();
		}
		previousSelection = getSelectedIndex();
		panels[previousSelection ].activate();
	}

}