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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.Encoding;

/**
 * - "DEFAULT",
 * - "ASCII",
 * - "Cp1252",
 * - "ISO8859_1",
 * - "UnicodeBig",
 * - "UnicodeBigUnmarked",
 * - "UnicodeLittle",
 * - "UnicodeLittleUnmarked",
 * - "UTF8",
 * - "UTF-16"  
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class EncodingAction extends AbstractAction {
	
	public EncodingAction( String encoding ) {
		putValue( AbstractAction.NAME, encoding );
	}

	public void actionPerformed( ActionEvent e ) {		
		String[] encoding = Encoding.XML_ENCODINGS;
		String newEncoding = ( String )getValue( AbstractAction.NAME );
		
		if ( newEncoding.startsWith( "DEFAULT" ) ) {
			newEncoding = "DEFAULT";
		}

		for ( int i = 0; i < encoding.length; i++ ) {
			String __ = encoding[ i ];
			if ( __.equals( newEncoding ) ) {
				String old = encoding[ 0 ];
				encoding[ i ] = old;
				encoding[ 0 ] = newEncoding;
			}
		}

		Preferences.setRawPreference( "file", "rw-encoding", encoding );
	}
	
}

