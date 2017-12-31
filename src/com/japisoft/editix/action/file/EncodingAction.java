package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.japisoft.framework.app.toolkit.Toolkit;
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
			String _ = encoding[ i ];
			if ( _.equals( newEncoding ) ) {
				String old = encoding[ 0 ];
				encoding[ i ] = old;
				encoding[ 0 ] = newEncoding;
			}
		}

		Preferences.setRawPreference( "file", "rw-encoding", encoding );
	}
	
}
