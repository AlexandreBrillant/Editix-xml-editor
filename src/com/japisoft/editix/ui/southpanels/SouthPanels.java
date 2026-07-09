// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
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
// 
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.ui.southpanels;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SouthPanels extends JTabbedPane implements ChangeListener {

	public SouthPanels() {
		super( JTabbedPane.LEFT );
	}

	SouthPanel[] panels; 
	
	@Override
	public void addNotify() {
		super.addNotify();
		initOnce();
		addChangeListener( this );
	}

	private boolean init = false;

	private void initOnce() {
		if ( !init ) {
			if ( panels == null ) {
				panels = new SouthPanel[] {
					new EditixOutput(),
					new EditixPrompter()
				};					
			}
			for ( SouthPanel panel : panels ) {
				addTab( panel.getTitle(), panel.getView() );
			}
		}
		init = true;
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		removeChangeListener( this );
	}

	public void deactivateAll() {
		if ( panels != null )
			for ( SouthPanel sp : panels )
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
