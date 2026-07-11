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

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.japisoft.editix.action.file.EncodingAction;
import com.japisoft.framework.application.descriptor.helpers.MenuBuilderDelegate;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.Encoding;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class EncodingMenuBuilder implements MenuBuilderDelegate {

	public void build( JMenu menu ) {
		menu.setEnabled( true );
		String[] encoding = Encoding.XML_ENCODINGS;
		ButtonGroup bg = new ButtonGroup();
		for ( int i = 0; i < encoding.length; i++ ) {
			EncodingAction action = new EncodingAction( encoding[ i ] );
			JRadioButtonMenuItem item = new JRadioButtonMenuItem( action );
			if ( encoding[ i ].equals( 
					Preferences.getPreference( "file", "rw-encoding", encoding )[ 0 ] ) )
				item.setSelected( true );
			bg.add( item );
			menu.add( item );
			if ( "DEFAULT".equals( item.getText() ) ) {
				String currentEncoding = System.getProperty( "file.encoding" );
				if ( currentEncoding == null )
					currentEncoding = "unknown";
				item.setText( "DEFAULT (" + currentEncoding + ")" );
			}
		}
	}

	public static JComboBox encodingComboBox() {
		JComboBox combo = new JComboBox();
		String[] encoding = Encoding.XML_ENCODINGS;
		for ( int i = 0; i < encoding.length; i++ ) {
			combo.addItem( encoding[ i ] );
		}

		String tmp = 
			Preferences.getPreference( "file", "rw-encoding", encoding )[ 0 ];

		if ( "DEFAULT".equals( tmp ) ) {
			String currentEncoding = System.getProperty( "file.encoding" );
			if ( currentEncoding == null )
				currentEncoding = "unknown";
			tmp = "DEFAULT (" + currentEncoding + ")"; 
		}

		combo.setSelectedItem( tmp );
		return combo;
	}

}
