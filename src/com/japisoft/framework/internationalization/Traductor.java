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

package com.japisoft.framework.internationalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

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

