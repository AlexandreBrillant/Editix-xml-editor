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
