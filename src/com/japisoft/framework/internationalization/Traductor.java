package com.japisoft.framework.internationalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

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
public class Traductor {

	private HashMap traduction = null;
	
	private Traductor() {
		traduction = 
			new HashMap();
	}

	private static Traductor currentTraductor = null;
	
	public static void setTraductor( String language ) {
		currentTraductor = 
			new Traductor();
		currentTraductor.read( language.toLowerCase() );
	}

	public static String traduce( String code, String def ) {
		if ( currentTraductor == null || 
				code == null )
			return def;
		String tmp = ( String )currentTraductor.traduction.get( code );
		if ( tmp == null )
			return def;
		return tmp;
	}

	private void read( String language ) {
		URL u = 
			Traductor.class.getResource( 
				"traduction_" + language + ".txt" );
		if ( u == null ) {
			// Try to load the default one
			language = "en";
			u =
				Traductor.class.getResource( 
					"traduction_" + language + ".txt" );
		}
		if ( u != null ) {
			try {
				InputStream input = 
					u.openStream();
				BufferedReader br = 
					new BufferedReader( 
						new InputStreamReader( 
							input ) );
				try {
					String l = null;
					while ( ( l = br.readLine() ) != null ) {
						int i = l.lastIndexOf( "=" );
						if ( i > -1 ) {
							traduction.put( 
								l.substring( 0, i ),
								l.substring( i + 1 )
							);
						}
					}
				} finally {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
