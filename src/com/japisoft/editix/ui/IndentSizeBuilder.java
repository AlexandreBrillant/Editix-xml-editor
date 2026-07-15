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

package com.japisoft.editix.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.japisoft.framework.descriptor.helpers.MenuBuilderDelegate;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.Encoding;

public class IndentSizeBuilder  implements MenuBuilderDelegate {

	public void build(JMenu menu) {
		
		menu.setEnabled( true );
		String[] encoding = Encoding.XML_ENCODINGS;
		ButtonGroup bg = new ButtonGroup();

		int tabSize = Preferences.getPreference( 
				"xmlconfig", 
				"format-space", 1 );

		for ( int i = 1; i < 6; i++ ) {
			ChangeIndentSizeAction action = new ChangeIndentSizeAction( i );
			JRadioButtonMenuItem item = new JRadioButtonMenuItem( action );
			if ( i == tabSize )
				item.setSelected( true );
			bg.add( item );
			menu.add( item );
		}
		
	}

	class ChangeIndentSizeAction extends AbstractAction {
		int size;
		ChangeIndentSizeAction( int size ) {
			this.size = size;
			putValue( Action.NAME, "" + size + " tabulation" + ( ( size > 1 ) ? "s":"" ) );
		}
		public void actionPerformed(ActionEvent e) {
			Preferences.setPreference( "xmlconfig", "format-space", size );
		}
	}	
	
}
